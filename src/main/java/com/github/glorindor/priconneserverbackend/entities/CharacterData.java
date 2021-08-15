package com.github.glorindor.priconneserverbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Entity
@AllArgsConstructor @NoArgsConstructor
public @Data class CharacterData {
    @Id
    @Positive(message = "All unit IDs must be positive")
    private int unitId;

    @NotBlank(message = "A unit must have a name")
    private String unitName;
}
