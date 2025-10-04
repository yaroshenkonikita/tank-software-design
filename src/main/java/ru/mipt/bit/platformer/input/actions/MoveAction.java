package ru.mipt.bit.platformer.input.actions;

import ru.mipt.bit.platformer.input.InputAction;
import ru.mipt.bit.platformer.model.FieldModel;
import ru.mipt.bit.platformer.model.TankModel;
import ru.mipt.bit.platformer.world.Direction;

public class MoveAction implements InputAction {
    private final TankModel tank;
    private final FieldModel field;
    private final Direction direction;

    public MoveAction(TankModel tank, FieldModel field, Direction direction) {
        this.tank = tank;
        this.field = field;
        this.direction = direction;
    }

    @Override
    public void execute() {
        tank.tryMove(direction, field);
    }
}

