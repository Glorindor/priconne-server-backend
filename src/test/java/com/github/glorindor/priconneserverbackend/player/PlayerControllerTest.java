package com.github.glorindor.priconneserverbackend.player;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.glorindor.priconneserverbackend.character.CharacterDataService;
import com.github.glorindor.priconneserverbackend.clanbattle.ClanBattleBossDamageDao;
import com.github.glorindor.priconneserverbackend.clanbattle.ClanBattleBossDataDao;
import com.github.glorindor.priconneserverbackend.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PlayerController.class)
public class PlayerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CharacterDataService characterDataService;

    @MockBean
    private ClanBattleBossDamageDao clanBattleBossDamageDao;

    @MockBean
    private ClanBattleBossDataDao clanBattleBossDataDao;

    @MockBean
    private PlayerDao playerDao;

    CharacterData mifuyuData = new CharacterData(104801, "Mifuyu");
    CharacterData suzumeData = new CharacterData(107701, "Suzume(Summer)");
    CharacterData limaData = new CharacterData(105201, "Lima");
    CharacterData monikaData = new CharacterData(105301, "Monika");
    CharacterData ayumiData = new CharacterData(105501, "Ayumi");
    CharacterData pecorineData = new CharacterData(105801, "Pecorine");

    Team recommendedTeam = new Team(1, new HashSet<>(Set.of(mifuyuData, suzumeData, limaData, monikaData, ayumiData)));
    Team notHaveCharacterTeam = new Team(9, new HashSet<>(Set.of(pecorineData, mifuyuData, suzumeData, limaData, monikaData)));

    ClanBattleBossData clanBattleBossData = new ClanBattleBossData(1,10050001,1, null, null);

    ClanBattleBossDamageId clanBattleBossDamageIdNumberOne = new ClanBattleBossDamageId(1, 1, 999);
    ClanBattleBossDamageId clanBattleBossDamageIdNumberTwo = new ClanBattleBossDamageId(1, 1, 1200);

    ClanBattleBossDamage clanBattleBossDamageNumberOne;
    ClanBattleBossDamage clanBattleBossDamageNumberTwo;

    private PlayerInfo validPlayerInfo = new PlayerInfo("Test", "MEMBER", 99);
    private PlayerInfo leaderPlayerInfo = new PlayerInfo("Spok", "CLAN_LEADER", 98);

    private Player validPlayer;
    private Player leaderPlayer;

    @BeforeEach
    void init() {
        validPlayer = new Player();
        validPlayer.update(validPlayerInfo);
        Set<CharacterData> characterDataSet = new HashSet<>();
        characterDataSet.add(mifuyuData);
        characterDataSet.add(suzumeData);

        validPlayer.setUnownedCharacterSet(characterDataSet);

        leaderPlayer = new Player();
        leaderPlayer.update(leaderPlayerInfo);

        // can be unmutable since it won't every change
        Set<CharacterData> leaderCharacterDataSet = Set.of(mifuyuData, suzumeData, limaData, monikaData, ayumiData);
        leaderPlayer.setUnownedCharacterSet(leaderCharacterDataSet);

        clanBattleBossDamageNumberOne = new ClanBattleBossDamage(clanBattleBossDamageIdNumberOne, clanBattleBossData, validPlayer);
        clanBattleBossDamageNumberTwo = new ClanBattleBossDamage(clanBattleBossDamageIdNumberTwo, clanBattleBossData, validPlayer);

        clanBattleBossData.setRecommendedTeams(new HashSet<>(Set.of(recommendedTeam, notHaveCharacterTeam)));
    }

    @Test
    void testValidPostOperation() throws Exception {
        mockMvc.perform(post("/player")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(validPlayerInfo)))
                .andExpect(status().isOk());

        validPlayer.setUnownedCharacterSet(null);
        verify(playerDao).save(validPlayer);
    }

    @Test
    void testPostOperationBlankPlayerName() throws Exception {
        PlayerInfo invalidPlayerInfo = new PlayerInfo("", "MEMBER", 99);

        mockMvc.perform(post("/player")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidPlayerInfo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPostOperationInvalidRole() throws Exception {
        PlayerInfo invalidPlayerInfo = new PlayerInfo("Test", "Invalid", 99);

        mockMvc.perform(post("/player")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidPlayerInfo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPostOperationInvalidLvl() throws Exception {
        PlayerInfo invalidPlayerInfo = new PlayerInfo("Test", "MEMBER", -1);

        mockMvc.perform(post("/player")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidPlayerInfo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInvalidPostOperation() throws Exception {
        mockMvc.perform(post("/player")
                .contentType("application/json")
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidGetOperation() throws Exception {
        Set<Player> expectedResponseBody = Set.of(validPlayer, leaderPlayer);
        when(playerDao.findAll()).thenReturn(expectedResponseBody);

        MvcResult mvcResult = mockMvc.perform(get("/player"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(expectedResponseBody).isEqualTo(
                objectMapper.readValue(actualResponseBody, new TypeReference<Set<Player>>() {})
        );
    }

    @Test
    void testValidGetOperationById() throws Exception {
        when(playerDao.findById(1)).thenReturn(Optional.ofNullable(validPlayer));

        MvcResult mvcResult = mockMvc.perform(get("/player/1"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(validPlayer)
        );
    }

    @Test
    void testGetOperationInvalidId() throws Exception {
        when(playerDao.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/player/99"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidPutOperationById() throws Exception {
        when(playerDao.findById(1)).thenReturn(Optional.ofNullable(validPlayer));

        mockMvc.perform(put("/player/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(leaderPlayerInfo)))
                .andExpect(status().isOk());
    }

    @Test
    void testPutOperationBlankPlayerName() throws Exception {
        when(playerDao.findById(1)).thenReturn(Optional.ofNullable(validPlayer));
        PlayerInfo invalidPlayerInfo = new PlayerInfo("", "MEMBER", 99);

        mockMvc.perform(put("/player/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidPlayerInfo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPutOperationInvalidRole() throws Exception {
        when(playerDao.findById(1)).thenReturn(Optional.ofNullable(validPlayer));
        PlayerInfo invalidPlayerInfo = new PlayerInfo("Test", "Invalid", 99);

        mockMvc.perform(put("/player/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidPlayerInfo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPutOperationInvalidLvl() throws Exception {
        when(playerDao.findById(1)).thenReturn(Optional.ofNullable(validPlayer));
        PlayerInfo invalidPlayerInfo = new PlayerInfo("Test", "MEMBER", -1);

        mockMvc.perform(put("/player/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidPlayerInfo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPutOperationInvalidId() throws Exception {
        when(playerDao.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(put("/player/99")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(leaderPlayerInfo)))
                .andExpect(status().isBadRequest());

        verify(playerDao, never()).save(leaderPlayer);
    }

    @Test
    void testValidDeleteOperationById() throws Exception {
        when(playerDao.findById(1)).thenReturn(Optional.ofNullable(validPlayer));

        mockMvc.perform(delete("/player/1"))
                .andExpect(status().isOk());

        verify(playerDao).delete(validPlayer);
    }

    @Test
    void testDeleteOperationInvalidId() throws Exception {
        when(playerDao.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/player/99"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidGetUnownedCharacterSet() throws Exception {
        when(playerDao.findById(1)).thenReturn(Optional.ofNullable(validPlayer));
        Set<CharacterData> expectedResponseBody = Set.of(mifuyuData, suzumeData);

        MvcResult mvcResult = mockMvc.perform(get("/player/1/character"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(expectedResponseBody).isEqualTo(
                objectMapper.readValue(actualResponseBody, new TypeReference<Set<CharacterData>>() {})
        );
    }

    @Test
    void testGetUnownedCharacterSetInvalidId() throws Exception {
        when(playerDao.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/player/99/character"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidPostUnownedCharacterSet() throws Exception {
        when(playerDao.findById(1)).thenReturn(Optional.ofNullable(validPlayer));

        Set<String> characterNameSet = Set.of("Lima", "Pecorine");
        Set<CharacterData> characterDataSet = Set.of(limaData, pecorineData);
        when(characterDataService.getCharacterDataSetFromNameSet(characterNameSet)).thenReturn(characterDataSet);

        mockMvc.perform(post("/player/1/character")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(characterNameSet)))
                .andExpect(status().isOk());

        assertThat(validPlayer.getUnownedCharacterSet()).isEqualTo(Set.of(limaData, pecorineData, mifuyuData, suzumeData));
    }

    @Test
    void testPostUnownedCharacterSetInvalidId() throws Exception {
        when(playerDao.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(post("/player/99/character")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(Set.of("Mifuyu, Suzume"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPostUnownedCharacterSetEmptySet() throws Exception {
        when(playerDao.findById(1)).thenReturn(Optional.ofNullable(validPlayer));

        mockMvc.perform(post("/player/1/character")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(new HashSet<String>())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPostUnownedCharacterSetAlreadyContainsCharacters() throws Exception {
        when(playerDao.findById(1)).thenReturn(Optional.ofNullable(validPlayer));

        Set<String> characterNameSet = Set.of("Mifuyu", "Suzume", "Lima");
        Set<CharacterData> characterDataSet = Set.of(mifuyuData, suzumeData, limaData);
        when(characterDataService.getCharacterDataSetFromNameSet(characterNameSet)).thenReturn(characterDataSet);

        // technically it should give empty set
        mockMvc.perform(post("/player/1/character")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(characterNameSet)))
                .andExpect(status().isOk());

        assertThat(validPlayer.getUnownedCharacterSet()).isEqualTo(Set.of(limaData));
    }

    @Test
    void testValidGetAllClanBattleDamages() throws Exception {
        Set<ClanBattleBossDamage> expectedResponseBody = Set.of(clanBattleBossDamageNumberOne, clanBattleBossDamageNumberTwo);
        when(clanBattleBossDamageDao.findByPlayerPlayerId(1)).thenReturn(expectedResponseBody);

        MvcResult mvcResult = mockMvc.perform(get("/player/1/damage"))
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

        mockMvc.perform(get("/player/99"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetRecommendedTeams() throws Exception {
        when(playerDao.findById(7)).thenReturn(Optional.ofNullable(leaderPlayer));
        when(clanBattleBossDataDao.findById(1)).thenReturn(Optional.ofNullable(clanBattleBossData));

        MvcResult mvcResult = mockMvc.perform(get("/player/7/team?battleId=1"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        assertThat(Set.of(recommendedTeam)).isEqualTo(
                objectMapper.readValue(actualResponseBody, new TypeReference<Set<Team>>() {})
        );
    }

    @Test
    void testGetRecommendedTeamsInvalidPlayerId() throws Exception {
        when(playerDao.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/player/99/team?battleId=1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetRecommendedTeamsInvalidBossId() throws Exception {
        when(playerDao.findById(1)).thenReturn(Optional.ofNullable(validPlayer));
        when(clanBattleBossDataDao.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/player/1/team?battleId=99"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetRecommendedTeamsBossDoesntHaveRecommendedTeams() throws Exception {
        clanBattleBossData.setRecommendedTeams(null); // set it to null for testing

        when(playerDao.findById(1)).thenReturn(Optional.ofNullable(validPlayer));
        when(clanBattleBossDataDao.findById(1)).thenReturn(Optional.ofNullable(clanBattleBossData));

        mockMvc.perform(get("/player/1/team?battleId=1"))
                .andExpect(status().isBadRequest());
    }
}