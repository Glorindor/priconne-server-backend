package com.github.glorindor.priconneserverbackend.character;

import com.github.glorindor.priconneserverbackend.entities.CharacterData;
import com.github.glorindor.priconneserverbackend.exceptions.InvalidRequestInputException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CharacterDataService {
    private final CharacterDataDao characterDataDao;

    /**
     * Given a set containing names, returns associated CharacterData.
     * @param nameSet a Set containing character names
     * @return {@link Set<CharacterData>} containing associated data.
     */
    public Set<CharacterData> getCharacterDataSetFromNameSet(Set<String> nameSet) {
        Set<CharacterData> characterDataSet = new HashSet<>();

        for (String name : nameSet) {
            CharacterData characterData = characterDataDao.findByUnitName(name);

            if (characterData == null) {
                throw new InvalidRequestInputException();
            }
            characterDataSet.add(characterData);
        }

        return characterDataSet;
    }
}
