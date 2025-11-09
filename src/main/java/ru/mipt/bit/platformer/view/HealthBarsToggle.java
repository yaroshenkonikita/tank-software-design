package ru.mipt.bit.platformer.view;

public class HealthBarsToggle {
    private boolean enabled = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void toggle() {
        enabled = !enabled;
    }
}

