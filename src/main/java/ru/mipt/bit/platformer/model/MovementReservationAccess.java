package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.Map;

public interface MovementReservationAccess {
    void setTankPositions(Map<Object, GridPoint2> positions);
    boolean tryReserve(GridPoint2 from, GridPoint2 to, Object token);
    void release(Object token);
}

