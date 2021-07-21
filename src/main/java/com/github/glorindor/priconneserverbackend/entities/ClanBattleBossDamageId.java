package com.github.glorindor.priconneserverbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@AllArgsConstructor @NoArgsConstructor
public @Data class ClanBattleBossDamageId implements Serializable {
    private int battleId;
    private int playerId;
    private long damage;
}
