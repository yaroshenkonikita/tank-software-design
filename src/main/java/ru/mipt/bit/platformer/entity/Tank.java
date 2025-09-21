package ru.mipt.bit.platformer.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.world.Direction;
import ru.mipt.bit.platformer.world.Field;

import static com.badlogic.gdx.math.MathUtils.isEqual;
import static ru.mipt.bit.platformer.util.GdxGameUtils.continueProgress;

public class Tank extends Entity {
    private final GridPoint2 dest = new GridPoint2();
    private float progress = 1f;
    private final float moveSpeed;

    public Tank(TextureRegion region, GridPoint2 start, TiledMapTileLayer layer, float moveSpeed) {
        super(region, start, layer);
        this.dest.set(start);
        this.moveSpeed = moveSpeed;
    }

    public boolean isIdle() {
        return isEqual(progress, 1f);
    }

    public void tryMove(Direction dir, Field field) {
        if (!isIdle()) return;
        GridPoint2 next = dir.next(coords);
        rotationDeg = dir.rotationDeg;
        if (field.passable(next)) {
            dest.set(next);
            progress = 0f;
        }
    }

    public void update(float deltaTime, Field field) {
        field.mover().moveRectangleBetweenTileCenters(bounds, coords, dest, progress);
        progress = continueProgress(progress, deltaTime, moveSpeed);
        if (isEqual(progress, 1f)) {
            coords.set(dest);
        }
    }
}
