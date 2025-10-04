package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.world.Direction;

import static com.badlogic.gdx.math.MathUtils.isEqual;
import static ru.mipt.bit.platformer.util.GdxGameUtils.continueProgress;

public class TankModel extends EntityModel {
    private final GridPoint2 dest = new GridPoint2();
    private float progress = 1f;
    private final float moveSpeed;
    private boolean shootRequested = false;

    public TankModel(GridPoint2 start, float moveSpeed) {
        super(start);
        this.dest.set(start);
        this.moveSpeed = moveSpeed;
    }

    public boolean isIdle() {
        return isEqual(progress, 1f);
    }

    public void tryMove(Direction dir, FieldModel field) {
        if (!isIdle()) return;
        GridPoint2 next = dir.next(coords);
        rotationDeg = dir.rotationDeg;
        if (field.passable(next)) {
            dest.set(next);
            progress = 0f;
        }
    }

    public void update(float deltaTime) {
        progress = continueProgress(progress, deltaTime, moveSpeed);
        if (isIdle()) {
            coords.set(dest);
        }
    }

    public GridPoint2 getDestination() {
        return new GridPoint2(dest);
    }

    public float getProgress() {
        return progress;
    }

    public void requestShoot() {
        shootRequested = true;
    }

    public boolean consumeShootRequested() {
        boolean was = shootRequested;
        shootRequested = false;
        return was;
    }
}

