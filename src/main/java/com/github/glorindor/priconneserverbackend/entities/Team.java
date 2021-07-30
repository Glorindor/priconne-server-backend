package com.github.glorindor.priconneserverbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@AllArgsConstructor @NoArgsConstructor
public @Data class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int teamId;

    @OneToMany
    private Set<CharacterData> characterSet;

    public Team(Set<CharacterData> characterSet) {
        this.characterSet = characterSet;
    }
}
