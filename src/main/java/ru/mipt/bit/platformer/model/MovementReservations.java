package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MovementReservations {
    private final Map<Object, GridPoint2> tankPositions = new HashMap<>();
    private final Map<Object, Reservation> reservations = new HashMap<>();
    private final Set<String> reservedTiles = new HashSet<>();

    private static final class Reservation {
        final GridPoint2 from;
        final GridPoint2 to;
        Reservation(GridPoint2 from, GridPoint2 to) {
            this.from = new GridPoint2(from);
            this.to = new GridPoint2(to);
        }
    }

    public void setTankPositions(Map<Object, GridPoint2> positions) {
        tankPositions.clear();
        for (Map.Entry<Object, GridPoint2> e : positions.entrySet()) {
            GridPoint2 p = e.getValue();
            if (p != null) tankPositions.put(e.getKey(), new GridPoint2(p));
        }
    }

    public boolean tryReserve(GridPoint2 from, GridPoint2 to, Object token) {
        if (reservations.containsKey(token)) return false;
        String kFrom = key(from);
        String kTo = key(to);
        if (reservedTiles.contains(kFrom) || reservedTiles.contains(kTo)) return false;

        for (Map.Entry<Object, GridPoint2> e : tankPositions.entrySet()) {
            GridPoint2 pos = e.getValue();
            if (Objects.equals(e.getKey(), token)) continue;
            if ((pos.x == from.x && pos.y == from.y) || (pos.x == to.x && pos.y == to.y)) {
                return false;
            }
        }

        reservations.put(token, new Reservation(from, to));
        reservedTiles.add(kFrom);
        reservedTiles.add(kTo);
        return true;
    }

    public void release(Object token) {
        Reservation r = reservations.remove(token);
        if (r != null) {
            reservedTiles.remove(key(r.from));
            reservedTiles.remove(key(r.to));
        }
    }

    public boolean isReserved(GridPoint2 t) {
        return reservedTiles.contains(key(t));
    }

    public boolean isOccupied(GridPoint2 t) {
        for (GridPoint2 pos : tankPositions.values()) {
            if (pos.x == t.x && pos.y == t.y) return true;
        }
        return false;
    }

    private static String key(GridPoint2 t) {
        return t.x + ":" + t.y;
    }
}

