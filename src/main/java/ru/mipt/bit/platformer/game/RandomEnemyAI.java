package ru.mipt.bit.platformer.game;

import ru.mipt.bit.platformer.input.CommandQueue;
import ru.mipt.bit.platformer.input.actions.MoveAction;
import ru.mipt.bit.platformer.input.actions.ShootAction;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameWorld;
import ru.mipt.bit.platformer.model.TankModel;

import java.util.Random;

public class RandomEnemyAI implements EnemyAI {
    private static final Direction[] DIRECTIONS = Direction.values();
    private final Random random;

    public RandomEnemyAI(Random random) {
        this.random = random;
    }

    @Override
    public void issueCommands(GameWorld gameWorld, CommandQueue commands) {
        if (gameWorld == null) {
            return;
        }
        for (int i = 0; i < gameWorld.getEnemyTankModels().size; i++) {
            TankModel bot = gameWorld.getEnemyTankModels().get(i);
            if (!bot.isIdle()) {
                continue;
            }
            if (random.nextFloat() < 0.5f) {
                commands.enqueue(new ShootAction(bot));
            } else {
                Direction dir = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
                commands.enqueue(new MoveAction(bot, gameWorld.getWorldPassability(), dir));
            }
        }
    }
}
