package com.github.glorindor.priconneserverbackend.clanbattle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.glorindor.priconneserverbackend.character.CharacterDataService;
import com.github.glorindor.priconneserverbackend.entities.*;
import com.github.glorindor.priconneserverbackend.exceptions.InvalidRequestInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ClanBattleController.class)
public class ClanBattleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CharacterDataService characterDataService;

    @MockBean
    private ClanBattleBossService clanBattleBossService;

    @MockBean
    private ClanBattleBossDataDao clanBattleBossDataDao;

    @MockBean
    private ClanBattleBossDamageDao clanBattleBossDamageDao;

    private Player player;

    private ClanBattleBossDamage clanBattleBossDamageNumberOne;
    private ClanBattleBossDamage clanBattleBossDamageNumberTwo;

    CharacterData mifuyuData = new CharacterData(104801, "Mifuyu");
    CharacterData suzumeData = new CharacterData(107701, "Suzume(Summer)");
    CharacterData limaData = new CharacterData(105201, "Lima");
    CharacterData monikaData = new CharacterData(105301, "Monika");
    CharacterData ayumiData = new CharacterData(105501, "Ayumi");
    CharacterData pecorineData = new CharacterData(105801, "Pecorine");

    Set<String> firstTeamNameSet = Set.of("Mifuyu", "Suzume(Summer)", "Lima", "Monika", "Ayumi");
    Set<String> secondTeamNameSet = Set.of("Mifuyu", "Suzume", "Lima", "Monika", "Pecorine");

    Team firstTeam = new Team(1, new HashSet<>(Set.of(mifuyuData, suzumeData, limaData, monikaData, ayumiData)));
    Team secondTeam = new Team(9, new HashSet<>(Set.of(pecorineData, mifuyuData, suzumeData, limaData, monikaData)));

    private BossInfo validBossInfo = new BossInfo(10050001, 1);
    private BossInfo secondBossInfo = new BossInfo(10050002, 1);

    private ClanBattleBossData validClanBattleBossData;
    private ClanBattleBossData secondClanBattleBossData;
    private ClanBattleBossData teamClanBattleBossData = new ClanBattleBossData(7, 70007, 1, null, null);

    @BeforeEach
    void init() {
        PlayerInfo playerInfo = new PlayerInfo("Test", "MEMBER", 99);
        player = new Player();
        player.update(playerInfo);

        validClanBattleBossData = new ClanBattleBossData();
        validClanBattleBossData.update(validBossInfo);

        secondClanBattleBossData = new ClanBattleBossData();
        secondClanBattleBossData.setBattleId(2);
        secondClanBattleBossData.update(secondBossInfo);

        teamClanBattleBossData.setRecommendedTeams(new HashSet<>(Set.of(firstTeam)));

        ClanBattleBossDamageId clanBattleBossDamageId = new ClanBattleBossDamageId(1, 1, 999);
        ClanBattleBossDamageId clanBattleBossDamageIdNumberTwo = new ClanBattleBossDamageId(1, 1, 1000);
        clanBattleBossDamageNumberOne = new ClanBattleBossDamage(clanBattleBossDamageId, validClanBattleBossData, player);
        clanBattleBossDamageNumberTwo = new ClanBattleBossDamage(clanBattleBossDamageIdNumberTwo, validClanBattleBossData, player);
    }

    @Test
    void testValidPostOperation() throws Exception {
        mockMvc.perform(post("/clanbattle")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(validBossInfo)))
                .andExpect(status().isOk());

        verify(clanBattleBossDataDao).save(validClanBattleBossData);
    }

    @Test
    void testPostOperationInvalidBossId() throws Exception {
        BossInfo invalidBossInfo = new BossInfo(99999, 1);

        mockMvc.perform(post("/clanbattle")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidBossInfo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPostOperationInvalidDifficulty() throws Exception {
        BossInfo invalidBossInfo = new BossInfo(10000000, 5);

        mockMvc.perform(post("/clanbattle")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidBossInfo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPostOperationNull() throws Exception {
        mockMvc.perform(post("/clanbattle")
                .contentType("application/json")
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidGetOperation() throws Exception {
        Set<ClanBattleBossData> expectedResponseBody = Set.of(validClanBattleBossData, secondClanBattleBossData, teamClanBattleBossData);
        when(clanBattleBossDataDao.findAll()).thenReturn(expectedResponseBody);

        MvcResult mvcResult = mockMvc.perform(get("/clanbattle"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(expectedResponseBody).isEqualTo(
                objectMapper.readValue(actualResponseBody, new TypeReference<Set<ClanBattleBossData>>() {})
        );
    }

    @Test
    void testValidGetOperationById() throws Exception {
        when(clanBattleBossDataDao.findById(0)).thenReturn(Optional.ofNullable(validClanBattleBossData));

        MvcResult mvcResult = mockMvc.perform(get("/clanbattle/0"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(validClanBattleBossData)
        );
    }

    @Test
    void testGetOperationInvalidId() throws Exception {
        when(clanBattleBossDataDao.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/clanbattle/99"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidPutOperationById() throws Exception {
        when(clanBattleBossDataDao.findById(0)).thenReturn(Optional.ofNullable(validClanBattleBossData));

        mockMvc.perform(put("/clanbattle/0")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(secondClanBattleBossData)))
                .andExpect(status().isOk());
    }

    @Test
    void testPutOperationInvalidBossId() throws Exception {
        when(clanBattleBossDataDao.findById(0)).thenReturn(Optional.ofNullable(validClanBattleBossData));
        BossInfo invalidBossInfo = new BossInfo(99999, 1);

        mockMvc.perform(put("/clanbattle/0")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidBossInfo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPutOperationInvalidDifficulty() throws Exception {
        when(clanBattleBossDataDao.findById(0)).thenReturn(Optional.ofNullable(validClanBattleBossData));
        BossInfo invalidBossInfo = new BossInfo(10000000, 5);

        mockMvc.perform(put("/clanbattle/0")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidBossInfo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPutOperationInvalidId() throws Exception {
        when(clanBattleBossDataDao.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(put("/clanbattle/99")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(secondClanBattleBossData)))
                .andExpect(status().isBadRequest());

        verify(clanBattleBossDataDao, never()).save(secondClanBattleBossData);
    }

    @Test
    void testValidDeleteOperationById() throws Exception {
        when(clanBattleBossDataDao.findById(0)).thenReturn(Optional.ofNullable(validClanBattleBossData));

        mockMvc.perform(delete("/clanbattle/0"))
                .andExpect(status().isOk());

        verify(clanBattleBossDataDao).delete(validClanBattleBossData);
    }

    @Test
    void testDeleteOperationInvalidId() throws Exception {
        when(clanBattleBossDataDao.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/clanbattle/99"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidGetAllClanBattleDamages() throws Exception {
        Set<ClanBattleBossDamage> expectedResponseBody = Set.of(clanBattleBossDamageNumberOne, clanBattleBossDamageNumberTwo);
        when(clanBattleBossDamageDao.findByClanBattleBossDataBattleId(0)).thenReturn(expectedResponseBody);

        MvcResult mvcResult = mockMvc.perform(get("/clanbattle/0/damage"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(expectedResponseBody).isEqualTo(
                objectMapper.readValue(actualResponseBody, new TypeReference<Set<ClanBattleBossDamage>>() {})
        );
    }

    @Test
    void testGetAllClanBattleDamagesPlayerNotFound() throws Exception {
        when(clanBattleBossDamageDao.findByPlayerPlayerId(99)).thenReturn(null);

        mockMvc.perform(get("/clanbattle/99"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidPostTeamOperation() throws Exception {
        when(clanBattleBossDataDao.findById(0)).thenReturn(Optional.ofNullable(validClanBattleBossData));
        when(characterDataService.getCharacterDataSetFromNameSet(firstTeamNameSet)).thenReturn(firstTeam.getCharacterSet());

        mockMvc.perform(post("/clanbattle/0/team")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(firstTeamNameSet)))
                .andExpect(status().isOk());

        verify(clanBattleBossDataDao).save(validClanBattleBossData);
    }

    @Test
    void testPostTeamOperationCharacterNameSetNot5() throws Exception {
        mockMvc.perform(post("/clanbattle")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(Set.of("Mifuyu"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInvalidPostTeamOperation() throws Exception {
        mockMvc.perform(post("/clanbattle")
                .contentType("application/json")
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidGetTeamsOperation() throws Exception {
        Set<Team> expectedResponseBody = Set.of(firstTeam);
        when(clanBattleBossDataDao.findById(7)).thenReturn(Optional.ofNullable(teamClanBattleBossData));

        MvcResult mvcResult = mockMvc.perform(get("/clanbattle/7/team"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(expectedResponseBody).isEqualTo(
                objectMapper.readValue(actualResponseBody, new TypeReference<Set<Team>>() {})
        );
    }

    @Test
    void testGetTeamsOperationInvalidBattleId() throws Exception {
        when(clanBattleBossDataDao.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/clanbattle/99/team"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidPutTeamOperationById() throws Exception {
        when(clanBattleBossDataDao.findById(7)).thenReturn(Optional.ofNullable(teamClanBattleBossData));
        doAnswer(invocation -> {
            teamClanBattleBossData.getRecommendedTeams().add(secondTeam);
            return null;
                }).when(clanBattleBossService).updateTeamFromRecommendedTeams(1, teamClanBattleBossData, secondTeamNameSet);

        mockMvc.perform(put("/clanbattle/7/team/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(secondTeamNameSet)))
                .andExpect(status().isOk());

        assertThat(teamClanBattleBossData.getRecommendedTeams()).isEqualTo(Set.of(firstTeam, secondTeam));
    }

    @Test
    void testPutTeamOperationTeamSizeNot5() throws Exception {
        when(clanBattleBossDataDao.findById(7)).thenReturn(Optional.ofNullable(teamClanBattleBossData));
        doThrow(InvalidRequestInputException.class).when(clanBattleBossService).updateTeamFromRecommendedTeams(1, teamClanBattleBossData, Set.of("Mifuyu"));

        mockMvc.perform(put("/clanbattle/7/team/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(Set.of("Mifuyu"))))
                .andExpect(status().isBadRequest());

        teamClanBattleBossData.getRecommendedTeams().add(secondTeam);
        verify(clanBattleBossDataDao, never()).save(teamClanBattleBossData);
    }

    @Test
    void testPutTeamOperationInvalidTeamId() throws Exception {
        when(clanBattleBossDataDao.findById(7)).thenReturn(Optional.ofNullable(teamClanBattleBossData));
        // since the method is void, this is the preferred method of mocking
        doThrow(InvalidRequestInputException.class).when(clanBattleBossService).updateTeamFromRecommendedTeams(99, teamClanBattleBossData, secondTeamNameSet);

        mockMvc.perform(put("/clanbattle/7/team/99")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(secondTeamNameSet)))
                .andExpect(status().isBadRequest());

        teamClanBattleBossData.getRecommendedTeams().add(secondTeam);
        verify(clanBattleBossDataDao, never()).save(teamClanBattleBossData);
    }

    @Test
    void testPutTeamOperationInvalidBattleId() throws Exception {
        when(clanBattleBossDataDao.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(put("/clanbattle/99/team/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(secondTeamNameSet)))
                .andExpect(status().isBadRequest());

        teamClanBattleBossData.getRecommendedTeams().add(secondTeam);
        verify(clanBattleBossDataDao, never()).save(teamClanBattleBossData);
    }

    @Test
    void testValidDeleteTeamOperationById() throws Exception {
        when(clanBattleBossDataDao.findById(7)).thenReturn(Optional.ofNullable(teamClanBattleBossData));

        mockMvc.perform(delete("/clanbattle/7/team/1"))
                .andExpect(status().isOk());

        assertThat(teamClanBattleBossData.getRecommendedTeams()).isEqualTo(new HashSet<>());
    }

    @Test
    void testDeleteTeamOperationInvalidBattleId() throws Exception {
        when(clanBattleBossDataDao.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/clanbattle/99/team/1"))
                .andExpect(status().isBadRequest());
    }
}
