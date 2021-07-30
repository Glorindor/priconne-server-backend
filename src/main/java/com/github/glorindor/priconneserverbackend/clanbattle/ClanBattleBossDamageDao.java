package com.github.glorindor.priconneserverbackend.clanbattle;

import com.github.glorindor.priconneserverbackend.entities.ClanBattleBossDamage;
import com.github.glorindor.priconneserverbackend.entities.ClanBattleBossDamageId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ClanBattleBossDamageDao extends CrudRepository<ClanBattleBossDamage, ClanBattleBossDamageId> {
    Set<ClanBattleBossDamage> findByPlayerPlayerId(int playerId);
    Set<ClanBattleBossDamage> findByClanBattleBossDataBattleId(int battleId);
}
