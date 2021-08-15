package com.github.glorindor.priconneserverbackend.clanbattle;

import com.github.glorindor.priconneserverbackend.character.CharacterDataService;
import com.github.glorindor.priconneserverbackend.entities.*;
import com.github.glorindor.priconneserverbackend.exceptions.InvalidRequestInputException;
import com.github.glorindor.priconneserverbackend.player.PlayerDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClanBattleBossServiceTest {
    @Mock
    private ClanBattleBossDataDao clanBattleBossDataDao;

    @Mock
    private PlayerDao playerDao;

    @Mock
    private CharacterDataService characterDataService;

    @InjectMocks
    private ClanBattleBossService clanBattleBossService;

    private final ClanBattleBossDamageId validId = new ClanBattleBossDamageId(1, 1, 1000);
    private final ClanBattleBossDamageId invalidPlayerId = new ClanBattleBossDamageId(1, 9, 9999);
    private final ClanBattleBossDamageId invalidBossId = new ClanBattleBossDamageId(9, 1, 9999);

    private Player player;
    private ClanBattleBossData clanBattleBossData;
    private ClanBattleBossDamage validBossDamage;

    CharacterData mifuyuData = new CharacterData(104801, "Mifuyu");
    CharacterData suzumeData = new CharacterData(107701, "Suzume(Summer)");
    CharacterData limaData = new CharacterData(105201, "Lima");
    CharacterData monikaData = new CharacterData(105301, "Monika");
    CharacterData ayumiData = new CharacterData(105501, "Ayumi");
    CharacterData pecorineData = new CharacterData(105801, "Pecorine");

    Team toBeChangedTeam = new Team(1, Set.of(mifuyuData, suzumeData, limaData, monikaData, ayumiData));
    Team placeHolderTeam = new Team(9, Set.of(pecorineData, mifuyuData, suzumeData, limaData, monikaData));

    @BeforeEach
    void init() {
        PlayerInfo playerInfo = new PlayerInfo("Test", "MEMBER", 99);
        player = new Player();
        player.update(playerInfo);

        clanBattleBossData = new ClanBattleBossData(1, 1, 1, null, null);
        clanBattleBossData.setRecommendedTeams(Set.of(toBeChangedTeam, placeHolderTeam));
        validBossDamage = new ClanBattleBossDamage(validId, clanBattleBossData, player);
    }

    // tests for createBossDamageFromId

    // when the player id is invalid
    @Test
    void testInvalidPlayerId() {
        when(clanBattleBossDataDao.findById(1)).thenReturn(Optional.ofNullable(clanBattleBossData));
        when(playerDao.findById(9)).thenReturn(Optional.empty());

        assertThatExceptionOfType(InvalidRequestInputException.class).isThrownBy(() -> {
            clanBattleBossService.createBossDamageFromId(invalidPlayerId);
        });
    }

    // when the battle id is invalid
    @Test
    void testInvalidBattleId() {
        when(clanBattleBossDataDao.findById(9)).thenReturn(Optional.empty());
        when(playerDao.findById(1)).thenReturn(Optional.ofNullable(player));

        assertThatExceptionOfType(InvalidRequestInputException.class).isThrownBy(() -> {
            clanBattleBossService.createBossDamageFromId(invalidBossId);
        });
    }

    // when everything looks good
    @Test
    void testValidId() {
        when(clanBattleBossDataDao.findById(1)).thenReturn(Optional.ofNullable(clanBattleBossData));
        when(playerDao.findById(1)).thenReturn(Optional.ofNullable(player));

        assertThat(clanBattleBossService.createBossDamageFromId(validId)).isEqualTo(validBossDamage);

        verify(clanBattleBossDataDao).findById(1);
        verify(playerDao).findById(1);
    }

    // tests for updateTeamFromRecommendedTeams

    @Test
    void testValidUpdateTeam() {
        Set<String> characterNameSet = Set.of("Lima", "Pecorine", "Ayumi", "Suzume", "Mifuyu");
        Set<CharacterData> characterDataSet = Set.of(pecorineData, mifuyuData, suzumeData, limaData, ayumiData);

        when(characterDataService.getCharacterDataSetFromNameSet(characterNameSet)).thenReturn(characterDataSet);

        clanBattleBossService.updateTeamFromRecommendedTeams(1, clanBattleBossData, characterNameSet);

        assertThat(clanBattleBossData.getRecommendedTeams()).isEqualTo(Set.of(
                new Team(1, characterDataSet),
                placeHolderTeam
        ));
    }

    @Test
    void testDifferentSizedCharacterSet() {
        Set<String> characterNameSet = Set.of("Lima", "Pecorine", "Ayumi", "Suzume");
        Set<CharacterData> characterDataSet = Set.of(pecorineData, ayumiData, suzumeData, limaData);

        when(characterDataService.getCharacterDataSetFromNameSet(characterNameSet)).thenReturn(characterDataSet);

        assertThatExceptionOfType(InvalidRequestInputException.class).isThrownBy(() -> {
            clanBattleBossService.updateTeamFromRecommendedTeams(1, clanBattleBossData, characterNameSet);
        });
    }
}
