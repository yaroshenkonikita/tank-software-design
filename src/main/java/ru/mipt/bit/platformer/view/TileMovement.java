package ru.mipt.bit.platformer.view;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;

public class TileMovement {

    private final TiledMapTileLayer tileLayer;
    private final Interpolation interpolation;

    public TileMovement(TiledMapTileLayer tileLayer, Interpolation interpolation) {
        this.tileLayer = tileLayer;
        this.interpolation = interpolation;
    }

    public Rectangle rectangleAtTileCenter(Rectangle shape, GridPoint2 tile) {
        int tileW = tileLayer.getTileWidth();
        int tileH = tileLayer.getTileHeight();
        float centerX = tile.x * tileW + tileW / 2f;
        float centerY = tile.y * tileH + tileH / 2f;
        return new Rectangle()
                .setWidth(shape.getWidth())
                .setHeight(shape.getHeight())
                .setCenter(centerX, centerY);
    }

    public Rectangle rectangleBetweenTileCenters(Rectangle shape, GridPoint2 fromTileCoordinates, GridPoint2 toTileCoordinates, float progress) {
        int tileW = tileLayer.getTileWidth();
        int tileH = tileLayer.getTileHeight();

        float fromBLX = fromTileCoordinates.x * tileW + (tileW - shape.getWidth()) / 2f;
        float fromBLY = fromTileCoordinates.y * tileH + (tileH - shape.getHeight()) / 2f;
        float toBLX = toTileCoordinates.x * tileW + (tileW - shape.getWidth()) / 2f;
        float toBLY = toTileCoordinates.y * tileH + (tileH - shape.getHeight()) / 2f;

        float x = interpolation.apply(fromBLX, toBLX, progress);
        float y = interpolation.apply(fromBLY, toBLY, progress);

        return new Rectangle(x, y, shape.getWidth(), shape.getHeight());
    }
}
