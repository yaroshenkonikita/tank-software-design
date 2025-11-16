package ru.mipt.bit.platformer.model;

public interface LevelObservable {
    void addListener(LevelListener listener);
    void removeListener(LevelListener listener);
}
