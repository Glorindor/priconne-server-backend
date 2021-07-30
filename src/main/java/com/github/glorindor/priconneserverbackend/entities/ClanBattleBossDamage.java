package com.github.glorindor.priconneserverbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
@AllArgsConstructor @NoArgsConstructor
public @Data class ClanBattleBossDamage {
    @EmbeddedId
    private ClanBattleBossDamageId clanBattleBossDamageId;

    @ManyToOne
    @MapsId("battleId")
    private ClanBattleBossData clanBattleBossData;

    @ManyToOne
    @MapsId("playerId")
    private Player player;
}
