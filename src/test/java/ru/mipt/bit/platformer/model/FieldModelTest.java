package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldModelTest {
    @Test
    void testInsideAndPassable() {
        FieldModel field = new FieldModel(3, 3);
        assertTrue(field.inside(new GridPoint2(0, 0)));
        assertTrue(field.inside(new GridPoint2(2, 2)));
        assertFalse(field.inside(new GridPoint2(-1, 0)));
        assertFalse(field.inside(new GridPoint2(0, 3)));

        // no obstacles
        assertTrue(field.passable(new GridPoint2(1, 1)));

        // add obstacle
        field.addObstacle(tile -> tile.equals(new GridPoint2(1, 1)));
        assertFalse(field.passable(new GridPoint2(1, 1)));
        assertTrue(field.passable(new GridPoint2(0, 0)));
    }
}

