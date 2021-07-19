package com.github.glorindor.priconneserverbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@AllArgsConstructor @NoArgsConstructor
public @Data class CharacterData {
    @Id
    private int unitId;
    private String unitName;
}
