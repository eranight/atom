package ru.atom.gameserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.atom.gameserver.geometry.Point;

public class Box extends AbstractGameObject {

    @JsonIgnore
    private Buff.BuffType buffType = null;

    public Box(int id, Point position) {
        super(id, position);
    }

    public boolean containsBuff() {
        return buffType != null;
    }

    public Buff.BuffType getBuffType() {
        return buffType;
    }

    public void setBuffType(Buff.BuffType buffType) {
        this.buffType = buffType;
    }
}
