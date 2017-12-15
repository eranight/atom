package ru.atom.gameserver.gsession;

import ru.atom.gameserver.geometry.Point;
import ru.atom.gameserver.model.*;
import ru.atom.gameserver.tick.Tickable;
import ru.atom.gameserver.tick.Ticker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameMechanics implements Tickable {

    private int id;
    private final Ticker ticker;
    private final Replicator replicator;

    private final List<Pawn> pawns = new ArrayList<>();
    private final List<GameObject> gameObjects = new CopyOnWriteArrayList<>();
    private int idGenerator = 0;

    GameMechanics(Ticker ticker, Replicator replicator) {
        id = 0;
        this.ticker = ticker;
        this.replicator = replicator;
        //init walls and boxes here
        //bombs, explosion and paws must be added to ticker
        //левая ограничивающая стена
        for (int i = 0; i < 13; i++) {
            gameObjects.add(new Wall(++idGenerator, indexToPoint(i,0)));
        }
        //верхняя
        for (int j = 1; j < 17; j++) {
            gameObjects.add(new Wall(++idGenerator, indexToPoint(12, j)));
        }
        //правая
        for (int i = 0; i < 12; i++) {
            gameObjects.add(new Wall(++idGenerator, indexToPoint(i,16)));
        }
        //нижняя
        for (int j = 1; j < 16; j++) {
            gameObjects.add(new Wall(++idGenerator, indexToPoint(0, j)));
        }

        //расставляем пол и Wall
        for (int i = 1; i < 12; i++) {
            for (int j = 1; j < 16; j++) {
                if (i % 2 == 0 && j % 2 == 0) {
                    gameObjects.add(new Wall(++idGenerator, indexToPoint(i, j)));
                } else {
                    gameObjects.add(new Grass(++idGenerator, indexToPoint(i, j)));
                }

            }
        }

    }

    int addPlayer() {
        //add to ticker!
        return pawns.size() - 1;
    }

    @Override
    public void tick(long elapsed) {
        boolean gameOverFlag = false;

        //send replica via replicator
        replicator.writeReplica(gameObjects, gameOverFlag);
    }

    private Point indexToPoint(int i, int j) {
        return new Point(j * 32, i * 32);
    }

}
