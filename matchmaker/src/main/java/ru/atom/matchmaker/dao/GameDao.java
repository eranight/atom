package ru.atom.matchmaker.dao;

import org.springframework.data.repository.CrudRepository;
import ru.atom.matchmaker.model.Game;

/**
 * Created by Alexandr on 25.11.2017.
 */
public interface GameDao extends CrudRepository<Game, Long> {

}
