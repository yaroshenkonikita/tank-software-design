package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;

import static com.badlogic.gdx.math.MathUtils.isEqual;

public class TankModel extends EntityModel implements Movable, Shooter, HealthProvider {
    private final GridPoint2 dest = new GridPoint2();
    private float progress = 1f;
    private static final float movementSpeed = 0.4f;
    private boolean shootRequested = false;
    private final int maxHealth = 100;
    private int health;

    public TankModel(GridPoint2 start) {
        super(start);
        this.dest.set(start);
        this.health = maxHealth;
    }

    public boolean isIdle() {
        return isEqual(progress, 1f);
    }

    @Override
    public void tryMove(Direction dir, Passability field) {
        if (!isIdle()) return;
        GridPoint2 next = dir.next(coords);
        if (field.passable(next)) {
            boolean reserved = true;
            if (field instanceof MovementReservationAccess) {
                reserved = ((MovementReservationAccess) field).tryReserve(coords, next, this);
            }
            if (reserved) {
                rotationDeg = dir.rotationDeg;
                dest.set(next);
                progress = 0f;
            }
        }
    }

    public void update(float deltaTime) {
        progress = MathUtils.clamp(progress + deltaTime / movementSpeed, 0f, 1f);
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

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public int getMaxHealth() {
        return maxHealth;
    }

    public void damage(int amount) {
        if (amount <= 0) return;
        health = MathUtils.clamp(health - amount, 0, maxHealth);
    }

    public void heal(int amount) {
        if (amount <= 0) return;
        health = MathUtils.clamp(health + amount, 0, maxHealth);
    }
}
