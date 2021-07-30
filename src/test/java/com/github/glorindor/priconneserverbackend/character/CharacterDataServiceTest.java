package com.github.glorindor.priconneserverbackend.character;

import com.github.glorindor.priconneserverbackend.entities.CharacterData;
import com.github.glorindor.priconneserverbackend.exceptions.InvalidRequestInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CharacterDataServiceTest {
    @Mock
    private CharacterDataDao characterDataDao;

    @InjectMocks
    private CharacterDataService characterDataService;

    CharacterData mifuyuData = new CharacterData(104801, "Mifuyu");
    CharacterData suzumeData = new CharacterData(107701, "Suzume(Summer)");

    @Test
    void testGetCharacterDataFromCharacterNameEmpty() {
        Set<CharacterData> resultSet = characterDataService.getCharacterDataSetFromNameSet(new HashSet<>());

        assertThat(resultSet).isEqualTo(new HashSet<>());
    }

    @Test
    void testGetCharacterDataFromCharacterNameInvalid() {
        // Set-up the expectation
        Mockito.when(characterDataDao.findByUnitName("invalid")).thenReturn(null);
        Set<String> inputSet = Set.of("invalid");

        assertThatExceptionOfType(InvalidRequestInputException.class).isThrownBy(() -> {
            characterDataService.getCharacterDataSetFromNameSet(inputSet);
        });
    }

    @Test
    void testGetCharacterDataFromCharacterNameValid() {
        // Set-up the expectation
        Mockito.when(characterDataDao.findByUnitName("Mifuyu")).thenReturn(mifuyuData);
        Mockito.when(characterDataDao.findByUnitName("Suzume(Summer)")).thenReturn(suzumeData);

        Set<String> inputSet = Set.of("Mifuyu", "Suzume(Summer)");

        Set<CharacterData> resultSet = characterDataService.getCharacterDataSetFromNameSet(inputSet);

        assertThat(resultSet).isEqualTo(Set.of(mifuyuData, suzumeData));
    }
}
