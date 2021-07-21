package com.github.glorindor.priconneserverbackend.clanbattle;

import com.github.glorindor.priconneserverbackend.entities.Team;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends CrudRepository<Team, Integer> {
}
