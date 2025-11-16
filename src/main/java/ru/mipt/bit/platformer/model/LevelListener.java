package ru.mipt.bit.platformer.model;

public interface LevelListener {
    void entityAdded(EntityModel entity);
    void entityRemoved(EntityModel entity);
}
