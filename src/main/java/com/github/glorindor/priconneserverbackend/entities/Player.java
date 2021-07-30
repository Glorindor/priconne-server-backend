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
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int playerId;

    private String playerName;

    @Enumerated
    private Role role;

    private int lvl;

    @OneToMany(cascade = {CascadeType.ALL})
    private Set<CharacterData> unownedCharacterSet;

    // Fix the cyclic dependency issues
    @JsonIgnore
    @OneToMany(mappedBy = "player")
    private Set<ClanBattleBossDamage> clanBattleBossDamages;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return getPlayerId() == player.getPlayerId() && getLvl() == player.getLvl() && getPlayerName().equals(player.getPlayerName()) && getRole() == player.getRole() && Objects.equals(getUnownedCharacterSet(), player.getUnownedCharacterSet());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPlayerId(), getPlayerName(), getRole(), getLvl(), getUnownedCharacterSet());
    }

    @Override
    public String toString() {
        return "Player{" +
                "playerId=" + playerId +
                ", playerName='" + playerName + '\'' +
                ", role=" + role +
                ", lvl=" + lvl +
                ", unownedCharacterSet=" + unownedCharacterSet +
                '}';
    }

    /**
     * Updates the player's name, role and level.
     *
     * This can also be used to instantiate the object after a no argument constructor call.
     * @param playerInfo contains Name, Lvl, Role information.
     */
    public void update(PlayerInfo playerInfo) {
        this.playerName = playerInfo.getPlayerName();
        this.role = Role.valueOf(playerInfo.getRole());
        this.lvl = playerInfo.getLvl();
    }
}
