package ru.atom.matchmaker.service;

import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by Alexandr on 25.11.2017.
 */
@Service
public class GameService {
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);
    private static final OkHttpClient client = new OkHttpClient();

    @Value("${url}")
    private String url;

    public long create(int playerCount) {
        logger.info("create new game for " + playerCount + "th players");
        return -1;
    }

    public void connect(String playerLogin, long gameId) {

    }

    public void start(long gameId) {

    }
}
