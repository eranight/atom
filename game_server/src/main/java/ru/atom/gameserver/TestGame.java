package ru.atom.gameserver;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.atom.gameserver.geometry.Bar;
import ru.atom.gameserver.geometry.Point;
import ru.atom.gameserver.model.Box;
import ru.atom.gameserver.model.Movable;
import ru.atom.gameserver.model.Pawn;
import ru.atom.gameserver.model.Wall;
import ru.atom.gameserver.service.GameRepository;

public class TestGame {

    private static final Logger logger = LoggerFactory.getLogger(TestGame.class);

    public static void main(String[] args) {
        GameRepository repository = new GameRepository();
        Long gameId = repository.createGame(4);
        for (int i = 1; i < 5; i++) {
            repository.getGameById(gameId).addPlayer(i);
        }
        Thread newThread = new Thread(new Runnable() {
            public void run() {
                try {
                    repository.getGameById(gameId).start();
                } catch (Exception e) {}
            }
        });
        newThread.start();



    }



}
