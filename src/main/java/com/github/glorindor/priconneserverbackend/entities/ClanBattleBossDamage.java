package com.github.glorindor.priconneserverbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
