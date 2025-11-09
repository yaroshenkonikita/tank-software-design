package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import ru.mipt.bit.platformer.input.*;
import ru.mipt.bit.platformer.input.actions.MoveAction;
import ru.mipt.bit.platformer.input.actions.ShootAction;
import ru.mipt.bit.platformer.model.FieldModel;
import ru.mipt.bit.platformer.model.TankModel;
import ru.mipt.bit.platformer.model.TreeModel;
import ru.mipt.bit.platformer.level.Level;
import ru.mipt.bit.platformer.level.LevelLoader;
import ru.mipt.bit.platformer.view.FieldView;
import ru.mipt.bit.platformer.view.TankView;
import ru.mipt.bit.platformer.view.TreeView;
import ru.mipt.bit.platformer.model.Direction;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.Input.Keys.*;

public class GameDesktopLauncher implements ApplicationListener {

    private Batch batch;

    private FieldView fieldView;
    private FieldModel fieldModel;

    private Texture blueTankTexture;
    private Texture greenTreeTexture;

    private TankModel playerTankModel;
    private TankView playerTankView;
    private com.badlogic.gdx.utils.Array<TankModel> enemyTankModels = new com.badlogic.gdx.utils.Array<>();
    private com.badlogic.gdx.utils.Array<TankView> enemyTankViews = new com.badlogic.gdx.utils.Array<>();
    private com.badlogic.gdx.utils.Array<TreeView> treeViews = new com.badlogic.gdx.utils.Array<>();

    private InputHandler inputHandler;
    private InputSource inputSource;
    private CommandQueue commands = new CommandQueue();
    private ru.mipt.bit.platformer.model.MovementPassability movementPassability;
    private ru.mipt.bit.platformer.model.CombinedPassability worldPassability;

    @Override
    public void create() {
        batch = new SpriteBatch();

        loadTextures();

        // build level: read from TMX config (player, trees, enemy count)
        Level levelData = LevelLoader.load("level.tmx");

        createField(levelData);
        placePlayer(levelData);
        buildObstacleTrees(levelData);
        buildEnemyTanks(levelData);

        // initialize dynamic occupancy before first input frame
        refreshOccupancy();

        setupInput();
    }
    
    private void update() {
            refreshOccupancy();
            handleInput();
            updateGameState();
        }

    @Override
    public void render() {
        update();
        clearScreen();
        updateViews();

        fieldView.render();
        batch.begin();
        renderEntities();
        batch.end();
    }

    public void handleInput() {
        inputHandler.handle(inputSource, commands);
    }

    public void updateGameState() {
        updateEnemyAI();
        commands.executeAll();

        // Apply updates
        float dt = Gdx.graphics.getDeltaTime();
        playerTankModel.update(dt);
        for (int i = 0; i < enemyTankModels.size; i++) {
            enemyTankModels.get(i).update(dt);
        }

        releaseFinishedReservations();
    }

    public void clearScreen() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
    }

    public void updateViews() {
        playerTankView.update(fieldView);
        for (int i = 0; i < enemyTankViews.size; i++) {
            enemyTankViews.get(i).update(fieldView);
        }
        for (int i = 0; i < treeViews.size; i++) {
            treeViews.get(i).update(fieldView);
        }
    }

    @Override
    public void resize(int width, int height) {
        // do not react to window resizing
    }

    @Override
    public void pause() {
        // game doesn't get paused
    }

    @Override
    public void resume() {
        // game doesn't get paused
    }

    @Override
    public void dispose() {
        // dispose of all the native resources (classes which implement com.badlogic.gdx.utils.Disposable)
        blueTankTexture.dispose();
        greenTreeTexture.dispose();
        fieldView.dispose();
        batch.dispose();
    }

    

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        // level width: 10 tiles x 128px, height: 8 tiles x 128px
        config.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(), config);
    }

    private void refreshOccupancy() {
        java.util.HashMap<Object, GridPoint2> positions = new java.util.HashMap<>();
        if (playerTankModel != null) {
            positions.put(playerTankModel, playerTankModel.getCoords());
        }
        for (int i = 0; i < enemyTankModels.size; i++) {
            TankModel t = enemyTankModels.get(i);
            positions.put(t, t.getCoords());
        }
        movementPassability.setTankPositions(positions);
    }

    private void loadTextures() {
        blueTankTexture = new Texture("images/tank_blue.png");
        greenTreeTexture = new Texture("images/greenTree.png");
    }

    private void createField(Level levelData) {
        fieldView = new FieldView(levelData.map, batch, Interpolation.smooth);
        fieldModel = new FieldModel(levelData.width, levelData.height);
        movementPassability = new ru.mipt.bit.platformer.model.MovementPassability(new ru.mipt.bit.platformer.model.MovementReservations());
        worldPassability = new ru.mipt.bit.platformer.model.CombinedPassability(fieldModel, movementPassability);
    }

    private void placePlayer(Level levelData) {
        playerTankModel = new TankModel(new GridPoint2(levelData.playerStart));
        playerTankView = new TankView(playerTankModel, new TextureRegion(blueTankTexture), fieldView);
    }

    private void setupInput() {
        inputSource = new GdxInputSource();
        inputHandler = createInputHandler();
    }

    private void buildObstacleTrees(Level levelData) {
        for (com.badlogic.gdx.math.GridPoint2 pos : levelData.treePositions) {
            TreeModel treeModel = new TreeModel(new GridPoint2(pos));
            TreeView view = new TreeView(treeModel, new TextureRegion(greenTreeTexture), fieldView);
            treeViews.add(view);
            fieldModel.addObstacle(treeModel);
        }
    }

    private void buildEnemyTanks(Level levelData) {
        java.util.Random rnd = new java.util.Random();
        java.util.HashSet<String> occupied = new java.util.HashSet<>();
        occupied.add(levelData.playerStart.x + "," + levelData.playerStart.y);
        for (int i = 0; i < levelData.treePositions.size; i++) {
            com.badlogic.gdx.math.GridPoint2 p = levelData.treePositions.get(i);
            occupied.add(p.x + "," + p.y);
        }
        int attempts = 0;
        int maxAttempts = levelData.width * levelData.height * 10;
        while (enemyTankModels.size < levelData.enemyCount && attempts++ < maxAttempts) {
            int x = rnd.nextInt(levelData.width);
            int y = rnd.nextInt(levelData.height);
            String key = x + "," + y;
            if (occupied.contains(key)) continue;
            com.badlogic.gdx.math.GridPoint2 pos = new com.badlogic.gdx.math.GridPoint2(x, y);
            if (!worldPassability.passable(pos)) continue;
            TankModel enemy = new TankModel(new GridPoint2(pos));
            enemyTankModels.add(enemy);
            enemyTankViews.add(new TankView(enemy, new TextureRegion(blueTankTexture), fieldView));
            occupied.add(key);
        }
    }

    private InputHandler createInputHandler() {
        return new InputHandler()
                .on(new AnyHoldKeyBinding(UP, W), new MoveAction(playerTankModel, worldPassability, Direction.kUp))
                .on(new AnyHoldKeyBinding(LEFT, A), new MoveAction(playerTankModel, worldPassability, Direction.kLeft))
                .on(new AnyHoldKeyBinding(DOWN, S), new MoveAction(playerTankModel, worldPassability, Direction.kDown))
                .on(new AnyHoldKeyBinding(RIGHT, D), new MoveAction(playerTankModel, worldPassability, Direction.kRight))
                .on(new AnyPressKeyBinding(SPACE), new ShootAction(playerTankModel));
    }

    private void renderEntities() {
        playerTankView.render(batch);
        for (int i = 0; i < enemyTankViews.size; i++) {
            enemyTankViews.get(i).render(batch);
        }
        for (int i = 0; i < treeViews.size; i++) {
            treeViews.get(i).render(batch);
        }
    }

    private void updateEnemyAI() {
        java.util.Random rnd = new java.util.Random();
        for (int i = 0; i < enemyTankModels.size; i++) {
            TankModel bot = enemyTankModels.get(i);
            if (bot.isIdle()) {
                Direction[] dirs = Direction.values();
                Direction dir = dirs[rnd.nextInt(4)];
                commands.enqueue(new MoveAction(bot, worldPassability, dir));
            }
        }
    }

    private void releaseFinishedReservations() {
        if (playerTankModel.isIdle()) {
            movementPassability.release(playerTankModel);
        }
        for (int i = 0; i < enemyTankModels.size; i++) {
            TankModel t = enemyTankModels.get(i);
            if (t.isIdle()) movementPassability.release(t);
        }
    }
}
