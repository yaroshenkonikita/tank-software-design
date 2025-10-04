package ru.mipt.bit.platformer.input.actions;

import ru.mipt.bit.platformer.input.InputAction;
import ru.mipt.bit.platformer.model.TankModel;

public class ShootAction implements InputAction {
    private final TankModel tank;

    public ShootAction(TankModel tank) {
        this.tank = tank;
    }

    @Override
    public void execute() {
        tank.requestShoot();
    }
}

