package ru.atom.gameserver;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.atom.gameserver.geometry.Point;
import ru.atom.gameserver.model.Box;
import ru.atom.gameserver.model.Pawn;
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
        try {
            Thread.sleep(5000);
        } catch (Exception e) {}

        //проверим пересечение накладывающихся Pawn и Box
        /*Pawn pawn = new Pawn(1, new Point(0, 0), 0.5f, 1);
        Box box = new Box(0, new Point(25, 0));
        boolean res = pawn.getBar().isColliding(box.getBar());
        logger.info("Pawn and Box collide: " + res);
        logger.info("originCorner of pawn: " + pawn.getBar().getOriginCorner());
        logger.info("endCorner of pawn: " + pawn.getBar().getEndCorner());
        logger.info("originCorner of box: " + box.getBar().getOriginCorner());
        logger.info("endCorner of box: " + box.getBar().getEndCorner());*/


    }

}
