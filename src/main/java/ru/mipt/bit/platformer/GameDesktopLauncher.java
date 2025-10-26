package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import ru.mipt.bit.platformer.input.*;
import ru.mipt.bit.platformer.input.actions.MoveAction;
import ru.mipt.bit.platformer.input.actions.ShootAction;
import ru.mipt.bit.platformer.model.FieldModel;
import ru.mipt.bit.platformer.model.TankModel;
import ru.mipt.bit.platformer.model.TreeModel;
import ru.mipt.bit.platformer.level.LevelData;
import ru.mipt.bit.platformer.level.LevelLoader;
import ru.mipt.bit.platformer.view.FieldView;
import ru.mipt.bit.platformer.view.TankView;
import ru.mipt.bit.platformer.view.TreeView;
import ru.mipt.bit.platformer.world.Direction;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.Input.Keys.*;

public class GameDesktopLauncher implements ApplicationListener {

    private Batch batch;

    private FieldView fieldView;

    private Texture blueTankTexture;
    private Texture greenTreeTexture;

    private TankModel playerTankModel;
    private TankView playerTankView;
    private com.badlogic.gdx.utils.Array<TreeView> treeViews = new com.badlogic.gdx.utils.Array<>();

    private InputHandler inputHandler;
    private InputSource inputSource;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // load level tiles
        TiledMap level = new TmxMapLoader().load("level.tmx");
        fieldView = new FieldView(level, batch, Interpolation.smooth);
        FieldModel fieldModel = new FieldModel(fieldView.layer().getWidth(), fieldView.layer().getHeight());

        // Texture decodes an image file and loads it into GPU memory, it represents a native resource
        blueTankTexture = new Texture("images/tank_blue.png");
        greenTreeTexture = new Texture("images/greenTree.png");

        // build level: either from file or randomly
        LevelData levelData = selectLevel(fieldView.layer().getWidth(), fieldView.layer().getHeight());

        // player
        playerTankModel = new TankModel(new GridPoint2(levelData.playerStart));
        playerTankView = new TankView(playerTankModel, new TextureRegion(blueTankTexture), fieldView);

        // obstacles (trees)
        for (com.badlogic.gdx.math.GridPoint2 pos : levelData.treePositions) {
            TreeModel treeModel = new TreeModel(new GridPoint2(pos));
            TreeView view = new TreeView(treeModel, new TextureRegion(greenTreeTexture), fieldView);
            treeViews.add(view);
            fieldModel.addObstacle(treeModel);
        }

        // input
        inputSource = new GdxInputSource();
        inputHandler = new InputHandler()
                // movement on hold
                .on(new AnyHoldKeyBinding(UP, W), new MoveAction(playerTankModel, fieldModel, Direction.kUp))
                .on(new AnyHoldKeyBinding(LEFT, A), new MoveAction(playerTankModel, fieldModel, Direction.kLeft))
                .on(new AnyHoldKeyBinding(DOWN, S), new MoveAction(playerTankModel, fieldModel, Direction.kDown))
                .on(new AnyHoldKeyBinding(RIGHT, D), new MoveAction(playerTankModel, fieldModel, Direction.kRight))
                // shooting on press
                .on(new AnyPressKeyBinding(SPACE), new ShootAction(playerTankModel));
    }

    @Override
    public void render() {
        /* update() */ {
            handleInput();
            updateGameState();
        }

        clearScreen();
        updateViews();

        fieldView.render();
        batch.begin();
        playerTankView.render(batch);
        for (int i = 0; i < treeViews.size; i++) {
            treeViews.get(i).render(batch);
        }
        batch.end();
    }

    public void handleInput() {
        inputHandler.handle(inputSource);
    }

    public void updateGameState() {
        playerTankModel.update(Gdx.graphics.getDeltaTime());
    }

    public void clearScreen() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
    }

    public void updateViews() {
        playerTankView.update(fieldView);
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

    private LevelData selectLevel(int width, int height) {
        try {
            return LevelLoader.fromFile("level.txt", width, height);
        } catch (IllegalArgumentException e) {
            // fallback: random if file not found
            return LevelLoader.random(width, height);
        }
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        // level width: 10 tiles x 128px, height: 8 tiles x 128px
        config.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(), config);
    }
}
