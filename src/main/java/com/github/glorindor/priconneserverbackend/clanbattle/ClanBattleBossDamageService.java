package com.github.glorindor.priconneserverbackend.clanbattle;

import com.github.glorindor.priconneserverbackend.entities.ClanBattleBossDamage;
import com.github.glorindor.priconneserverbackend.entities.ClanBattleBossDamageId;
import com.github.glorindor.priconneserverbackend.entities.ClanBattleBossData;
import com.github.glorindor.priconneserverbackend.entities.Player;
import com.github.glorindor.priconneserverbackend.exceptions.InvalidRequestInputException;
import com.github.glorindor.priconneserverbackend.player.PlayerDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClanBattleBossDamageService {
    private final ClanBattleBossDataDao clanBattleBossDataDao;
    private final PlayerDao playerDao;

    /**
     * Given ids creates an associated {@link ClanBattleBossDamage}.
     * @param clanBattleBossDamageId {@link ClanBattleBossDamageId} containing, damage,
     * @return
     */
    public ClanBattleBossDamage createBossDamageFromId(ClanBattleBossDamageId clanBattleBossDamageId) {
        Optional<ClanBattleBossData> clanBattleBossData =
                clanBattleBossDataDao.findById(clanBattleBossDamageId.getBattleId());
        Optional<Player> player = playerDao.findById(clanBattleBossDamageId.getPlayerId());

        if (clanBattleBossData.isEmpty() || player.isEmpty()) {
            throw new InvalidRequestInputException();
        }

        return new ClanBattleBossDamage(clanBattleBossDamageId, clanBattleBossData.get(), player.get());
    }
}
