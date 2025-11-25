package ru.mipt.bit.platformer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.mipt.bit.platformer.input.AnyHoldKeyBinding;
import ru.mipt.bit.platformer.input.AnyPressKeyBinding;
import ru.mipt.bit.platformer.input.GdxInputSource;
import ru.mipt.bit.platformer.input.InputAction;
import ru.mipt.bit.platformer.input.InputHandler;
import ru.mipt.bit.platformer.input.InputSource;
import ru.mipt.bit.platformer.input.actions.ToggleHealthBarsAction;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameWorld;
import ru.mipt.bit.platformer.model.TankModel;
import ru.mipt.bit.platformer.view.HealthBarsToggle;

import static com.badlogic.gdx.Input.Keys.A;
import static com.badlogic.gdx.Input.Keys.D;
import static com.badlogic.gdx.Input.Keys.DOWN;
import static com.badlogic.gdx.Input.Keys.L;
import static com.badlogic.gdx.Input.Keys.LEFT;
import static com.badlogic.gdx.Input.Keys.RIGHT;
import static com.badlogic.gdx.Input.Keys.S;
import static com.badlogic.gdx.Input.Keys.SPACE;
import static com.badlogic.gdx.Input.Keys.UP;
import static com.badlogic.gdx.Input.Keys.W;

@Configuration
public class InputConfiguration {

    @Bean
    public InputSource inputSource() {
        return new GdxInputSource();
    }

    @Bean
    public InputHandler inputHandler(GameWorld gameWorld, HealthBarsToggle healthBarsToggle) {
        return new InputHandler()
                .on(new AnyHoldKeyBinding(UP, W), playerMoveAction(gameWorld, Direction.kUp))
                .on(new AnyHoldKeyBinding(LEFT, A), playerMoveAction(gameWorld, Direction.kLeft))
                .on(new AnyHoldKeyBinding(DOWN, S), playerMoveAction(gameWorld, Direction.kDown))
                .on(new AnyHoldKeyBinding(RIGHT, D), playerMoveAction(gameWorld, Direction.kRight))
                .on(new AnyPressKeyBinding(SPACE), playerShootAction(gameWorld))
                .on(new AnyPressKeyBinding(L), new ToggleHealthBarsAction(healthBarsToggle));
    }

    private InputAction playerMoveAction(GameWorld gameWorld, Direction direction) {
        return () -> {
            TankModel player = gameWorld.getPlayerTank();
            if (player != null) {
                player.tryMove(direction, gameWorld.getWorldPassability());
            }
        };
    }

    private InputAction playerShootAction(GameWorld gameWorld) {
        return () -> {
            TankModel player = gameWorld.getPlayerTank();
            if (player != null) {
                player.requestShoot();
            }
        };
    }
}
