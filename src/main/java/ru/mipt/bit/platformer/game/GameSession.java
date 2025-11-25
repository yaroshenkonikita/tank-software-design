package ru.mipt.bit.platformer.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import ru.mipt.bit.platformer.input.CommandQueue;
import ru.mipt.bit.platformer.input.InputHandler;
import ru.mipt.bit.platformer.input.InputSource;
import ru.mipt.bit.platformer.model.GameWorld;
import ru.mipt.bit.platformer.view.FieldView;
import ru.mipt.bit.platformer.view.LevelGraphics;

public class GameSession {
    private final GameWorld gameWorld;
    private final LevelGraphics levelGraphics;
    private final FieldView fieldView;
    private final InputHandler inputHandler;
    private final InputSource inputSource;
    private final CommandQueue commands;
    private final EnemyAI enemyAI;
    private final DeltaTimeProvider deltaTimeProvider;

    public GameSession(GameWorld gameWorld,
                       LevelGraphics levelGraphics,
                       FieldView fieldView,
                       InputHandler inputHandler,
                       InputSource inputSource,
                       CommandQueue commands,
                       EnemyAI enemyAI,
                       DeltaTimeProvider deltaTimeProvider) {
        this.gameWorld = gameWorld;
        this.levelGraphics = levelGraphics;
        this.fieldView = fieldView;
        this.inputHandler = inputHandler;
        this.inputSource = inputSource;
        this.commands = commands;
        this.enemyAI = enemyAI;
        this.deltaTimeProvider = deltaTimeProvider;
        this.gameWorld.addListener(levelGraphics);
    }

    public void initialize() {
        refreshOccupancy();
    }

    public void update() {
        refreshOccupancy();
        inputHandler.handle(inputSource, commands);
        enemyAI.issueCommands(gameWorld, commands);
        commands.executeAll();
        float dt = deltaTimeProvider.getDeltaTime();
        gameWorld.update(dt);
        gameWorld.releaseFinishedReservations();
    }

    public void updateViews() {
        levelGraphics.updateViews();
    }

    public void renderField() {
        fieldView.render();
    }

    public void renderEntities(Batch batch) {
        levelGraphics.render(batch);
    }

    private void refreshOccupancy() {
        gameWorld.refreshOccupancy();
    }
}
