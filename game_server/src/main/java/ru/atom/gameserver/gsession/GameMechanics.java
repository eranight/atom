package ru.atom.gameserver.gsession;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import ru.atom.gameserver.geometry.Bar;
import ru.atom.gameserver.geometry.Point;
import ru.atom.gameserver.message.Message;
import ru.atom.gameserver.model.*;
import ru.atom.gameserver.tick.Tickable;
import ru.atom.gameserver.tick.Ticker;
import ru.atom.gameserver.util.JsonHelper;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameMechanics implements Tickable, GarbageCollector, ModelsManager {

    private static final int DEF_SIZE = 32;

    private final Ticker ticker;
    private final Replicator replicator;
    private final InputQueue inputQueue;

    private final Map<Integer, Pawn> pawns = new HashMap<>();
    private final List<GameObject> gameObjects = new CopyOnWriteArrayList<>();
    private final Set<GameObject> garbageIndexSet = new HashSet<>();
    private int idGenerator = 0;

    private final Field field;

    GameMechanics(Ticker ticker, Replicator replicator, InputQueue inputQueue) {
        this.ticker = ticker;
        this.replicator = replicator;
        this.inputQueue = inputQueue;
        this.field = new Field();
        //init walls and boxes here
        //bombs, explosion and paws must be added to ticker
        //левая ограничивающая стена
        initByMap();
    }

    private int nextId() {
        return idGenerator++;
    }

    private void initByMap() {
        for (int row = 0; row < Field.ROWS; ++row) {
            for (int col = 0; col < Field.COLS; ++col) {
                Field.Cell cell = new Field.Cell(col, row);
                int id = nextId();
                switch (field.getCellType(cell)) {
                    case Field.WALL: {
                        field.setId(cell, id);
                        gameObjects.add(new Wall(id, indexToPoint(col, row)));
                    }
                        break;
                    case Field.EMPTY: {
                        field.setId(cell, id);
                        gameObjects.add(new Grass(id, indexToPoint(col, row)));
                    }
                        break;
                    case Field.BOX: {
                        gameObjects.add(new Grass(id, indexToPoint(col, row)));
                        id = nextId();
                        field.setId(cell, id);
                        Wood wood = new Wood(id, indexToPoint(col, row));
                        gameObjects.add(wood);
                    }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    int addPlayer() {
        int id = nextId();
        Point point = null;
        switch (pawns.size()){
            case 0: point = new Point(DEF_SIZE, DEF_SIZE); break;
            case 1: point = new Point(DEF_SIZE * 15, DEF_SIZE); break;
            case 2: point = new Point(DEF_SIZE, DEF_SIZE * 11); break;
            case 3: point = new Point(DEF_SIZE * 15, DEF_SIZE * 11); break;
            default: point = null;
        }
        Pawn pawn = new Pawn(id, point, 0.16667f, 1);
        pawn.setGarbageCollector(this);
        gameObjects.add(pawn);
        pawns.put(id, pawn);
        ticker.insertTickableFront(pawn);
        return id;
    }

    @Override
    public void tick(long elapsed) {
        boolean gameOverFlag = false;

        Set<Integer> translated = new HashSet<>();
        List<Message> messages = inputQueue.pollMessages();
        for (Message message : messages) {
            JsonNode jsonNode = JsonHelper.getJsonNode(message.getData());
            switch (message.getTopic()) {
                case MOVE: {
                    int possess = jsonNode.get("possess").asInt();
                    if (translated.contains(possess)) {
                        continue;
                    }
                    translated.add(possess);
                    Movable.Direction direction = Movable.Direction.valueOf(jsonNode.get("direction").asText());
                    Pawn pawn = pawns.get(possess);
                    Point nextPos = pawn.move(direction, elapsed);
                    Bar nextBar = Pawn.getBarForPosition(nextPos);
                    boolean collision = false;
                    for(GameObject gameObject : gameObjects) {
                        if (gameObject instanceof Wall || gameObject instanceof Wood) {
                            if (nextBar.isColliding(gameObject.getBar())) {
                                collision = true;
                                break;
                            }
                        }
                    }
                    if (!collision) {
                        pawn.setPosition(nextPos);
                    }
                }
                    break;
                case PLANT_BOMB: {
                    int possess = jsonNode.asInt();
                    if (translated.contains(possess)) {
                        continue;
                    }
                    translated.add(possess);
                    Pawn pawn = pawns.get(possess);
                    putBomb(pawn.getPosition(), 3000, pawn.getBombPower());
                }
                    break;
            }
        }

        for (GameObject gameObject : garbageIndexSet) {
            if (gameObject instanceof Tickable) {
                ticker.unregisterTickable((Tickable) gameObject);
            }
            if (gameObject instanceof Pawn) {
                pawns.remove(gameObject.getId());
            }
            gameObjects.remove(gameObject);
        }
        garbageIndexSet.clear();
        replicator.writeReplica(gameObjects, gameOverFlag);
    }

    private Point indexToPoint(int i, int j) {
        return new Point(i * DEF_SIZE, j * DEF_SIZE);
    }

    private Point normilizePoint(Point point) {
        return new Point(DEF_SIZE * ((int)point.getX() / DEF_SIZE), DEF_SIZE * ((int)point.getY() / DEF_SIZE));
    }

    private Field.Cell pointToCell(Point point) {
        int col = (int)point.getX() / DEF_SIZE;
        int row = (int)point.getY() / DEF_SIZE;
        return  new Field.Cell(col, row);
    }

    private Point cellToPoint(Field.Cell cell) {
        return new Point(cell.col * DEF_SIZE, cell.row * DEF_SIZE);
    }

    @Override
    public void mark(GameObject gameObject) {
        garbageIndexSet.add(gameObject);
    }

    @Override
    public void putBomb(Point point, long lifetime, int power) {
        Bomb bomb = new Bomb(nextId(), normilizePoint(point), lifetime, power);
        bomb.setGarbageCollector(this);
        bomb.setModelsManager(this);
        gameObjects.add(bomb);
        ticker.insertTickableFront(bomb);
    }

    @Override
    public void putFire(Point point, long lifetime, int power) {
        List<Field.Cell> cells = field.getFireCells(pointToCell(point), power);
        for (Field.Cell fireCell : cells) {
            Fire fire = new Fire(nextId(), cellToPoint(fireCell), lifetime);
            fire.setGarbageCollector(this);
            fire.setModelsManager(this);
            gameObjects.add(fire);
            ticker.insertTickableFront(fire);
        }
        cells = field.applyFireCells(cells);
        for (Field.Cell cell : cells) {
            int id = field.getId(cell);
            Wood wood = (Wood)gameObjects.stream().filter(g -> g.getId() == id).findFirst().get();
            if (wood.containsBuff()) {
                putBonus(wood.getPosition(), wood.getBuffType());
            }
            garbageIndexSet.add(wood);
        }
    }

    @Override
    public void putBonus(Point point, Buff.BuffType buffType) {
        Buff buff = new Buff(nextId(), point, buffType);
        field.setBonus(pointToCell(point));
        gameObjects.add(buff);
        ticker.insertTickableFront(buff);
    }

    @Override
    public List<Pawn> getIntersectPawns(Bar bar) {
        List<Pawn> intersectPawns = new ArrayList<>();
        for (Pawn pawn : pawns.values()) {
            if (pawn.getBar().isColliding(bar)) {
                intersectPawns.add(pawn);
            }
        }
        return intersectPawns;
    }
}
