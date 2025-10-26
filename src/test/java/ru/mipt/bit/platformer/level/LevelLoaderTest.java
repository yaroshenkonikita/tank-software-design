package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LevelLoaderTest {

    @BeforeAll
    static void initGdxFiles() {
        // Initialize libGDX file handling for tests
        Gdx.files = new Lwjgl3Files();
    }

    @Test
    void testLoadFromFileParsesLayout() {
        int width = 10;
        int height = 6;
        LevelData data = LevelLoader.fromFile("levels/sample_level.txt", width, height);

        assertEquals(width, data.width);
        assertEquals(height, data.height);

        // Player position from sample: row=4, col=5 -> y = (height-1) - row = 1
        assertEquals(new GridPoint2(5, 1), data.playerStart);

        // Total trees in sample: 15
        assertEquals(15, data.treePositions.size);

        // Spot-check a few tree coordinates based on the sample
        assertTrue(contains(data, 3, 5)); // row 0, col 3
        assertTrue(contains(data, 6, 5)); // row 0, col 6
        assertTrue(contains(data, 9, 4)); // row 1, col 9
        assertTrue(contains(data, 9, 3)); // row 2, col 9
        assertTrue(contains(data, 0, 2)); // row 3, col 0
        assertTrue(contains(data, 6, 2)); // row 3, col 6

        // Ensure player's tile is not occupied by a tree
        assertFalse(contains(data, data.playerStart.x, data.playerStart.y));
    }

    @Test
    void testRandomGenerationConstraints() {
        int width = 10;
        int height = 10;
        LevelData data = LevelLoader.random(width, height);

        // 15% of 100 is 15
        assertEquals(15, data.treePositions.size);

        Set<String> seen = new HashSet<>();
        for (int i = 0; i < data.treePositions.size; i++) {
            GridPoint2 p = data.treePositions.get(i);
            assertTrue(p.x >= 0 && p.x < width, "Tree x is out of bounds: " + p);
            assertTrue(p.y >= 0 && p.y < height, "Tree y is out of bounds: " + p);
            String key = p.x + "," + p.y;
            assertTrue(seen.add(key), "Duplicate tree position: " + key);
        }

        GridPoint2 player = data.playerStart;
        assertTrue(player.x >= 0 && player.x < width);
        assertTrue(player.y >= 0 && player.y < height);
        assertFalse(contains(data, player.x, player.y));
    }

    private static boolean contains(LevelData data, int x, int y) {
        for (int i = 0; i < data.treePositions.size; i++) {
            GridPoint2 p = data.treePositions.get(i);
            if (p.x == x && p.y == y) return true;
        }
        return false;
    }
}

