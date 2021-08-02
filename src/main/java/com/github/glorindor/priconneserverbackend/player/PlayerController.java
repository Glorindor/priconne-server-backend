package com.github.glorindor.priconneserverbackend.player;

import com.github.glorindor.priconneserverbackend.character.CharacterDataService;
import com.github.glorindor.priconneserverbackend.clanbattle.ClanBattleBossDamageDao;
import com.github.glorindor.priconneserverbackend.clanbattle.ClanBattleBossDataDao;
import com.github.glorindor.priconneserverbackend.entities.*;
import com.github.glorindor.priconneserverbackend.exceptions.InvalidRequestInputException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/player")
@RequiredArgsConstructor
public class PlayerController {
    private final CharacterDataService characterDataService;

    private final ClanBattleBossDamageDao clanBattleBossDamageDao;
    private final ClanBattleBossDataDao clanBattleBossDataDao;
    private final PlayerDao playerDao;

    /**
     * Endpoint to adding a new player to the clan.
     * @param playerBasicInfo a {@link PlayerInfo} that contains Name, Lvl, Role information.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Player createPlayer(@RequestBody PlayerInfo playerBasicInfo) {
        Player player = new Player();
        player.update(playerBasicInfo);
        return playerDao.save(player);
    }

    /**
     * Endpoint to getting all players within the clan.
     * @return a {@link Set<Player>} containing all players.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Set<Player> getAllPlayers() {
        Iterable<Player> playerIterable = playerDao.findAll();
        Set<Player> playerSet = new HashSet<>();

        playerIterable.forEach(playerSet::add);

        return playerSet;
    }

    /**
     * Endpoint to reaching a player.
     * @param playerId id that specifies the player.
     * @return a {@link Player} about the specified player.
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Player getPlayerById(@PathVariable("id") int playerId) {
        Optional<Player> player = playerDao.findById(playerId);

        if (player.isEmpty()) {
            throw new InvalidRequestInputException();
        }

        return player.get();
    }

    /**
     * Endpoint to updating a player to the clan.
     * @param playerId id that specifies the player.
     * @param playerBasicInfo a {@link PlayerInfo} that contains Name, Lvl, Role information.
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Player updatePlayer(@PathVariable("id") int playerId,
                                             @RequestBody PlayerInfo playerBasicInfo) {
        // retrieve player so that the id stays the same
        Optional<Player> player = playerDao.findById(playerId);

        if (player.isEmpty()) {
            throw new InvalidRequestInputException();
        }

        player.get().update(playerBasicInfo);
        return playerDao.save(player.get());
    }

    /**
     * Endpoint to removing a player from the clan.
     * @param playerId id that specifies the player.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePlayer(@PathVariable("id") int playerId) {
        Optional<Player> player = playerDao.findById(playerId);

        if (player.isEmpty()) {
            throw new InvalidRequestInputException();
        }

        playerDao.delete(player.get());
    }

    /**
     * Endpoint to reaching unowned characters of a player.
     * @param playerId id that specifies the player
     * @return a {@link Set<com.github.glorindor.priconneserverbackend.entities.CharacterData>} about the specified player.
     */
    @GetMapping("/{id}/character")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Set<CharacterData> getUnownedCharacters(@PathVariable("id") int playerId) {
        Optional<Player> player = playerDao.findById(playerId);

        if (player.isEmpty()) {
            throw new InvalidRequestInputException();
        }

        return player.get().getUnownedCharacterSet();
    }

    /**
     * Endpoint to updating or creating the unowned character set of a player.
     * If the character is already within the set, then the character will be removed from the set.
     * @param playerId id that specifies the player
     * @param unownedCharacterNames a {@link Set<String>} containing names of characters.
     */
    @PostMapping("/{id}/character")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Player addUnownedCharacter(@PathVariable("id") int playerId,
                                                    @RequestBody Set<String> unownedCharacterNames) {
        Optional<Player> player = playerDao.findById(playerId);

        if (player.isEmpty()) {
            throw new InvalidRequestInputException();
        }

        Set<CharacterData> unownedCharacterSet = characterDataService.getCharacterDataSetFromNameSet(unownedCharacterNames);
        Set<CharacterData> playerUnownedCharacterSet = player.get().getUnownedCharacterSet();

        // update the set
        for (CharacterData data : unownedCharacterSet) {
            if (playerUnownedCharacterSet.contains(data)) { // if already present, remove
                playerUnownedCharacterSet.remove(data);
            } else { // if not present add
                playerUnownedCharacterSet.add(data);
            }
        }
        return playerDao.save(player.get());
    }

    /**
     * Endpoint to reaching all damage dealt by specified player to all bosses.
     * @param playerId id that specifies the player
     * @return a {@link Set<ClanBattleBossData>} containing all damage data associated with the player.
     */
    @GetMapping("/{id}/damage")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Set<ClanBattleBossDamage> getBossDamage(@PathVariable("id") int playerId) {
        Set<ClanBattleBossDamage> clanBattleBossDamageSet = clanBattleBossDamageDao.findByPlayerPlayerId(playerId);

        if (clanBattleBossDamageSet == null) {
            throw new InvalidRequestInputException();
        }

        return clanBattleBossDamageSet;
    }

    /**
     * Endpoint to getting all recommended teams a player can make.
     * @param playerId id that specifies the player
     * @param battleId id that specifies the battle
     * @return a {@link Set<Team>} containing all recommended teams a player can make for the specifies battle.
     */
    @GetMapping("/{id}/team")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Set<Team> getRecommendedTeams(@PathVariable("id") int playerId,
                                                       @RequestBody int battleId) {
        Optional<Player> player = playerDao.findById(playerId);
        Optional<ClanBattleBossData> clanBattleBossData = clanBattleBossDataDao.findById(battleId);

        if (player.isEmpty() || clanBattleBossData.isEmpty()) {
            throw new InvalidRequestInputException();
        }

        Set<Team> recommendedTeams = clanBattleBossData.get().getRecommendedTeams();
        Set<CharacterData> unownedCharacterSet = player.get().getUnownedCharacterSet();

        // if the set has changed then remove it because the player doesn't own a character in that team
        recommendedTeams.removeIf(t -> t.getCharacterSet().retainAll(unownedCharacterSet));

        return recommendedTeams;
    }
}
