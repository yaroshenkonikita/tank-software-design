package ru.mipt.bit.platformer.input;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputHandlerTest {

    static class FakeInput implements InputSource {
        int pressed = -1;
        int justPressed = -1;

        @Override
        public boolean isKeyPressed(int keyCode) {
            return keyCode == pressed;
        }

        @Override
        public boolean isKeyJustPressed(int keyCode) {
            return keyCode == justPressed;
        }
    }

    @Test
    void testHoldAndPressBindings() {
        FakeInput input = new FakeInput();

        int[] counter = {0};
        boolean[] shot = {false};

        InputHandler handler = new InputHandler()
                .on(new AnyHoldKeyBinding('W'), () -> counter[0]++)
                .on(new AnyPressKeyBinding(' '), () -> shot[0] = true);
        CommandQueue queue = new CommandQueue();

        // hold W
        input.pressed = 'W';
        handler.handle(input, queue);
        queue.executeAll();
        assertEquals(1, counter[0]);
        assertFalse(shot[0]);

        // press SPACE
        input.pressed = -1;
        input.justPressed = ' ';
        handler.handle(input, queue);
        queue.executeAll();
        assertTrue(shot[0]);
    }
}
