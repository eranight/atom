package ru.atom.gameserver.gsession;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.atom.gameserver.geometry.Bar;
import ru.atom.gameserver.geometry.Point;
import ru.atom.gameserver.message.Message;
import ru.atom.gameserver.model.*;

import java.util.Map;
import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

public class GameMechanics {

    private static final Logger logger = LoggerFactory.getLogger(GameMechanics.class);
    private final Map<Integer, GameObject> mapOfObjects; //множество игровых объектов
    private final int frameTime = 1000 / 60;
    private final Set<Integer> transported; //множество id игроков, которых за данный тик уже переместили

    public GameMechanics() {
        this.mapOfObjects = new HashMap<>();
        this.transported = new HashSet<>();
    }

    public void addGameObject(int id, GameObject obj) {
        this.mapOfObjects.put(id, obj);
    }

    public void doMechanics(List<Message> messages) {
        //обнуляем множество уже передвинутых игроков и начинаем обрабатывать сообщения
        transported.clear();
        for (Message msg : messages) {
            //работаем с данным сообщением
            ObjectMapper mapper = new ObjectMapper();
            try {
                logger.info("Proccessing the message: " + mapper.writeValueAsString(msg));
            } catch (Exception e) {}

            boolean collisionOccur = false;

            int playerId = msg.getPlayerId();
            //проверяем, был ли данный игрок уже передвинут
            if (transported.contains(playerId)) continue;
            mapOfObjects.get(playerId);
            Pawn pawn = (Pawn) mapOfObjects.get(playerId);
            Point newPoint = pawn.move(Movable.Direction.valueOf(msg.getData()), frameTime);
            //создадим игрока в новой позиции
            Pawn newPawn = new Pawn(pawn.getId(), newPoint, pawn.getVelocity(), pawn.getMaxBombs());
            //проверим выход за границы поля. Для этого создадим Bar с размерами поля
            Bar field = new Bar(0, 0, 17 * 32, 13 * 32);
            if (!field.isIncluding(newPawn.getBar())) {
                continue;
            }
            Collection<GameObject> collectionOfObjects = mapOfObjects.values();
            Iterator<GameObject> it = collectionOfObjects.iterator();
            //для Box, Wall проверяем пересечения
            while (it.hasNext()) {
                GameObject obj = it.next();
                if (obj.getClass() == Box.class || obj.getClass() == Wall.class) {
                    if (newPawn.getBar().isColliding(obj.getBar())) {
                        collisionOccur = true;
                        break;
                    }
                }
            }
            if (!collisionOccur) {
                mapOfObjects.put(playerId, newPawn);
            }
            logger.info("New position: " + ((Pawn) mapOfObjects.get(playerId)).getPosition());
            //transported.add(playerId);
        }
    }

}