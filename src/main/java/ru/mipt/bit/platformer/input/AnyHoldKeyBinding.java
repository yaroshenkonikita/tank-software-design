package ru.mipt.bit.platformer.input;

public class AnyHoldKeyBinding implements KeyBinding {
    private final int[] keys;

    public AnyHoldKeyBinding(int... keys) {
        this.keys = keys;
    }

    @Override
    public boolean isActive(InputSource input) {
        for (int k : keys) {
            if (input.isKeyPressed(k)) return true;
        }
        return false;
    }
}

