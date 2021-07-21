package com.github.glorindor.priconneserverbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@AllArgsConstructor @NoArgsConstructor
public @Data class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int playerId;

    private String playerName;

    @Enumerated
    private Role role;

    private int lvl;

    @OneToMany
    private Set<CharacterData> unownedCharacterSet;

    @OneToMany(mappedBy = "player")
    private Set<ClanBattleBossDamage> clanBattleBossDamages;

    /**
     * Instantiates Player with limited field information.
     *
     * Most suited for changing from JSON as enumerated type is expected to be a String.
     * @param newPlayer contains Name, Lvl, Role information.
     */
    public Player(NewPlayer newPlayer) {
        this.playerName = newPlayer.getPlayerName();
        this.role = Role.valueOf(newPlayer.getRole());
        this.lvl = newPlayer.getLvl();
    }
}
