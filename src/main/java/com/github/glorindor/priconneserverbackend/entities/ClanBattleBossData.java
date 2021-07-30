package com.github.glorindor.priconneserverbackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class ClanBattleBossData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int battleId; // easier to store and refer

    private int bossId;

    private int difficulty;

    @OneToMany(cascade = {CascadeType.ALL})
    private Set<Team> recommendedTeams;

    @JsonIgnore
    @OneToMany(mappedBy = "clanBattleBossData")
    private Set<ClanBattleBossDamage> clanBattleBossDamages;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClanBattleBossData that = (ClanBattleBossData) o;
        return getBattleId() == that.getBattleId() && getBossId() == that.getBossId() && getDifficulty() == that.getDifficulty() && Objects.equals(getRecommendedTeams(), that.getRecommendedTeams());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBattleId(), getBossId(), getDifficulty(), getRecommendedTeams());
    }

    @Override
    public String toString() {
        return "ClanBattleBossData{" +
                "battleId=" + battleId +
                ", bossId=" + bossId +
                ", difficulty=" + difficulty +
                ", recommendedTeams=" + recommendedTeams +
                '}';
    }

    /**
     * Updates the boss's game-id and difficulty.
     *
     * This shouldn't get too much use as manually updating the database by parsing the
     * internet is a much simpler solution.
     * @param bossInfo bossInfo ID of the boss
     */
    public void update(BossInfo bossInfo) {
        this.bossId = bossInfo.getBossId();
        this.difficulty = bossInfo.getDifficulty();
    }

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