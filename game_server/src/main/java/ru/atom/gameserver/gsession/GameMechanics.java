package ru.atom.gameserver.gsession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.atom.gameserver.geometry.Bar;
import ru.atom.gameserver.geometry.Point;
import ru.atom.gameserver.message.Message;
import ru.atom.gameserver.model.*;
import ru.atom.gameserver.tick.Tickable;
import ru.atom.gameserver.tick.Ticker;
import ru.atom.gameserver.util.JsonHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameMechanics implements Tickable {

    private final Logger logger = LoggerFactory.getLogger(GameMechanics.class);

    private final Ticker ticker;
    private final Replicator replicator;
    private final InputQueue inputQueue;

    private final List<Pawn> pawns = new ArrayList<>();
    private final List<GameObject> gameObjects = new CopyOnWriteArrayList<>();
    private final Set<Integer> translated = new HashSet<>();
    private int idGenerator = 0;

    GameMechanics(Ticker ticker, Replicator replicator, InputQueue queue) {
        this.ticker = ticker;
        this.replicator = replicator;
        this.inputQueue = queue;
        //init walls and boxes here
        //bombs, explosion and paws must be added to ticker
        //левая ограничивающая стена
        for (int i = 0; i < 13; i++) {
            gameObjects.add(new Wall(idGenerator++, indexToPoint(i,0)));
        }
        //верхняя
        for (int j = 1; j < 17; j++) {
            gameObjects.add(new Wall(idGenerator++, indexToPoint(12, j)));
        }
        //правая
        for (int i = 0; i < 12; i++) {
            gameObjects.add(new Wall(idGenerator++, indexToPoint(i,16)));
        }
        //нижняя
        for (int j = 1; j < 16; j++) {
            gameObjects.add(new Wall(idGenerator++, indexToPoint(0, j)));
        }

        //расставляем пол и Wall
        for (int i = 1; i < 12; i++) {
            for (int j = 1; j < 16; j++) {
                if (i % 2 == 0 && j % 2 == 0) {
                    gameObjects.add(new Wall(idGenerator++, indexToPoint(i, j)));
                } else {
                    gameObjects.add(new Grass(idGenerator++, indexToPoint(i, j)));
                }

            }
        }

    }

    void createBomb(Point point) {
        Bomb bomb = new Bomb(idGenerator++, point, 3000, 1);
        gameObjects.add(bomb);
        ticker.insertTickableFront(bomb);
    }

    int addPlayer() {
        int id = idGenerator++;
        Point point = null;
        switch (pawns.size()){
            case 0: point = new Point(33.0f, 33.0f); break;
            case 1: point = new Point(32.0f * 15, 32.0f); break;
            case 2: point = new Point(32.0f * 11, 32.0f * 11); break;
            case 3: point = new Point(32.0f * 15, 32.0f * 11); break;
            default: point = null;
        }
        Pawn pawn = new Pawn(id, point, 0.15f, 1);
        gameObjects.add(pawn);
        pawns.add(pawn);
        ticker.insertTickableFront(pawn);
        return id;
    }

    @Override
    public void tick(long elapsed) {
        boolean gameOverFlag = false;

        translated.clear();
        List<Message> messages = inputQueue.pollMessages();
        for  (Message msg : messages) {
            switch (msg.getTopic()) {
                case MOVE:
                    logger.info("Message processing");
                    int playerId = JsonHelper.getJsonNode(msg.getData()).get("possess").asInt();
                    if (translated.contains(playerId)) {
                        break;
                    }
                    translated.add(playerId);
                    Movable.Direction direction = Movable.Direction.valueOf(JsonHelper.getJsonNode(msg.getData()).get("direction").asText());
                    Pawn pawn = (Pawn) gameObjects.get(playerId);
                    Bar newBar = pawn.move(direction, elapsed);
                    boolean collision = false;
                    for (GameObject obj : gameObjects) {
                        if (obj instanceof Wall || obj instanceof Wood) {
                            if (newBar.isColliding(obj.getBar())) {
                                collision = true;
                                break;
                            }
                        }
                    }
                    if (!collision) {
                        pawn.setBar(newBar);
            }
                    break;
            }
        }

        //send replica via replicator
        replicator.writeReplica(gameObjects, gameOverFlag);
    }

    private Point indexToPoint(int i, int j) {
        return new Point(j * 32, i * 32);
    }

}
