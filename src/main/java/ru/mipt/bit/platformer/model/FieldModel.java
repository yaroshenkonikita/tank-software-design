package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

public class FieldModel {
    public interface Obstacle {
        boolean blocks(GridPoint2 tile);
    }

    private final int width;
    private final int height;
    private final Array<Obstacle> obstacles = new Array<>();

    public FieldModel(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void addObstacle(Obstacle obstacle) {
        obstacles.add(obstacle);
    }

    public boolean inside(GridPoint2 t) {
        return t.x >= 0 && t.y >= 0 && t.x < width && t.y < height;
    }

    public boolean passable(GridPoint2 t) {
        if (!inside(t)) return false;
        for (Obstacle o : obstacles) {
            if (o.blocks(t)) return false;
        }
        return true;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}

