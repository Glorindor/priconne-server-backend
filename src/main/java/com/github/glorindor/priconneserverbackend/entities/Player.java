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

    @Enumerated
    private Role role;

    private int lvl;

    @OneToMany
    private Set<CharacterData> unownedCharacterSet;
}
