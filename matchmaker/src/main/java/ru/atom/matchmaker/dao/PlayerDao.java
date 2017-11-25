package ru.atom.matchmaker.dao;

import org.springframework.data.repository.CrudRepository;
import ru.atom.matchmaker.model.Player;

/**
 * Created by Alexandr on 25.11.2017.
 */
public interface PlayerDao extends CrudRepository<Player, Integer> {

}
