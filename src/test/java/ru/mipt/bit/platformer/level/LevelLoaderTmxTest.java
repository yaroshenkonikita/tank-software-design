package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LevelLoaderTmxTest {

    @BeforeAll
    static void initGdxFiles() {
        Gdx.files = new Lwjgl3Files();
    }

    @Test
    void testLoadFromTmxReadsDimensionsAndEnemyCount() {
        Level data = LevelLoader.load("src/main/resources/level.tmx");

        assertEquals(10, data.width);
        assertEquals(8, data.height);
        assertEquals(5, data.enemyCount);

        // Player start must be inside the bounds
        assertTrue(data.playerStart.x >= 0 && data.playerStart.x < data.width);
        assertTrue(data.playerStart.y >= 0 && data.playerStart.y < data.height);
    }

    @Test
    void testRandomIfPropertyEnabled() {
        Level data = LevelLoader.load("src/test/resources/levels/empty_map.tmx");
        assertEquals(10, data.width);
        assertEquals(10, data.height);
        assertEquals(3, data.enemyCount);
        assertEquals(15, data.treePositions.size);
        // Player inside bounds
        assertTrue(data.playerStart.x >= 0 && data.playerStart.x < data.width);
        assertTrue(data.playerStart.y >= 0 && data.playerStart.y < data.height);
    }
}
