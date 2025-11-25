package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import static com.badlogic.gdx.math.MathUtils.isEqual;

public class TankModel extends EntityModel implements Movable, Shooter, HealthProvider {
    private final GridPoint2 dest = new GridPoint2();
    private float progress = 1f;
    private static final float movementSpeed = 0.4f;
    private static final float shootCooldownSeconds = 1f;
    private float shootCooldownTimer = 0f;
    private final int maxHealth = 100;
    private int health;
    private Direction facing = Direction.kRight;
    private final Array<BulletSpawnListener> bulletSpawnListeners = new Array<>();

    public TankModel(GridPoint2 start) {
        super(start);
        this.dest.set(start);
        this.health = maxHealth;
        this.rotationDeg = facing.rotationDeg;
    }

    public boolean isIdle() {
        return isEqual(progress, 1f);
    }

    @Override
    public void tryMove(Direction dir, Passability field) {
        if (!isIdle()) return;
        rotationDeg = dir.rotationDeg;
        facing = dir;
        GridPoint2 next = dir.next(coords);
        if (field.passable(next)) {
            boolean reserved = true;
            if (field instanceof MovementReservationAccess) {
                reserved = ((MovementReservationAccess) field).tryReserve(coords, next, this);
            }
            if (reserved) {
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
        if (shootCooldownTimer > 0f) {
            shootCooldownTimer = Math.max(0f, shootCooldownTimer - deltaTime);
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
        if (canShoot()) {
            notifyBulletSpawnRequested();
        }
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

    public Direction getFacing() {
        return facing;
    }

    public boolean canShoot() {
        return shootCooldownTimer <= 0f;
    }

    void onShotFired() {
        shootCooldownTimer = shootCooldownSeconds;
    }

    public void addBulletSpawnListener(BulletSpawnListener listener) {
        if (listener == null || bulletSpawnListeners.contains(listener, true)) {
            return;
        }
        bulletSpawnListeners.add(listener);
    }

    public void removeBulletSpawnListener(BulletSpawnListener listener) {
        bulletSpawnListeners.removeValue(listener, true);
    }

    private void notifyBulletSpawnRequested() {
        for (int i = 0; i < bulletSpawnListeners.size; i++) {
            bulletSpawnListeners.get(i).onBulletSpawnRequested(this);
        }
    }
}
