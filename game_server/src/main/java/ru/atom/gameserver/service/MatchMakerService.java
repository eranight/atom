package ru.atom.gameserver.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class MatchMakerService {

    private static final Logger logger = LoggerFactory.getLogger(MatchMakerService.class);
    private static final OkHttpClient client = new OkHttpClient();

    private HttpHeaders headers;
    @Value("${mmserver}")
    private String mmServer;
    @Value("${mmport}")
    private int port;
    @Value("${mmgameover}")
    private String mmGameOver;
    @Value("${mm_disconnect}")
    private String mmDisconnect;

    public MatchMakerService() {
        headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-urlencoded");
    }

    public void disconectionWithPlayer(String login) {
        Request request = new Request.Builder()
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
                        "login=" + login))
                .url(mmServer + ":" + port + mmDisconnect)
                .build();
        try {
            client.newCall(request).execute();
        } catch (Exception e) {
            logger.info("Problem with executing request to mm");
        }
    }

    public void sendGameOver(String winnerLogin) {
        logger.info("Winner login: " + winnerLogin);
        Request request = new Request.Builder()
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
                        "login=" + winnerLogin))
                .url(mmServer + ":" + port + mmGameOver)
                .build();
        try {
            Response response = client.newCall(request).execute();
            logger.info("Response from mm: " + response);
        } catch (Exception e) {
            logger.info("Problem with executing request to mm");
        }
    }


}
