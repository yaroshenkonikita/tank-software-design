package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {
    @Test
    void testVectorsAndRotation() {
        assertEquals(0, Direction.kUp.dx);
        assertEquals(1, Direction.kUp.dy);
        assertEquals(90f, Direction.kUp.rotationDeg);

        assertEquals(-1, Direction.kLeft.dx);
        assertEquals(0, Direction.kLeft.dy);
        assertEquals(180f, Direction.kLeft.rotationDeg);

        assertEquals(0, Direction.kDown.dx);
        assertEquals(-1, Direction.kDown.dy);
        assertEquals(-90f, Direction.kDown.rotationDeg);

        assertEquals(1, Direction.kRight.dx);
        assertEquals(0, Direction.kRight.dy);
        assertEquals(0f, Direction.kRight.rotationDeg);
    }

    @Test
    void testNext() {
        GridPoint2 p = new GridPoint2(5, 5);
        assertEquals(new GridPoint2(5, 6), Direction.kUp.next(p));
        assertEquals(new GridPoint2(4, 5), Direction.kLeft.next(p));
        assertEquals(new GridPoint2(5, 4), Direction.kDown.next(p));
        assertEquals(new GridPoint2(6, 5), Direction.kRight.next(p));
    }
}
