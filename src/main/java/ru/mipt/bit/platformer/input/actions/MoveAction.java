package ru.mipt.bit.platformer.input.actions;

import ru.mipt.bit.platformer.input.InputAction;
import ru.mipt.bit.platformer.model.Movable;
import ru.mipt.bit.platformer.model.Passability;
import ru.mipt.bit.platformer.world.Direction;

public class MoveAction implements InputAction {
    private final Movable actor;
    private final Passability field;
    private final Direction direction;

    public MoveAction(Movable actor, Passability field, Direction direction) {
        this.actor = actor;
        this.field = field;
        this.direction = direction;
    }

    @Override
    public void execute() {
        actor.tryMove(direction, field);
    }
}
