package ru.atom.gameserver.gsession;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.atom.gameserver.component.ConnectionHandler;
import ru.atom.gameserver.geometry.Point;
import ru.atom.gameserver.message.Message;
import ru.atom.gameserver.message.Topic;
import ru.atom.gameserver.model.Box;
import ru.atom.gameserver.model.Pawn;
import ru.atom.gameserver.model.Wall;
import ru.atom.gameserver.tick.Ticker;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class GameSession {

    private final Logger logger = LoggerFactory.getLogger(GameSession.class);
    private final Ticker ticker;
    private final GameMechanics gameMechanics;
    private final Replicator replicator;
    private final InputQueue inputQueue;
    private final List<Integer> players;
    private final int frameTime = 1000 / 60;

    public GameSession(Long gameId, ConnectionHandler connectionHandler) {
        this.ticker = new Ticker();
        this.gameMechanics = new GameMechanics();
        this.replicator = new Replicator(gameId, connectionHandler);
        this.inputQueue = new InputQueue();
        this.players = new LinkedList<>();
    }

    public MessagesOffering messagesOffering() {
        return inputQueue;
    }

    public void start() throws Exception{
        //инициализируем игровой мир
        //создаем стены и коробки
        int curId = 5; // id с 1 по 4 пока зарезервированы под Pawn'ов
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 17; j++) {
                if (tileForWall(i, j)) {
                    Wall wall = new Wall(0, createPoint(i, j));
                    gameMechanics.addGameObject(curId++, wall);
                }
                if (outOfCorners(i, j) && !tileForWall(i, j)) {
                    Box box = new Box(0, createPoint(i, j));
                    gameMechanics.addGameObject(curId++, box);
                }
            }
        }
        //добавляем игроков
        float velocity = 0.5f;
        Pawn pawn = new Pawn(players.get(0), createPoint(0, 0), velocity, 1);
        logger.info("Initial position of first player: " + pawn.getPosition());
        gameMechanics.addGameObject(players.get(0), pawn);
        pawn = new Pawn(players.get(1), createPoint(0, 16), velocity, 1);
        gameMechanics.addGameObject(players.get(1), pawn);
        pawn = new Pawn(players.get(2), createPoint(12, 0), velocity, 1);
        gameMechanics.addGameObject(players.get(2), pawn);
        pawn = new Pawn(players.get(3), createPoint(12, 16), velocity, 1);
        gameMechanics.addGameObject(players.get(3), pawn);

        //добавим в inputQueue тестирующие сообщения
        //сначала пробуем тыкнуться в край поля
        Message msgToLeft = new Message(Topic.MOVE, "LEFT", players.get(0));
        inputQueue.offerMessage(msgToLeft);
        //затем сначала отходим вправо, потом возвращаемся и опять упираемся
        Message msgToRight = new Message(Topic.MOVE, "RIGHT", players.get(0));
        inputQueue.offerMessage(msgToRight);
        inputQueue.offerMessage(msgToRight);
        inputQueue.offerMessage(msgToLeft);
        inputQueue.offerMessage(msgToLeft);
        inputQueue.offerMessage(msgToLeft);
        //затем пробуем свободно ли сверху (должно быть свободно)
        Message msgToUp = new Message(Topic.MOVE, "UP", players.get(0));
        Message msgToDown = new Message(Topic.MOVE, "DOWN", players.get(0));
        inputQueue.offerMessage(msgToUp);
        inputQueue.offerMessage(msgToDown);
        //тоже, но после смещения вправо (должны упереться в верхний Wall)
        inputQueue.offerMessage(msgToRight);
        inputQueue.offerMessage(msgToUp);
        //еще раз (еще упираемся)
        inputQueue.offerMessage(msgToRight);
        inputQueue.offerMessage(msgToUp);
        //и еще (еще упираемся)
        inputQueue.offerMessage(msgToRight);
        inputQueue.offerMessage(msgToUp);
        //и еще (еще упираемся)
        inputQueue.offerMessage(msgToRight);
        inputQueue.offerMessage(msgToUp);
        //и еще (теперь упираемся в Box)
        inputQueue.offerMessage(msgToRight);



        //бесконечный цикл с оцищением InputQueue каждый фрейм и просчетом механики
        while (true) {
            long startTimePoint = (new Date()).getTime();
            List messages = inputQueue.pollMessages();
            gameMechanics.doMechanics(messages);
            long endTimePoint = (new Date()).getTime();
            long sleepTime = frameTime - (endTimePoint - startTimePoint);
            if (sleepTime > 0) Thread.sleep(sleepTime);
            //logger.info("Still alive");
        }
    }

    public void addPlayer(int playerId) {
        players.add(playerId);
    }

    //вспомогательный метод, создающий точку на основе индексов. Учитывается смещение на половину тайла
    private Point createPoint(int i, int j) {
        return new Point(j * 32 + 16, i * 32 + 16);
    }

    //еще один вспомогательный. Возвращает true, если точка вне углов
    private boolean outOfCorners(int i, int j) {
        if (i == 0 && (j < 2 || j > 14) || i == 1 && (j == 0 || j == 15) ||
                i == 11 && (j == 0 || j == 15) || i == 12 && (j < 2 || j > 14))
            return false;
        return true;
    }

    //возвращает true, если здесь должен быть Wall
    private boolean tileForWall(int i, int j) {
        if (i % 2 != 0 && j % 2 != 0)
            return true;
        return false;
    }
}