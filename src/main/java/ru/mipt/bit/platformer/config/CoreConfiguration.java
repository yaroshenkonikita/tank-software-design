package ru.mipt.bit.platformer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.mipt.bit.platformer.game.DeltaTimeProvider;
import ru.mipt.bit.platformer.game.GdxDeltaTimeProvider;
import ru.mipt.bit.platformer.input.CommandQueue;
import ru.mipt.bit.platformer.level.Level;
import ru.mipt.bit.platformer.level.LevelLoader;
import ru.mipt.bit.platformer.model.GameWorld;

@Configuration
public class CoreConfiguration {

    @Bean
    public Level level() {
        return LevelLoader.load("level.tmx");
    }

    @Bean
    public GameWorld gameWorld(Level level) {
        return new GameWorld(level);
    }

    @Bean
    public CommandQueue commandQueue() {
        return new CommandQueue();
    }

    @Bean
    public DeltaTimeProvider deltaTimeProvider() {
        return new GdxDeltaTimeProvider();
    }
}
