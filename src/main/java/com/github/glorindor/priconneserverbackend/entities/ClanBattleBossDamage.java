package com.github.glorindor.priconneserverbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.validation.Valid;

@Entity
@AllArgsConstructor @NoArgsConstructor
public @Data class ClanBattleBossDamage {
    @EmbeddedId
    @Valid
    private ClanBattleBossDamageId clanBattleBossDamageId;

    @ManyToOne
    @MapsId("battleId")
    @Valid
    private ClanBattleBossData clanBattleBossData;

    @ManyToOne
    @MapsId("playerId")
    @Valid
    private Player player;
}
