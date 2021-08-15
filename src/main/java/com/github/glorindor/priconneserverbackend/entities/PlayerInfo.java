package com.github.glorindor.priconneserverbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;

@AllArgsConstructor
public @Data class PlayerInfo {
    @NotBlank(message = "A player must have a name")
    private String playerName;

    @NotNull(message = "A role must not be null")
    private String role;

    @Positive(message = "A level cannot be lower than 1")
    @Max(value = 200, message = "A level cannot be higher than 200")
    private int lvl;
}
