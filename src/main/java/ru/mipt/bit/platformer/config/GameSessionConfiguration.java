package ru.mipt.bit.platformer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.mipt.bit.platformer.game.GameSession;
import ru.mipt.bit.platformer.input.CommandQueue;
import ru.mipt.bit.platformer.input.InputHandler;
import ru.mipt.bit.platformer.input.InputSource;
import ru.mipt.bit.platformer.game.DeltaTimeProvider;
import ru.mipt.bit.platformer.game.EnemyAI;
import ru.mipt.bit.platformer.model.GameWorld;
import ru.mipt.bit.platformer.view.FieldView;
import ru.mipt.bit.platformer.view.LevelGraphics;

@Configuration
@Import({CoreConfiguration.class, InputConfiguration.class, AiConfiguration.class, RenderingConfiguration.class})
public class GameSessionConfiguration {

    @Bean
    public GameSession gameSession(GameWorld gameWorld,
                                   LevelGraphics levelGraphics,
                                   FieldView fieldView,
                                   InputHandler inputHandler,
                                   InputSource inputSource,
                                   CommandQueue commandQueue,
                                   EnemyAI enemyAI,
                                   DeltaTimeProvider deltaTimeProvider) {
        return new GameSession(gameWorld, levelGraphics, fieldView, inputHandler, inputSource, commandQueue, enemyAI, deltaTimeProvider);
    }
}
