package com.github.glorindor.priconneserverbackend.clanbattle;

import com.github.glorindor.priconneserverbackend.character.CharacterDataService;
import com.github.glorindor.priconneserverbackend.entities.*;
import com.github.glorindor.priconneserverbackend.exceptions.InvalidRequestInputException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("clanbattle")
@RequiredArgsConstructor
public class ClanBattleController {
    private final CharacterDataService characterDataService;

    private final ClanBattleBossDataDao clanBattleBossDataDao;
    private final ClanBattleBossDamageDao clanBattleBossDamageDao;

    /**
     * Endpoint to adding a new boss to the clan battle.
     * @param bossInfo a {@link BossInfo} that contains id and difficulty info
     */
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void createBoss(@RequestBody BossInfo bossInfo) {
        ClanBattleBossData clanBattleBossData = new ClanBattleBossData();
        clanBattleBossData.update(bossInfo);
        clanBattleBossDataDao.save(clanBattleBossData);
    }

    /**
     * Endpoint to getting all bosses/battles in clan battle.
     * @return a {@link Set<ClanBattleBossData>} containing all boss battle info.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Set<ClanBattleBossData> getAllBosses() {
        Iterable<ClanBattleBossData> clanBattleBossDataIterable = clanBattleBossDataDao.findAll();
        Set<ClanBattleBossData> clanBattleBossDataSet = new HashSet<>();

        clanBattleBossDataIterable.forEach(clanBattleBossDataSet::add);

        return clanBattleBossDataSet;
    }

    /**
     * Endpoint to reaching to a boss battle.
     * @param battleId id that specifies the battle
     * @return a {@link ClanBattleBossData} about the specified battle.
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody ClanBattleBossData getBossData(@PathVariable("id") int battleId) {
        Optional<ClanBattleBossData> bossData = clanBattleBossDataDao.findById(battleId);

        if (bossData.isEmpty()) {
            throw new InvalidRequestInputException();
        }

        return bossData.get();
    }

    /**
     * Endpoint to updating a battle info.
     * @param battleId id that specifies the battle
     * @param bossInfo a {@link BossInfo} that contains id and difficulty info
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateBossData(@PathVariable("id") int battleId, @RequestBody BossInfo bossInfo) {
        Optional<ClanBattleBossData> bossData = clanBattleBossDataDao.findById(battleId);

        if (bossData.isEmpty()) {
            throw new InvalidRequestInputException();
        }

        bossData.get().update(bossInfo);
        clanBattleBossDataDao.save(bossData.get());
    }

    /**
     * Endpoint to deleting a battle from the clan battle.
     * @param battleId id that specifies the battle.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteBossData(@PathVariable("id") int battleId) {
        Optional<ClanBattleBossData> bossData = clanBattleBossDataDao.findById(battleId);

        if (bossData.isEmpty()) {
            throw new InvalidRequestInputException();
        }

        clanBattleBossDataDao.delete(bossData.get());
    }

    /**
     * Endpoint to reaching all damage dealt by players to a specified boss battle.
     * @param battleId id that specifies the battle
     * @return a {@link Set<ClanBattleBossData>} containing all damage data associated with the boss battle.
     */
    @GetMapping("/{id}/damage")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Set<ClanBattleBossDamage> getBossDamage(@PathVariable("id") int battleId) {
        Set<ClanBattleBossDamage> clanBattleBossDamageSet = clanBattleBossDamageDao.findByClanBattleBossDataBattleId(battleId);

        if (clanBattleBossDamageSet == null) {
            throw new InvalidRequestInputException();
        }

        return clanBattleBossDamageSet;
    }

    /**
     * Endpoint to adding a recommended team to a specified boss battle.
     * @param battleId id that specifies the battle
     * @param characterNameSet a {@link Set<String>} containing character names to be put in a team.
     */
    @PostMapping("/{id}/team")
    @ResponseStatus(HttpStatus.OK)
    public void addTeam(@PathVariable("id") int battleId, @RequestBody Set<String> characterNameSet) {
        Optional<ClanBattleBossData> clanBattleBossData = clanBattleBossDataDao.findById(battleId);

        if (clanBattleBossData.isEmpty() || characterNameSet.size() != 5) {
            throw new InvalidRequestInputException();
        }

        Set<CharacterData> characterDataSet = characterDataService.getCharacterDataSetFromNameSet(characterNameSet);

        clanBattleBossData.get().getRecommendedTeams().add(new Team(characterDataSet));
    }

    /**
     * Endpoint to reaching all recommended teams associated with a boss battle.
     * @param battleId id that specifies the battle
     * @return a {@link Set<Team>} containing the recommended teams.
     */
    @GetMapping("/{id}/team")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Set<Team> getTeams(@PathVariable("id") int battleId) {
        Optional<ClanBattleBossData> clanBattleBossData = clanBattleBossDataDao.findById(battleId);

        if (clanBattleBossData.isEmpty()) {
            throw new InvalidRequestInputException();
        }

        return clanBattleBossData.get().getRecommendedTeams();
    }

    /**
     * Endpoint to updating a teams character set.
     * @param battleId id that specifies the battle.
     * @param teamId id that specifies the team.
     * @param characterNameSet a {@link Set<String>} containing character names to be put in a team.
     */
    @PutMapping("/{id}/team/{team_id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateTeam(@PathVariable("id") int battleId, @PathVariable("team_id") int teamId,
                           @RequestBody Set<String> characterNameSet) {
        Optional<ClanBattleBossData> clanBattleBossData = clanBattleBossDataDao.findById(battleId);

        if (clanBattleBossData.isEmpty()) {
            throw new InvalidRequestInputException();
        }

        Set<CharacterData> characterDataSet = characterDataService.getCharacterDataSetFromNameSet(characterNameSet);

        for (Team team : clanBattleBossData.get().getRecommendedTeams()) {
            if (team.getTeamId() == teamId) {
                team.setCharacterSet(characterDataSet);
            }
        }
    }

    /**
     * Endpoint to deleting a team from recommended team set.
     * @param battleId id that specifies the battle
     * @param teamId id that specifies the team
     */
    @DeleteMapping("/{id}/team/{team_id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTeam(@PathVariable("id") int battleId, @PathVariable("team_id") int teamId) {
        Optional<ClanBattleBossData> clanBattleBossData = clanBattleBossDataDao.findById(battleId);

        if (clanBattleBossData.isEmpty()) {
            throw new InvalidRequestInputException();
        }

        // remove the specified team
        clanBattleBossData.get().getRecommendedTeams().removeIf(team -> team.getTeamId() == teamId);
    }

    /**
     * Endpoint to terminating the current battle and resets the associating services to default for next clan battle.
     */
    @DeleteMapping("/nuke")
    @ResponseStatus(HttpStatus.OK)
    public void terminateClanBattle() {
        //TODO: add information to reset the entire database
    }
}
