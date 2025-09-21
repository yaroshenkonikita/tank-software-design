package ru.mipt.bit.platformer.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.world.Field;

public class Tree extends Entity implements Field.Obstacle {

    public Tree(TextureRegion region, GridPoint2 pos, TiledMapTileLayer layer) {
        super(region, pos, layer);
    }

    @Override
    public boolean blocks(GridPoint2 tile) {
        return tile.equals(coords);
    }
}
