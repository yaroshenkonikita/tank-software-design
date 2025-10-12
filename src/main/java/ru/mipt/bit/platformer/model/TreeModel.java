package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public class TreeModel extends EntityModel implements Obstacle {

    public TreeModel(GridPoint2 pos) {
        super(pos);
    }

    @Override
    public boolean blocks(GridPoint2 tile) {
        return tile.equals(coords);
    }
}
