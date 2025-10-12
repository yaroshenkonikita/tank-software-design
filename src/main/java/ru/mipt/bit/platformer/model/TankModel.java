package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.world.Direction;

import static com.badlogic.gdx.math.MathUtils.isEqual;
import static ru.mipt.bit.platformer.util.GdxGameUtils.continueProgress;

public class TankModel extends EntityModel implements Movable, Shooter {
    private final GridPoint2 dest = new GridPoint2();
    private float progress = 1f;
    private static final float movementSpeed = 0.4f;
    private boolean shootRequested = false;

    public TankModel(GridPoint2 start) {
        super(start);
        this.dest.set(start);
    }

    public boolean isIdle() {
        return isEqual(progress, 1f);
    }

    @Override
    public void tryMove(Direction dir, Passability field) {
        if (!isIdle()) return;
        GridPoint2 next = dir.next(coords);
        rotationDeg = dir.rotationDeg;
        if (field.passable(next)) {
            dest.set(next);
            progress = 0f;
        }
    }

    public void update(float deltaTime) {
        progress = continueProgress(progress, deltaTime, movementSpeed);
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

    @Override
    public void requestShoot() {
        shootRequested = true;
    }

    public boolean consumeShootRequested() {
        boolean was = shootRequested;
        shootRequested = false;
        return was;
    }
}
