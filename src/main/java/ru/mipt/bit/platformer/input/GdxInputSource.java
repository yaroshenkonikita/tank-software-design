package ru.mipt.bit.platformer.input;

import com.badlogic.gdx.Gdx;

public class GdxInputSource implements InputSource {
    @Override
    public boolean isKeyPressed(int keyCode) {
        return Gdx.input.isKeyPressed(keyCode);
    }

    @Override
    public boolean isKeyJustPressed(int keyCode) {
        return Gdx.input.isKeyJustPressed(keyCode);
    }
}

