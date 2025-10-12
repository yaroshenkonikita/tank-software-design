package ru.mipt.bit.platformer.input.actions;

import ru.mipt.bit.platformer.input.InputAction;
import ru.mipt.bit.platformer.model.Shooter;

public class ShootAction implements InputAction {
    private final Shooter shooter;

    public ShootAction(Shooter shooter) {
        this.shooter = shooter;
    }

    @Override
    public void execute() {
        shooter.requestShoot();
    }
}
