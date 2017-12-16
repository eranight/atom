package ru.atom.gameserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.atom.gameserver.geometry.Bar;
import ru.atom.gameserver.geometry.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexandr on 06.12.2017.
 */
public class Pawn extends AbstractGameObject implements Movable {

    private float velocity;
    private int maxBombs;
    private int bombPower;
    private float speedModifier;
    private int height = 12;
    private int width = 12;
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
    public Bar move(Direction direction, long time) {
        float oldPosX = getBar().getOriginCorner().getX();
        float oldPosY = getBar().getOriginCorner().getY();
        Bar newBar = null;
        float delta = getVelocity() * time;
        switch (direction) {
            case UP: newBar = new Bar(new Point(oldPosX, oldPosY + delta), width, height); break;
            case RIGHT: newBar = new Bar(new Point(oldPosX + delta, oldPosY ), width, height); break;
            case DOWN: newBar = new Bar(new Point(oldPosX, oldPosY -delta ), width, height); break;
            case LEFT: newBar = new Bar(new Point(oldPosX -delta, oldPosY ), width, height); break;
        }
        return newBar;
    }
}
