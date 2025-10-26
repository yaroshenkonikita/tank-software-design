package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

public final class LevelLoader {
    private LevelLoader() {}

    public static LevelData fromFile(String fileName, int width, int height) {
        FileHandle handle = Gdx.files.internal(fileName);
        if (!handle.exists()) {
            throw new IllegalArgumentException("Level file not found: " + fileName);
        }
        String content = handle.readString("UTF-8");
        String[] lines = content.replace("\r\n", "\n").replace('\r', '\n').split("\n");

        Array<GridPoint2> trees = new Array<>();
        GridPoint2 player = null;

        for (int row = 0; row < lines.length; row++) {
            String line = lines[row];
            if (line.isEmpty()) continue;
            for (int col = 0; col < line.length(); col++) {
                char c = line.charAt(col);
                int x = col;
                int y = (height - 1) - row;
                if (x < 0 || x >= width || y < 0 || y >= height) {
                    continue;
                }
                if (c == 'T') {
                    trees.add(new GridPoint2(x, y));
                } else if (c == 'X') {
                    if (player != null) {
                        throw new IllegalStateException("Found second place for player");
                    }
                    player = new GridPoint2(x, y);
                }
            }
        }

        if (player == null) {
            throw new IllegalStateException("No place for player found in level file");
        }

        removeIfContains(trees, player.x, player.y);

        return new LevelData(width, height, player, trees);
    }

    public static LevelData random(int width, int height) {
        Random rnd = new java.util.Random();
        Array<GridPoint2> trees = new Array<>();
        int total = width * height;
        int target = Math.round(total * 0.15f);

        while (trees.size < target) {
            int x = rnd.nextInt(width);
            int y = rnd.nextInt(height);
            if (!contains(trees, x, y)) {
                trees.add(new GridPoint2(x, y));
            }
        }

        GridPoint2 player;
        while (true) {
            int x = rnd.nextInt(width);
            int y = rnd.nextInt(height);
            if (!contains(trees, x, y)) {
                player = new GridPoint2(x, y);
                break;
            }
        }

        return new LevelData(width, height, player, trees);
    }

    private static boolean contains(Array<GridPoint2> arr, int x, int y) {
        for (GridPoint2 p : arr) {
            if (p.x == x && p.y == y) return true;
        }
        return false;
    }

    private static void removeIfContains(Array<GridPoint2> arr, int x, int y) {
        for (int i = 0; i < arr.size; i++) {
            GridPoint2 p = arr.get(i);
            if (p.x == x && p.y == y) {
                arr.removeIndex(i);
                return;
            }
        }
    }
}

