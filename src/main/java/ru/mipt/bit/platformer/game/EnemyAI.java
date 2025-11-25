package ru.mipt.bit.platformer.game;

import ru.mipt.bit.platformer.input.CommandQueue;
import ru.mipt.bit.platformer.model.GameWorld;

public interface EnemyAI {
    void issueCommands(GameWorld gameWorld, CommandQueue commands);
}
