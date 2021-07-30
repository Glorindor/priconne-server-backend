package com.github.glorindor.priconneserverbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public @Data class PlayerInfo {
    private String playerName;

    private String role;

    private int lvl;
}
