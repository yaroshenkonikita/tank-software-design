package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public abstract class EntityModel {
    protected final GridPoint2 coords = new GridPoint2();
    protected float rotationDeg = 0f;

    protected EntityModel(GridPoint2 start) {
        this.coords.set(start);
    }

    public GridPoint2 getCoords() {
        return new GridPoint2(coords);
    }

    public float getRotationDeg() {
        return rotationDeg;
    }
}

