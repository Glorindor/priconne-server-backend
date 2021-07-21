package com.github.glorindor.priconneserverbackend.player;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.glorindor.priconneserverbackend.character.CharacterDataRepository;
import com.github.glorindor.priconneserverbackend.entities.CharacterData;
import com.github.glorindor.priconneserverbackend.entities.NewPlayer;
import com.github.glorindor.priconneserverbackend.entities.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/player")
public class PlayerController {
    private final CharacterDataRepository characterDataRepository;
    private final PlayerRepository playerRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Endpoint to reaching a player.
     * @param id id that specifies the player.
     * @return a {@link Player} about the specified player.
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Player getPlayerById(@PathVariable("id") int id) {
        Optional<Player> player = playerRepository.findById(id);
        return player.get();
    }

    /**
     * Endpoint to adding a new player to the clan.
     * @param newPlayer a {@link NewPlayer} that contains Name, Lvl, Role information.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void createPlayer(@RequestBody NewPlayer newPlayer) {
        playerRepository.save(new Player(newPlayer));
    }

    @GetMapping("/{id}/character")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Set<CharacterData> getUnownedCharacters(@PathVariable("id") int id) {
        Optional<Player> player = playerRepository.findById(id);
        return player.get().getUnownedCharacterSet();
    }

    @PostMapping("/{id}/character")
    public void addUnownedCharacter(@PathVariable("id") int id,
                                    @RequestBody Set<CharacterData> unownedCharacterSet) {

        Optional<Player> player = playerRepository.findById(id);
        player.get().setUnownedCharacterSet(unownedCharacterSet);
        playerRepository.save(player.get());
    }
}
