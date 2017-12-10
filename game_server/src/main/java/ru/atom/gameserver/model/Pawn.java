package ru.atom.gameserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.atom.gameserver.geometry.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexandr on 06.12.2017.
 */
public class Pawn extends AbstractGameObject implements Movable {

    private static final Logger logger = LoggerFactory.getLogger(Pawn.class);

    private float velocity;
    private int maxBombs;
    private int bombPower;
    private float speedModifier;
    @JsonIgnore
    private List<Bomb> bombs;

    public Pawn(int id, Point position, float velocity, int maxBombs) {
        super(id, position);
        this.velocity = velocity;
        this.maxBombs = maxBombs;
        this.bombPower = 1;
        this.speedModifier = 1.0f;
        bombs = new ArrayList<>();
    }

    public float getVelocity() {
        return velocity;
    }

    public int getMaxBombs() {
        return maxBombs;
    }

    public int getBombPower() {
        return bombPower;
    }

    public float getSpeedModifier() {
        return speedModifier;
    }

    @Override
    public void tick(long elapsed) {

    }

    @Override
    public Point move(Direction direction, long time) {
        Point lastPosition = getPosition();
        Point newPosition;
        float vel = getVelocity();
        switch (direction) {
            case UP: newPosition = new Point(lastPosition.getX(), lastPosition.getY() + time * vel); break;
            case RIGHT: newPosition = new Point(lastPosition.getX() + time * vel, lastPosition.getY()); break;
            case DOWN: newPosition = new Point(lastPosition.getX(), lastPosition.getY() - time * vel); break;
            case LEFT: newPosition = new Point(lastPosition.getX() - time * vel, lastPosition.getY()); break;
            default: newPosition = new Point(lastPosition.getX(), lastPosition.getY());
        }
        return newPosition;
    }
}
