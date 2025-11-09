package ru.mipt.bit.platformer.input;

import com.badlogic.gdx.utils.Array;

public class InputHandler {
    private static class Entry {
        final KeyBinding binding;
        final InputAction action;
        Entry(KeyBinding binding, InputAction action) {
            this.binding = binding;
            this.action = action;
        }
    }

    private final Array<Entry> entries = new Array<>();

    public InputHandler on(KeyBinding binding, InputAction action) {
        entries.add(new Entry(binding, action));
        return this;
    }

    public void handle(InputSource input) {
        for (Entry e : entries) {
            if (e.binding.isActive(input)) {
                e.action.execute();
                break;
            }
        }
    }

    public void handle(InputSource input, CommandQueue queue) {
        for (Entry e : entries) {
            if (e.binding.isActive(input)) {
                queue.enqueue(e.action);
                break;
            }
        }
    }
}
