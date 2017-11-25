package ru.atom.matchmaker.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.atom.matchmaker.service.DatabaseService;
import ru.atom.matchmaker.service.GameService;
import ru.atom.matchmaker.utils.MatchBuilder;

/**
 * Created by Alexandr on 25.11.2017.
 */
@Component
@RequestMapping("matchmaker")
public class MatchmakerComponent {
    private static final Logger logger = LoggerFactory.getLogger(MatchmakerComponent.class);

    @Autowired
    private GameService gameService;
    @Autowired
    private DatabaseService databaseService;

    @Value("${maxplayerscount}")
    private Integer maxPlayersCount;

    private MatchBuilder currentBuilder;

    @RequestMapping(path = "join",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String> join(@RequestParam("name") String name) {
        logger.info("join request has been received");
        if (!checkValidLogin(name)) {
            return ResponseEntity.badRequest().body("incorrect name");
        }
        processJoinRequest(name);
        return ResponseEntity.ok().body(String.valueOf(currentBuilder.getGameId()));
    }

    private boolean checkValidLogin(String login) {
        return (login != null) && !"".equals(login.trim());
    }

    private void processJoinRequest(String login) {
        if (currentBuilder == null) {
            long gameId = gameService.create(maxPlayersCount);
            currentBuilder = new MatchBuilder(maxPlayersCount, gameId);
            logger.info("game has been created with id=" + gameId);
        }
        currentBuilder.putLogin(login);
        gameService.connect(login, currentBuilder.getGameId());
        if (currentBuilder.isReady()) {
            gameService.start(currentBuilder.getGameId());
            currentBuilder = null;
        }
    }
}
