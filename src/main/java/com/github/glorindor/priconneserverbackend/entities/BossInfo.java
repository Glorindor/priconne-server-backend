package com.github.glorindor.priconneserverbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@AllArgsConstructor
public @Data class BossInfo {
    @Min(value = 10000000, message = "All boss IDs must be bigger than 10000000")
    private int bossId;

    @Min(value = 1, message = "You cannot have a difficulty lower than 1")
    @Max(value = 2, message = "You cannot have a difficulty higher than 2")
    private int difficulty;
}
