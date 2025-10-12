package ru.mipt.bit.platformer.model;

import ru.mipt.bit.platformer.world.Direction;

public interface Movable {
    void tryMove(Direction dir, Passability field);
}

