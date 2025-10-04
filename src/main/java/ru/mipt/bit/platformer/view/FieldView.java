package ru.mipt.bit.platformer.view;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Interpolation;
import ru.mipt.bit.platformer.util.GdxGameUtils;
import ru.mipt.bit.platformer.world.TileMovement;

public class FieldView {
    private final TiledMap map;
    private final TiledMapTileLayer layer;
    private final MapRenderer renderer;
    private final TileMovement mover;

    public FieldView(TiledMap map, Batch batch, Interpolation interpolation) {
        this.map = map;
        this.layer = GdxGameUtils.getSingleLayer(map);
        this.renderer = GdxGameUtils.createSingleLayerMapRenderer(map, batch);
        this.mover = new TileMovement(layer, interpolation);
    }

    public TiledMapTileLayer layer() { return layer; }

    public TileMovement mover() { return mover; }

    public void render() { renderer.render(); }

    public void dispose() { map.dispose(); }
}

