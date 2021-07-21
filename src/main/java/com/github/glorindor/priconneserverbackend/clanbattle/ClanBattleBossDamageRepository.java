package com.github.glorindor.priconneserverbackend.clanbattle;

import com.github.glorindor.priconneserverbackend.entities.ClanBattleBossDamage;
import com.github.glorindor.priconneserverbackend.entities.ClanBattleBossDamageId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClanBattleBossDamageRepository extends CrudRepository<ClanBattleBossDamage, ClanBattleBossDamageId> {
    Optional<ClanBattleBossDamage> findByPlayerPlayerName(String name);
    Optional<ClanBattleBossDamage> findByClanBattleBossDataBossId(int bossId);
}
