package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public class BulletModel extends EntityModel {
    private final Direction direction;
    private final TankModel shooter;
    private final int damage;
    private final float tileTravelTime;
    private float progress = 0f;
    private boolean destroyed = false;

    public BulletModel(GridPoint2 start, Direction direction, TankModel shooter, int damage, float tileTravelTime) {
        super(start);
        this.direction = direction;
        this.shooter = shooter;
        this.damage = damage;
        this.tileTravelTime = tileTravelTime;
        this.rotationDeg = direction.rotationDeg;
    }

    public Direction getDirection() {
        return direction;
    }

    public TankModel getShooter() {
        return shooter;
    }

    public int getDamage() {
        return damage;
    }

    public float getProgress() {
        return progress;
    }

    public GridPoint2 getDestination() {
        return direction.next(coords);
    }

    public void advance(float deltaTime) {
        if (destroyed) return;
        progress += deltaTime / tileTravelTime;
    }

    public boolean shouldStep() {
        return !destroyed && progress >= 1f;
    }

    public GridPoint2 stepForward() {
        progress -= 1f;
        coords.add(direction.dx, direction.dy);
        return getCoords();
    }

    public void destroy() {
        destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
