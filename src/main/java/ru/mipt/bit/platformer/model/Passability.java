package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public interface Passability {
    boolean passable(GridPoint2 t);
}

