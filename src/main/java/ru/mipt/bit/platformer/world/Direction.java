package ru.mipt.bit.platformer.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;

import static com.badlogic.gdx.Input.Keys.*;

public enum Direction {
    kUp(0, 1, 90f, new int[]{UP, W}),
    kLeft(-1, 0, 180f, new int[]{LEFT, A}),
    kDown(0, -1, -90f, new int[]{DOWN, S}),
    kRight(1, 0, 0f, new int[]{RIGHT, D});

    public final int dx;
    public final int dy;
    public final float rotationDeg;
    private final int[] keys;

    Direction(int dx, int dy, float rotationDeg, int[] keys) {
        this.dx = dx;
        this.dy = dy;
        this.rotationDeg = rotationDeg;
        this.keys = keys;
    }

    public boolean isPressed() {
        for (int k : keys) {
            if (Gdx.input.isKeyPressed(k)) return true;
        }
        return false;
    }

    public GridPoint2 next(GridPoint2 p) {
        return new GridPoint2(p).add(dx, dy);
    }
}
