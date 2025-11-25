package ru.mipt.bit.platformer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.mipt.bit.platformer.game.EnemyAI;
import ru.mipt.bit.platformer.game.RandomEnemyAI;

import java.util.Random;

@Configuration
public class AiConfiguration {

    @Bean
    public Random aiRandom() {
        return new Random();
    }

    @Bean
    public EnemyAI enemyAI(Random aiRandom) {
        return new RandomEnemyAI(aiRandom);
    }
}
