package ru.mipt.bit.platformer.input;

import java.util.ArrayList;
import java.util.List;

public class CommandQueue {
    private final List<InputAction> queue = new ArrayList<>();

    public void enqueue(InputAction action) {
        if (action != null) queue.add(action);
    }

    public void executeAll() {
        for (int i = 0; i < queue.size(); i++) {
            queue.get(i).execute();
        }
        queue.clear();
    }
}
