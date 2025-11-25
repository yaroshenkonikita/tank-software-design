package ru.mipt.bit.platformer.game;

import com.badlogic.gdx.Gdx;

public class GdxDeltaTimeProvider implements DeltaTimeProvider {
    @Override
    public float getDeltaTime() {
        return Gdx.graphics.getDeltaTime();
    }
}
