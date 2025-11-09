package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.Map;

public class CombinedPassability implements Passability, MovementReservationAccess {
    private final Passability[] delegates;
    private final MovementReservationAccess reservations;

    public CombinedPassability(Passability... delegates) {
        this.delegates = delegates;
        MovementReservationAccess found = null;
        for (Passability p : delegates) {
            if (p instanceof MovementReservationAccess) {
                found = (MovementReservationAccess) p;
                break;
            }
        }
        this.reservations = found;
    }

    @Override
    public boolean passable(GridPoint2 t) {
        for (Passability p : delegates) {
            if (!p.passable(t)) return false;
        }
        return true;
    }

    @Override
    public void setTankPositions(Map<Object, GridPoint2> positions) {
        if (reservations != null) reservations.setTankPositions(positions);
    }

    @Override
    public boolean tryReserve(GridPoint2 from, GridPoint2 to, Object token) {
        return reservations != null && reservations.tryReserve(from, to, token);
    }

    @Override
    public void release(Object token) {
        if (reservations != null) reservations.release(token);
    }
}
