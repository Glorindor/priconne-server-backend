package com.github.glorindor.priconneserverbackend.character;

import com.github.glorindor.priconneserverbackend.entities.CharacterData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CharacterDataDao extends CrudRepository<CharacterData, Integer> {
    CharacterData findByUnitName(String unitName);
}
