package ru.atom.gameserver.service;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MatchMakerService {

    private static final Logger logger = LoggerFactory.getLogger(MatchMakerService.class);
    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

    @Value("${mmserver}")
    private String mmServer;
    @Value("${mmport}")
    private int port;
    @Value("${mmgameover}")
    private String mmGameOver;
    @Value("${mmdisconnect}")
    private String mmDisconnect;

    public void disconnectionWithPlayer(String login) {
        logger.info("Disconnect player with login: " + login);
        sendRequest(login, mmDisconnect);
    }

    public void sendGameOver(String winnerLogin) {
        logger.info("Winner login: " + winnerLogin);
        sendRequest(winnerLogin, mmGameOver);
    }

    private void sendRequest(String login, String urlEnding) {
        Request request = new Request.Builder()
                .post(RequestBody.create(mediaType,"login=" + login))
                .url(mmServer + ":" + port + urlEnding)
                .build();
        try (Response response = client.newCall(request).execute()) {
            logger.info("send request successful!");
        } catch (IOException e) {
            logger.warn("IOEXception: " + e.getMessage());
        } catch (Exception e) {
            logger.warn("Uknowned excetrion:" + e.getMessage());
        }
    }
}
