package com.github.glorindor.priconneserverbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.validation.constraints.Positive;
import java.io.Serializable;

@Embeddable
@AllArgsConstructor @NoArgsConstructor
public @Data class ClanBattleBossDamageId implements Serializable {
    @Positive(message = "All battle IDs must be bigger than 10000000")
    private int battleId;

    @Positive(message = "All player IDs must be positive")
    private int playerId;

    @Positive(message = "All damages must be positive")
    private long damage;
}
