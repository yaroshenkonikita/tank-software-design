package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.Map;

public class MovementPassability implements Passability, MovementReservationAccess {
    private final MovementReservations reservations;

    public MovementPassability(MovementReservations reservations) {
        this.reservations = reservations;
    }

    @Override
    public boolean passable(GridPoint2 t) {
        return !reservations.isReserved(t) && !reservations.isOccupied(t);
    }

    @Override
    public void setTankPositions(Map<Object, GridPoint2> positions) {
        reservations.setTankPositions(positions);
    }

    @Override
    public boolean tryReserve(GridPoint2 from, GridPoint2 to, Object token) {
        return reservations.tryReserve(from, to, token);
    }

    @Override
    public void release(Object token) {
        reservations.release(token);
    }
}

