package com.github.glorindor.priconneserverbackend.player;

import com.github.glorindor.priconneserverbackend.entities.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerDao extends CrudRepository<Player, Integer> {
}
