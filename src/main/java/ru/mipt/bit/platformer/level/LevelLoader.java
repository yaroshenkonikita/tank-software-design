package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

public final class LevelLoader {
    private LevelLoader() {}

    // Loads level from TMX-based config. If property randomLevel=true -> random layout,
    // otherwise tries to parse ASCII layout from <objectgroup><object><text>.
    public static Level load(String fileName) {
        FileHandle handle = Gdx.files.internal(fileName);
        if (!handle.exists()) {
            throw new IllegalArgumentException("TMX file not found: " + fileName);
        }
        XmlReader reader = new XmlReader();
        Element root = reader.parse(handle);

        // Read basic dimensions from TMX attributes
        int width = root.getIntAttribute("width", 10);
        int height = root.getIntAttribute("height", 10);

        // Load TMX with textures only when graphics context is available (not in headless tests)
        TiledMap map;
        if (Gdx.graphics != null) {
            map = new TmxMapLoader().load(fileName);
            int[] size = getMapSize(map);
            width = size[0];
            height = size[1];
        } else {
            int tileW = root.getIntAttribute("tilewidth", 128);
            int tileH = root.getIntAttribute("tileheight", 128);
            map = new TiledMap();
            TiledMapTileLayer layer = new TiledMapTileLayer(width, height, tileW, tileH);
            map.getLayers().add(layer);
        }

        MapConfig config = parseMapConfig(root);
        if (config.randomLevel) {
            return generateRandomLevel(width, height, config.enemyCount, config.treeCount, map);
        }

        Layout layout = readMap(root, width, height);
        if (layout == null) {
            throw new IllegalStateException("Bad or no map in config at path map.Config.Map");
        }

        GridPoint2 player = (layout.playerStart != null)
                ? layout.playerStart
                : fallbackFirstFreeTile(width, height, layout.trees);
        if (player == null) player = new GridPoint2(0, 0);

        removeIfContains(layout.trees, player.x, player.y);
        return new Level(width, height, player, layout.trees, config.enemyCount, map);
    }

    private static Level generateRandomLevel(int width, int height, int enemyCount, int treeCount, TiledMap map) {
        Random rnd = new Random();
        Array<GridPoint2> trees = new Array<>();
        int total = width * height;
        int target = (treeCount > 0) ? Math.min(treeCount, Math.max(0, total - enemyCount - 1)) : Math.round(total * 0.15f);

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

        return new Level(width, height, player, trees, enemyCount, map);
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

    private static class MapConfig {
        final boolean randomLevel;
        final int enemyCount;
        final int treeCount; // used only for random levels
        MapConfig(boolean randomLevel, int enemyCount, int treeCount) {
            this.randomLevel = randomLevel;
            this.enemyCount = enemyCount;
            this.treeCount = treeCount;
        }
    }

    private static class Layout {
        final Array<GridPoint2> trees;
        final GridPoint2 playerStart;
        Layout(Array<GridPoint2> trees, GridPoint2 playerStart) {
            this.trees = trees;
            this.playerStart = playerStart;
        }
    }

    private static MapConfig parseMapConfig(Element root) {
        boolean randomLevel = false;
        int enemyCount = 0;
        int treeCount = -1;
        Element props = root.getChildByName("properties");
        if (props != null) {
            for (int i = 0; i < props.getChildCount(); i++) {
                Element pr = props.getChild(i);
                if (!"property".equals(pr.getName())) continue;
                String name = pr.getAttribute("name", "");
                String val = pr.getAttribute("value", "");
                if ("enemyCount".equals(name)) {
                    try { enemyCount = Integer.parseInt(val); } catch (NumberFormatException ignored) {}
                } else if ("randomLevel".equals(name)) {
                    randomLevel = "true".equalsIgnoreCase(val) || "1".equals(val);
                } else if ("treeCount".equals(name)) {
                    try { treeCount = Integer.parseInt(val); } catch (NumberFormatException ignored) {}
                }
            }
        }
        return new MapConfig(randomLevel, enemyCount, treeCount);
    }

    private static Layout readMap(Element root, int width, int height) {
        String text = readAsciiTextObject(root, "Config", "Map");
        Array<String> lines = normalizeAsciiLines(text);
        if (lines == null || lines.size == 0) return null;

        Array<GridPoint2> trees = new Array<>();
        GridPoint2 player = null;
        for (int row = 0; row < lines.size; row++) {
            String line = lines.get(row);
            for (int col = 0; col < line.length(); col++) {
                char c = line.charAt(col);
                int x = col;
                int y = (height - 1) - row;
                if (x < 0 || x >= width || y < 0 || y >= height) continue;
                if (c == 'T') {
                    trees.add(new GridPoint2(x, y));
                } else if (c == 'X') {
                    if (player != null) throw new IllegalStateException("Multiple player markers in TMX data");
                    player = new GridPoint2(x, y);
                }
            }
        }
        return new Layout(trees, player);
    }

    private static Array<String> normalizeAsciiLines(String text) {
        if (text == null) return null;
        String[] split = text.replace("\r\n", "\n").replace('\r', '\n').split("\n");
        Array<String> out = new Array<>();
        for (String ln : split) {
            if (!ln.trim().isEmpty()) out.add(ln);
        }
        return out;
    }

    private static GridPoint2 fallbackFirstFreeTile(int width, int height, Array<GridPoint2> trees) {
        GridPoint2 player = null;
        outer: for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (!contains(trees, x, y)) { player = new GridPoint2(x, y); break outer; }
            }
        }
        return player;
    }

    private static int[] getMapSize(TiledMap map) {
        for (MapLayer l : map.getLayers()) {
            if (l instanceof TiledMapTileLayer) {
                TiledMapTileLayer tl = (TiledMapTileLayer) l;
                return new int[]{tl.getWidth(), tl.getHeight()};
            }
        }
        return new int[]{10, 10};
    }

    private static String getProperty(Element root, String name) {
        Element props = root.getChildByName("properties");
        if (props == null) return null;
        for (int i = 0; i < props.getChildCount(); i++) {
            Element pr = props.getChild(i);
            if (!"property".equals(pr.getName())) continue;
            if (name.equals(pr.getAttribute("name", ""))) {
                return pr.getAttribute("value", null);
            }
        }
        return null;
    }

    private static String readAsciiTextObject(Element root, String groupName, String objectName) {
        for (int i = 0; i < root.getChildCount(); i++) {
            Element ch = root.getChild(i);
            if (!"objectgroup".equals(ch.getName())) continue;
            if (!groupName.equals(ch.getAttribute("name", ""))) continue;
            for (int j = 0; j < ch.getChildCount(); j++) {
                Element obj = ch.getChild(j);
                if (!"object".equals(obj.getName())) continue;
                if (!objectName.equals(obj.getAttribute("name", ""))) continue;
                Element text = obj.getChildByName("text");
                if (text != null) return text.getText();
            }
        }
        return null;
    }
}
