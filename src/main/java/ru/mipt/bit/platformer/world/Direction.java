package ru.mipt.bit.platformer.world;

import com.badlogic.gdx.math.GridPoint2;

public enum Direction {
    kUp(0, 1, 90f),
    kLeft(-1, 0, 180f),
    kDown(0, -1, -90f),
    kRight(1, 0, 0f);

    public final int dx;
    public final int dy;
    public final float rotationDeg;

    Direction(int dx, int dy, float rotationDeg) {
        this.dx = dx;
        this.dy = dy;
        this.rotationDeg = rotationDeg;
    }

    public GridPoint2 next(GridPoint2 p) {
        return new GridPoint2(p).add(dx, dy);
    }
}
