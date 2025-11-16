package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import ru.mipt.bit.platformer.input.*;
import ru.mipt.bit.platformer.input.actions.MoveAction;
import ru.mipt.bit.platformer.input.actions.ShootAction;
import ru.mipt.bit.platformer.input.actions.ToggleHealthBarsAction;
import ru.mipt.bit.platformer.level.Level;
import ru.mipt.bit.platformer.level.LevelLoader;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameWorld;
import ru.mipt.bit.platformer.model.TankModel;
import ru.mipt.bit.platformer.view.FieldView;
import ru.mipt.bit.platformer.view.HealthBarDecorator;
import ru.mipt.bit.platformer.view.HealthBarsToggle;
import ru.mipt.bit.platformer.view.LevelGraphics;

import java.util.Random;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.Input.Keys.*;

public class GameDesktopLauncher implements ApplicationListener {

    private Batch batch;

    private FieldView fieldView;
    private GameWorld gameWorld;
    private LevelGraphics levelGraphics;

    private Texture blueTankTexture;
    private Texture greenTreeTexture;
    private Texture bulletTexture;

    private InputHandler inputHandler;
    private InputSource inputSource;
    private final CommandQueue commands = new CommandQueue();
    private final HealthBarsToggle healthBarsToggle = new HealthBarsToggle();
    private static final Direction[] DIRECTIONS = Direction.values();
    private final Random aiRandom = new Random();

    @Override
    public void create() {
        batch = new SpriteBatch();

        loadTextures();

        // build level: read from TMX config (player, trees, enemy count)
        Level levelData = LevelLoader.load("level.tmx");

        createFieldView(levelData);
        gameWorld = new GameWorld(levelData);
        levelGraphics = new LevelGraphics(fieldView, healthBarsToggle,
                new TextureRegion(blueTankTexture),
                new TextureRegion(greenTreeTexture),
                new TextureRegion(bulletTexture));
        gameWorld.addListener(levelGraphics);

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
        gameWorld.handleShootRequests();

        // Apply updates
        float dt = Gdx.graphics.getDeltaTime();
        gameWorld.update(dt);

        releaseFinishedReservations();
    }

    public void clearScreen() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
    }

    public void updateViews() {
        levelGraphics.updateViews();
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
        bulletTexture.dispose();
        fieldView.dispose();
        batch.dispose();
        HealthBarDecorator.disposeStatic();
    }

    

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        // level width: 10 tiles x 128px, height: 8 tiles x 128px
        config.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(), config);
    }

    private void refreshOccupancy() {
        gameWorld.refreshOccupancy();
    }

    private void loadTextures() {
        blueTankTexture = new Texture("images/tank_blue.png");
        greenTreeTexture = new Texture("images/greenTree.png");
        bulletTexture = createBulletTexture();
    }

    private Texture createBulletTexture() {
        Pixmap pixmap = new Pixmap(20, 20, Pixmap.Format.RGBA8888);
        pixmap.setColor(1f, 0.85f, 0f, 1f);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void createFieldView(Level levelData) {
        fieldView = new FieldView(levelData.map, batch, Interpolation.smooth);
    }

    private void setupInput() {
        inputSource = new GdxInputSource();
        inputHandler = createInputHandler();
    }


    private InputHandler createInputHandler() {
        return new InputHandler()
                .on(new AnyHoldKeyBinding(UP, W), playerMoveAction(Direction.kUp))
                .on(new AnyHoldKeyBinding(LEFT, A), playerMoveAction(Direction.kLeft))
                .on(new AnyHoldKeyBinding(DOWN, S), playerMoveAction(Direction.kDown))
                .on(new AnyHoldKeyBinding(RIGHT, D), playerMoveAction(Direction.kRight))
                .on(new AnyPressKeyBinding(SPACE), playerShootAction())
                .on(new AnyPressKeyBinding(L), new ToggleHealthBarsAction(healthBarsToggle));
    }

    private InputAction playerMoveAction(Direction direction) {
        return () -> {
            TankModel player = gameWorld.getPlayerTank();
            if (player != null) {
                player.tryMove(direction, gameWorld.getWorldPassability());
            }
        };
    }

    private InputAction playerShootAction() {
        return () -> {
            TankModel player = gameWorld.getPlayerTank();
            if (player != null) {
                player.requestShoot();
            }
        };
    }

    private void renderEntities() {
        levelGraphics.render(batch);
    }

    private void updateEnemyAI() {
        com.badlogic.gdx.utils.Array<TankModel> enemies = gameWorld.getEnemyTankModels();
        for (int i = 0; i < enemies.size; i++) {
            TankModel bot = enemies.get(i);
            if (!bot.isIdle()) continue;
            if (aiRandom.nextFloat() < 0.5f) {
                commands.enqueue(new ShootAction(bot));
            } else {
                Direction dir = DIRECTIONS[aiRandom.nextInt(DIRECTIONS.length)];
                commands.enqueue(new MoveAction(bot, gameWorld.getWorldPassability(), dir));
            }
        }
    }

    private void releaseFinishedReservations() {
        gameWorld.releaseFinishedReservations();
    }
}
