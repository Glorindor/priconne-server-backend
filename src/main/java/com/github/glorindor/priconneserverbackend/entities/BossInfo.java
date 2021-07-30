package com.github.glorindor.priconneserverbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public @Data class BossInfo {
    private int bossId;

    private int difficulty;
}
