package ru.mipt.bit.platformer.world;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;
import ru.mipt.bit.platformer.util.GdxGameUtils;

public class Field {
    public interface Obstacle {
        boolean blocks(GridPoint2 tile);
    }

    private final TiledMap map;
    private final TiledMapTileLayer layer;
    private final MapRenderer renderer;
    private final TileMovement mover;
    private final Array<Obstacle> obstacles = new Array<>();

    public Field(TiledMap map, Batch batch, Interpolation interpolation) {
        this.map = map;
        this.layer = GdxGameUtils.getSingleLayer(map);
        this.renderer = GdxGameUtils.createSingleLayerMapRenderer(map, batch);
        this.mover = new TileMovement(layer, interpolation);
    }

    public TiledMapTileLayer layer() { return layer; }

    public TileMovement mover() { return mover; }

    public void addObstacle(Obstacle o) { obstacles.add(o); }

    public boolean inside(GridPoint2 t) {
        return t.x >= 0 && t.y >= 0 && t.x < layer.getWidth() && t.y < layer.getHeight();
    }

    public boolean passable(GridPoint2 t) {
        if (!inside(t)) return false;
        for (Obstacle o : obstacles) {
            if (o.blocks(t)) return false;
        }
        return true;
    }

    public void render() { renderer.render(); }

    public void dispose() { map.dispose(); }
}
