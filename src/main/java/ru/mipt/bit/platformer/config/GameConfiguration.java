package ru.mipt.bit.platformer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({GameSessionConfiguration.class})
public class GameConfiguration {
    // Aggregates modular configurations to keep launcher simple.
}
