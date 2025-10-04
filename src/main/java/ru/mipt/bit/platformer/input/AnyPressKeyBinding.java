package ru.mipt.bit.platformer.input;

public class AnyPressKeyBinding implements KeyBinding {
    private final int[] keys;

    public AnyPressKeyBinding(int... keys) {
        this.keys = keys;
    }

    @Override
    public boolean isActive(InputSource input) {
        for (int k : keys) {
            if (input.isKeyJustPressed(k)) return true;
        }
        return false;
    }
}

