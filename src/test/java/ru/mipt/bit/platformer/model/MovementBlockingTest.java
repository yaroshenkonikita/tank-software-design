package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.model.Direction;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class MovementBlockingTest {

    @Test
    void testReservationsAndOccupancyBlockMoves() {
        FieldModel field = new FieldModel(5, 5);
        MovementPassability movePass = new MovementPassability(new MovementReservations());
        CombinedPassability world = new CombinedPassability(field, movePass);

        TankModel a = new TankModel(new GridPoint2(1, 1));
        TankModel b = new TankModel(new GridPoint2(1, 2));

        HashMap<Object, GridPoint2> positions = new HashMap<>();
        positions.put(a, a.getCoords());
        positions.put(b, b.getCoords());
        movePass.setTankPositions(positions);

        // A cannot move into B's tile
        a.tryMove(Direction.kUp, world);
        assertTrue(a.isIdle());
        assertEquals(new GridPoint2(1, 1), a.getDestination());

        // B starts moving up, reserves (1,2) and (1,3)
        b.tryMove(Direction.kUp, world);
        assertFalse(b.isIdle());
        assertEquals(new GridPoint2(1, 3), b.getDestination());

        // Update positions (A and B) remain same for occupancy during movement
        positions.put(a, a.getCoords());
        positions.put(b, b.getCoords());
        movePass.setTankPositions(positions);

        // A still cannot move into reserved (1,2)
        a.tryMove(Direction.kUp, world);
        assertTrue(a.isIdle());

        // Another tank C cannot move into reserved (1,3)
        TankModel c = new TankModel(new GridPoint2(1, 4));
        positions.put(c, c.getCoords());
        movePass.setTankPositions(positions);
        c.tryMove(Direction.kDown, world);
        assertTrue(c.isIdle());

        // Finish B movement
        while (!b.isIdle()) {
            b.update(0.5f);
        }
        // Release reservation, update positions
        movePass.release(b);
        positions.put(b, b.getCoords());
        movePass.setTankPositions(positions);

        // Now A can move up to (1,2)
        a.tryMove(Direction.kUp, world);
        assertFalse(a.isIdle());
        assertEquals(new GridPoint2(1, 2), a.getDestination());
    }
}
