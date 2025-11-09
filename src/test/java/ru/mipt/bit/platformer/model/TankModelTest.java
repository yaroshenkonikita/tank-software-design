package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.model.Direction;

import static org.junit.jupiter.api.Assertions.*;

class TankModelTest {
    @Test
    void testMoveAndUpdate() {
        FieldModel field = new FieldModel(5, 5);
        TankModel tank = new TankModel(new GridPoint2(1, 1));

        assertTrue(tank.isIdle());
        tank.tryMove(Direction.kUp, field);
        assertFalse(tank.isIdle());
        assertEquals(new GridPoint2(1, 2), tank.getDestination());
        assertEquals(Direction.kUp.rotationDeg, tank.getRotationDeg());

        // progress to completion
        for (int i = 0; i < 10; i++) {
            assertFalse(tank.isIdle());
            tank.update(0.04f);
        }
        assertTrue(tank.isIdle());
        assertEquals(new GridPoint2(1, 2), tank.getCoords());
    }

    @Test
    void testBlockedMove() {
        FieldModel field = new FieldModel(5, 5);
        field.addObstacle(tile -> tile.equals(new GridPoint2(1, 2)));
        TankModel tank = new TankModel(new GridPoint2(1, 1));

        tank.tryMove(Direction.kUp, field);
        // should remain idle, destination unchanged
        assertTrue(tank.isIdle());
        assertEquals(new GridPoint2(1, 1), tank.getDestination());
        assertEquals(new GridPoint2(1, 1), tank.getCoords());
    }
}

