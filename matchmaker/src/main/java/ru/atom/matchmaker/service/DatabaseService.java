package ru.atom.matchmaker.service;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.atom.matchmaker.dao.GameDao;
import ru.atom.matchmaker.dao.PlayerDao;
import ru.atom.matchmaker.model.Game;
import ru.atom.matchmaker.model.Player;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alexandr on 25.11.2017.
 */
@Service
public class DatabaseService {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);

    @Autowired
    private GameDao gameDao;
    @Autowired
    private PlayerDao playerDao;

    @Transactional
    public void insertGame(@NotNull long gameId, @NotNull Date startTime, @NotEmpty List<String> logins) {
        Game game = new Game();
        gameDao.save(game).setId(gameId).setStartTime(startTime);
        playerDao.save(
                logins.stream().map(log -> new Player().setGame(game).setLogin(log))
                        .collect(Collectors.toList()));
        logger.info("insert game with id=" + gameId);
    }
}
