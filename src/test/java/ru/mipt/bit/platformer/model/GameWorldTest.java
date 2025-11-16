package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.level.Level;

import static org.junit.jupiter.api.Assertions.*;

class GameWorldTest {

    @Test
    void testPlayerShotStopsOnTree() {
        Array<GridPoint2> trees = arrayOf(new GridPoint2(2, 1));
        GameWorld world = createWorld(new GridPoint2(1, 1), trees);
        TankModel player = world.getPlayerTank();
        assertNotNull(player);

        player.requestShoot();
        world.handleShootRequests();
        assertEquals(0, world.getBulletModels().size);
        assertTrue(world.hasBlockingObstacle(new GridPoint2(2, 1)));
    }

    @Test
    void testPlayerShotRemovesEnemyTank() {
        GameWorld world = createWorld(new GridPoint2(1, 1), new Array<>());
        TankModel enemy = world.addEnemy(new GridPoint2(2, 1));
        assertNotNull(enemy);

        TankModel player = world.getPlayerTank();
        assertNotNull(player);
        for (int i = 0; i < 4; i++) {
            player.requestShoot();
            world.handleShootRequests();
            world.update(1f);
        }

        assertEquals(0, enemy.getHealth());
        assertFalse(world.getEnemyTankModels().contains(enemy, true));
        assertFalse(world.hasTankAt(new GridPoint2(2, 1)));
    }

    private GameWorld createWorld(GridPoint2 playerStart, Array<GridPoint2> trees) {
        Level level = new Level(5, 5, playerStart, trees, 0, new TiledMap());
        return new GameWorld(level);
    }

    private Array<GridPoint2> arrayOf(GridPoint2... points) {
        Array<GridPoint2> array = new Array<>();
        for (GridPoint2 point : points) {
            array.add(new GridPoint2(point));
        }
        return array;
    }
}
