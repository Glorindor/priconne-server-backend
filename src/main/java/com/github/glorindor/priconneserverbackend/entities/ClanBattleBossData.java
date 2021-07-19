package com.github.glorindor.priconneserverbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
@IdClass(ClanBattleBossDataId.class)
@AllArgsConstructor @NoArgsConstructor
public @Data class ClanBattleBossData {
    @Id
    private int bossId;

    @Id
    private int difficulty;

    @OneToMany
    private Set<Team> recommendedTeams;

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