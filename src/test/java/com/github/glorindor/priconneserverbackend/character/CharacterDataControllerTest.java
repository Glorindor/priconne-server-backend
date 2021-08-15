package com.github.glorindor.priconneserverbackend.character;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.glorindor.priconneserverbackend.entities.CharacterData;
import com.github.glorindor.priconneserverbackend.entities.ClanBattleBossDamage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CharacterDataController.class)
public class CharacterDataControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CharacterDataDao characterDataDao;

    CharacterData mifuyuData = new CharacterData(104801, "Mifuyu");
    CharacterData suzumeData = new CharacterData(107701, "Suzume(Summer)");

    @Test
    void testGetOperation() throws Exception{
        Set<CharacterData> expectedResponseBody = Set.of(mifuyuData, suzumeData);
        when(characterDataDao.findAll()).thenReturn(expectedResponseBody);
        MvcResult mvcResult = mockMvc.perform(get("/character"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertThat(expectedResponseBody).isEqualTo(
                objectMapper.readValue(actualResponseBody, new TypeReference<Set<CharacterData>>() {})
        );
    }
}
