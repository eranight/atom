package ru.atom.gameserver.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import ru.atom.gameserver.geometry.Bar;
import ru.atom.gameserver.geometry.Point;

/**
 * Created by Alexandr on 05.12.2017.
 */
@JsonPropertyOrder({"id", "position"})
public abstract class AbstractGameObject implements GameObject {

    private final int id;
    private  Point position;
    private Bar bar;

    public AbstractGameObject(int id, Point position) {
        this.id = id;
        this.position = new Point(position);
        switch (getClass().getSimpleName()) {
            case "Pawn": this.bar = new Bar(position, 12, 12); break;
            default: this.bar = new Bar(position, 32, 32);
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public Bar getBar() {
        return bar;
    }

    @Override
    public void setBar(Bar bar) {
        this.bar = bar;
        this.position = bar.getOriginCorner();
    }
}
