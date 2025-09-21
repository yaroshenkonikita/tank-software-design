package ru.mipt.bit.platformer.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.util.GdxGameUtils;

public abstract class Entity {
    protected final TextureRegion region;
    protected final Rectangle bounds;
    protected final GridPoint2 coords = new GridPoint2();
    protected float rotationDeg = 0f;

    protected Entity(TextureRegion region, GridPoint2 start, TiledMapTileLayer layer) {
        this.region = region;
        this.bounds = GdxGameUtils.createBoundingRectangle(region);
        this.coords.set(start);
        GdxGameUtils.moveRectangleAtTileCenter(layer, bounds, coords);
    }

    public void render(Batch batch) {
        GdxGameUtils.drawTextureRegionUnscaled(batch, region, bounds, rotationDeg);
    }
}
