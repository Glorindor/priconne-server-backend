package com.github.glorindor.priconneserverbackend.clanbattle;

import com.github.glorindor.priconneserverbackend.entities.ClanBattleBossData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClanBattleBossDataDao extends CrudRepository<ClanBattleBossData, Integer> {
}
