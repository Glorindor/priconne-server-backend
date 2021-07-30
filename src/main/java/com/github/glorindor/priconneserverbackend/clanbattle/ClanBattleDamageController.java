package com.github.glorindor.priconneserverbackend.clanbattle;

import com.github.glorindor.priconneserverbackend.entities.ClanBattleBossDamage;
import com.github.glorindor.priconneserverbackend.entities.ClanBattleBossDamageId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("clanbattle-damage")
@RequiredArgsConstructor
public class ClanBattleDamageController {
    private final ClanBattleBossDamageService clanBattleBossDamageService;

    private final ClanBattleBossDamageDao clanBattleBossDamageDao;

    /**
     * Endpoint to adding a new damage info.
     * @param clanBattleBossDamageId a {@link ClanBattleBossDamageId} containing battleId, playerId, and damage
     *                               info.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void addBossDamage(@RequestBody ClanBattleBossDamageId clanBattleBossDamageId) {
        clanBattleBossDamageDao.save(clanBattleBossDamageService.createBossDamageFromId(clanBattleBossDamageId));
    }

    /**
     * Endpoint to getting all damages done in this clan battle.
     * @return a {@link Set<ClanBattleBossDamage>} that contains all the rows.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Set<ClanBattleBossDamage> getAllBossDamages() {
        Iterable<ClanBattleBossDamage> clanBattleBossDamageIterable = clanBattleBossDamageDao.findAll();
        Set<ClanBattleBossDamage> clanBattleBossDamageSet = new HashSet<>();

        clanBattleBossDamageIterable.forEach(clanBattleBossDamageSet::add);

        return clanBattleBossDamageSet;
    }

    /**
     * Endpoint to updating a damage row.
     * @param clanBattleBossDamageIds a {@link List<ClanBattleBossDamageId>} with following properties:
     *                                it must have two elements,
     *                                the first element will specify the replaced row,
     *                                the latter element will specify the new values for the row
     */
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void updateBossDamage(@RequestBody List<ClanBattleBossDamageId> clanBattleBossDamageIds) {
        ClanBattleBossDamage formerDamage = clanBattleBossDamageService.createBossDamageFromId(clanBattleBossDamageIds.get(0));
        ClanBattleBossDamage newDamage = clanBattleBossDamageService.createBossDamageFromId(clanBattleBossDamageIds.get(1));

        clanBattleBossDamageDao.delete(formerDamage);
        clanBattleBossDamageDao.save(newDamage);
    }

    /**
     * Endpoint to deleting a damage row.
     * @param clanBattleBossDamageId a {@link ClanBattleBossDamageId} containing battleId, playerId, and damage
     *                               info.
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteBossDamage(@RequestBody ClanBattleBossDamageId clanBattleBossDamageId) {
        clanBattleBossDamageDao.delete(clanBattleBossDamageService.createBossDamageFromId(clanBattleBossDamageId));
    }
}
