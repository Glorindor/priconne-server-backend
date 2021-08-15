package com.github.glorindor.priconneserverbackend.clanbattle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ClanBattleDamageController.class)
public class ClanBattleDamageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClanBattleBossService clanBattleBossService;

    @MockBean
    private ClanBattleBossDamageDao clanBattleBossDamageDao;

    private Player player;
    private ClanBattleBossData clanBattleBossData;

    private ClanBattleBossDamage validBossDamage;
    private ClanBattleBossDamage secondBossDamage;

    private ClanBattleBossDamageId validId = new ClanBattleBossDamageId(1, 1, 999);
    private ClanBattleBossDamageId secondId = new ClanBattleBossDamageId(1, 1, 1000);
    private ClanBattleBossDamageId invalidId = new ClanBattleBossDamageId(9, 9, 999999);

    @BeforeEach
    void init() {
        PlayerInfo playerInfo = new PlayerInfo("Test", "MEMBER", 99);
        player = new Player();
        player.update(playerInfo);

        clanBattleBossData = new ClanBattleBossData(1, 10050001, 1, null, null);
        validBossDamage = new ClanBattleBossDamage(validId, clanBattleBossData, player);
        secondBossDamage = new ClanBattleBossDamage(secondId, clanBattleBossData, player);
    }

    @Test
    void testValidPostOperation() throws Exception {
        when(clanBattleBossService.createBossDamageFromId(validId)).thenReturn(validBossDamage);

        mockMvc.perform(post("/clanbattle-damage")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(validId)))
                .andExpect(status().isOk());

        verify(clanBattleBossDamageDao).save(validBossDamage);
    }

    @Test
    void testPostOperationInvalidBattleId() throws Exception {
        ClanBattleBossDamageId invalidClanBattleBossDamageId = new ClanBattleBossDamageId(0, 1, 1);

        mockMvc.perform(post("/clanbattle-damage")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidClanBattleBossDamageId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPostOperationInvalidPlayerId() throws Exception {
        ClanBattleBossDamageId invalidClanBattleBossDamageId = new ClanBattleBossDamageId(1, 0, 1);

        mockMvc.perform(post("/clanbattle-damage")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidClanBattleBossDamageId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPostOperationInvalidDefault() throws Exception {
        ClanBattleBossDamageId invalidClanBattleBossDamageId = new ClanBattleBossDamageId(1, 1, 0);

        mockMvc.perform(post("/clanbattle-damage")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidClanBattleBossDamageId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInvalidPostOperation() throws Exception {
        mockMvc.perform(post("/clanbattle-damage")
                .contentType("application/json")
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidGetOperation() throws Exception {
        Set<ClanBattleBossDamage> expectedResponseBody = Set.of(validBossDamage, secondBossDamage);
        when(clanBattleBossDamageDao.findAll()).thenReturn(expectedResponseBody);

        MvcResult mvcResult = mockMvc.perform(get("/clanbattle-damage"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();

        // convert to object because set being unordered can randomly fail
        assertThat(expectedResponseBody).isEqualTo(
                objectMapper.readValue(actualResponseBody, new TypeReference<Set<ClanBattleBossDamage>>() {})
        );
    }

    @Test
    void testValidPutOperation() throws Exception{
        List<ClanBattleBossDamageId> clanBattleBossDamageIds = List.of(validId, secondId);

        when(clanBattleBossService.createBossDamageFromId(validId)).thenReturn(validBossDamage);
        when(clanBattleBossService.createBossDamageFromId(secondId)).thenReturn(secondBossDamage);

        mockMvc.perform(put("/clanbattle-damage")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(clanBattleBossDamageIds)))
                .andExpect(status().isOk());

        verify(clanBattleBossDamageDao).delete(validBossDamage);
        verify(clanBattleBossDamageDao).save(secondBossDamage);
    }

    @Test
    void testSingleSetPutOperation() throws Exception {
        Set<ClanBattleBossDamageId> clanBattleBossDamageIds = Set.of(validId);
        mockMvc.perform(put("/clanbattle-damage")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(clanBattleBossDamageIds)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testDamageNotFoundPutOperation() throws Exception {
        Set<ClanBattleBossDamageId> clanBattleBossDamageIds = Set.of(invalidId, secondId);

        when(clanBattleBossService.createBossDamageFromId(invalidId)).thenThrow(InvalidRequestInputException.class);
        when(clanBattleBossService.createBossDamageFromId(secondId)).thenReturn(secondBossDamage);

        mockMvc.perform(put("/clanbattle-damage")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(clanBattleBossDamageIds)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidDeleteOperation() throws Exception{
        when(clanBattleBossService.createBossDamageFromId(validId)).thenReturn(validBossDamage);

        mockMvc.perform(delete("/clanbattle-damage")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(validId)))
                .andExpect(status().isOk());

        verify(clanBattleBossDamageDao).delete(validBossDamage);
    }

    @Test
    void testDeleteOperationInvalidBattleId() throws Exception {
        ClanBattleBossDamageId invalidClanBattleBossDamageId = new ClanBattleBossDamageId(0, 1, 1);

        mockMvc.perform(delete("/clanbattle-damage")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidClanBattleBossDamageId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteOperationInvalidPlayerId() throws Exception {
        ClanBattleBossDamageId invalidClanBattleBossDamageId = new ClanBattleBossDamageId(1, 0, 1);

        mockMvc.perform(delete("/clanbattle-damage")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidClanBattleBossDamageId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteOperationInvalidDefault() throws Exception {
        ClanBattleBossDamageId invalidClanBattleBossDamageId = new ClanBattleBossDamageId(1, 1, 0);

        mockMvc.perform(delete("/clanbattle-damage")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidClanBattleBossDamageId)))
                .andExpect(status().isBadRequest());
    }
}
