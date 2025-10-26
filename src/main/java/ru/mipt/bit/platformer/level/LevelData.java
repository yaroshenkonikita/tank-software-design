package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

public class LevelData {
    public final int width;
    public final int height;
    public final GridPoint2 playerStart;
    public final Array<GridPoint2> treePositions;

    public LevelData(int width, int height, GridPoint2 playerStart, Array<GridPoint2> treePositions) {
        this.width = width;
        this.height = height;
        this.playerStart = new GridPoint2(playerStart);
        this.treePositions = new Array<>(treePositions);
    }
}

