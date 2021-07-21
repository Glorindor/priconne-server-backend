package com.github.glorindor.priconneserverbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@AllArgsConstructor @NoArgsConstructor
public @Data class ClanBattleBossData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int battleId; // easier to store and refer

    private int bossId;

    private int difficulty;

    @OneToMany
    private Set<Team> recommendedTeams;

    @OneToMany(mappedBy = "clanBattleBossData")
    private Set<ClanBattleBossDamage> clanBattleBossDamages;

    /**
     * Return the clan battle ID.
     *
     * Take module 100 of battle ID to get human readable clan battle.
     * @return clan battle ID.
     */
    public int whichClanBattle() {
        return bossId / 10000;
    }
}