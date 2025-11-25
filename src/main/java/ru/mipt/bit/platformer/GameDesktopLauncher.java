package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.Batch;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.mipt.bit.platformer.config.GameSessionConfiguration;
import ru.mipt.bit.platformer.game.GameSession;
import ru.mipt.bit.platformer.view.FieldView;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public class GameDesktopLauncher implements ApplicationListener {

    private Batch batch;
    private GameSession gameSession;
    private FieldView fieldView;

    private AnnotationConfigApplicationContext applicationContext;

    @Override
    public void create() {
        applicationContext = new AnnotationConfigApplicationContext(GameSessionConfiguration.class);

        batch = applicationContext.getBean(Batch.class);
        gameSession = applicationContext.getBean(GameSession.class);
        fieldView = applicationContext.getBean(FieldView.class);

        // initialize dynamic occupancy before first input frame
        gameSession.initialize();
    }

    private void update() {
        gameSession.update();
    }

    @Override
    public void render() {
        update();
        clearScreen();
        gameSession.updateViews();

        gameSession.renderField();
        batch.begin();
        gameSession.renderEntities(batch);
        batch.end();
    }

    public void clearScreen() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
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
        if (applicationContext != null) {
            applicationContext.close();
        }
    }

    

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        // level width: 10 tiles x 128px, height: 8 tiles x 128px
        config.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(), config);
    }
}
