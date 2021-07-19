package com.github.glorindor.priconneserverbackend.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClanBattleBossDataTest {
    @Test
    void testSingleDigitClanBattle() {
        ClanBattleBossData cb = new ClanBattleBossData();
        cb.setBossId(10010101);

        assertEquals(1001, cb.whichClanBattle());
    }

    @Test
    void testTwoDigit() {
        ClanBattleBossData cb = new ClanBattleBossData();
        cb.setBossId(10110101);

        assertEquals(1011, cb.whichClanBattle());
    }
}
