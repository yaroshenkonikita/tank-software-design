package ru.mipt.bit.platformer.model;

import ru.mipt.bit.platformer.model.Direction;

public interface Movable {
    void tryMove(Direction dir, Passability field);
}
