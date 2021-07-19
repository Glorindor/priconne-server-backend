package com.github.glorindor.priconneserverbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor @NoArgsConstructor
public @Data class ClanBattleBossDataId implements Serializable {
    private int bossId;

    private int difficulty;
}
