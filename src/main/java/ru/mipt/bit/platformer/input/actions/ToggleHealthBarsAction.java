package ru.mipt.bit.platformer.input.actions;

import ru.mipt.bit.platformer.input.InputAction;
import ru.mipt.bit.platformer.view.HealthBarsToggle;

public class ToggleHealthBarsAction implements InputAction {
    private final HealthBarsToggle toggle;

    public ToggleHealthBarsAction(HealthBarsToggle toggle) {
        this.toggle = toggle;
    }

    @Override
    public void execute() {
        toggle.toggle();
    }
}

