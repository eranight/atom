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
    private final Point position;
    private final Bar bar;

    public AbstractGameObject(int id, Point position) {
        this.id = id;
        this.position = new Point(position);
        //в зависимости от того, это Pawn или нет, мы устанавливаем размер
        int halfOfObject;
        if (this.getClass() == Pawn.class) {
            halfOfObject = 10;
        }
        else {
            halfOfObject = 16;
        }
        this.bar = new Bar((int) position.getX() - halfOfObject, (int) position.getY() - halfOfObject,
                (int) position.getX() + halfOfObject, (int) position.getY() + halfOfObject);

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
    public Bar getBar() { return bar;   }
}
