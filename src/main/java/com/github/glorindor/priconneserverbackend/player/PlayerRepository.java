package com.github.glorindor.priconneserverbackend.player;

import com.github.glorindor.priconneserverbackend.entities.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Integer> {
}
