package com.github.glorindor.priconneserverbackend.character;

import com.github.glorindor.priconneserverbackend.entities.CharacterData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CharacterDataRepository extends CrudRepository<CharacterData, Integer> {
    Optional<CharacterData> findByUnitName(String unitName);
}
