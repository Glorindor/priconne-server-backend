package com.github.glorindor.priconneserverbackend.clanbattle;

import com.github.glorindor.priconneserverbackend.character.CharacterDataService;
import com.github.glorindor.priconneserverbackend.entities.*;
import com.github.glorindor.priconneserverbackend.exceptions.InvalidRequestInputException;
import com.github.glorindor.priconneserverbackend.player.PlayerDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ClanBattleBossService {
    private final ClanBattleBossDataDao clanBattleBossDataDao;
    private final PlayerDao playerDao;

    private final CharacterDataService characterDataService;

    /**
     * Given ids creates an associated {@link ClanBattleBossDamage}.
     * @param clanBattleBossDamageId {@link ClanBattleBossDamageId} containing, damage,
     * @return an associated {@link ClanBattleBossDamage}
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

    /**
     * Updates the given team from Set of recommended teams in a {@link ClanBattleBossData}
     * @param teamId id that specifies the team
     * @param clanBattleBossData from which boss the recommended team set will be taken
     * @param characterNameSet name set of the character that will make up the team
     */
    public void updateTeamFromRecommendedTeams(int teamId, ClanBattleBossData clanBattleBossData, Set<String> characterNameSet) {
        Set<CharacterData> characterDataSet = characterDataService.getCharacterDataSetFromNameSet(characterNameSet);

        if (characterDataSet.size() != 5) { // a team is comprised of 5 characters
            throw new InvalidRequestInputException();
        }

        for (Team team : clanBattleBossData.getRecommendedTeams()) {
            if (team.getTeamId() == teamId) {
                team.setCharacterSet(characterDataSet);
            }
        }
    }
}
