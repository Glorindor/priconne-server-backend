package com.github.glorindor.priconneserverbackend.character;

import com.github.glorindor.priconneserverbackend.entities.CharacterData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/character")
@RequiredArgsConstructor
public class CharacterDataController {
    private final CharacterDataDao characterDataDao;

    /**
     * Endpoint to returning all characters' data.
     * @return a {@link Set<CharacterData>} containing information.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Set<CharacterData> getAllCharacters() {
        Iterable<CharacterData> characterDataIterable = characterDataDao.findAll();
        Set<CharacterData> characterDataSet = new HashSet<>();

        characterDataIterable.forEach(characterDataSet::add);

        return characterDataSet;
    }
}
