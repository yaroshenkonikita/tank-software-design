package ru.mipt.bit.platformer.view;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Interpolation;

import java.util.NoSuchElementException;

public class FieldView {
    private final TiledMap map;
    private final TiledMapTileLayer layer;
    private final MapRenderer renderer;
    private final TileMovement mover;

    public FieldView(TiledMap map, Batch batch, Interpolation interpolation) {
        this.map = map;
        this.layer = getFirstTileLayer(map);
        this.renderer = createMapRenderer(map, batch, layer);
        this.mover = new TileMovement(layer, interpolation);
    }

    public TiledMapTileLayer layer() { return layer; }

    public TileMovement mover() { return mover; }

    public void render() { renderer.render(); }

    public void dispose() { map.dispose(); }

    private static MapRenderer createMapRenderer(TiledMap tiledMap, Batch batch, TiledMapTileLayer tileLayer) {
        float viewWidth = tileLayer.getWidth() * tileLayer.getTileWidth();
        float viewHeight = tileLayer.getHeight() * tileLayer.getTileHeight();

        OrthogonalTiledMapRenderer mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);
        mapRenderer.getViewBounds().set(0f, 0f, viewWidth, viewHeight);

        return mapRenderer;
    }

    private static TiledMapTileLayer getFirstTileLayer(Map map) {
        MapLayers layers = map.getLayers();
        for (MapLayer l : layers) {
            if (l instanceof TiledMapTileLayer) {
                return (TiledMapTileLayer) l;
            }
        }
        throw new NoSuchElementException("Map has no tile layers");
    }
}
