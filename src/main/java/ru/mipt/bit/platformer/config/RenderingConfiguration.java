package ru.mipt.bit.platformer.config;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.mipt.bit.platformer.level.Level;
import ru.mipt.bit.platformer.view.FieldView;
import ru.mipt.bit.platformer.view.HealthBarResources;
import ru.mipt.bit.platformer.view.HealthBarsToggle;
import ru.mipt.bit.platformer.view.LevelGraphics;

@Configuration
public class RenderingConfiguration {

    @Bean(destroyMethod = "dispose")
    public Batch batch() {
        return new SpriteBatch();
    }

    @Bean(name = "blueTankTexture", destroyMethod = "dispose")
    public Texture blueTankTexture() {
        return new Texture("images/tank_blue.png");
    }

    @Bean(name = "greenTreeTexture", destroyMethod = "dispose")
    public Texture greenTreeTexture() {
        return new Texture("images/greenTree.png");
    }

    @Bean(name = "bulletTexture", destroyMethod = "dispose")
    public Texture bulletTexture() {
        Pixmap pixmap = new Pixmap(20, 20, Pixmap.Format.RGBA8888);
        pixmap.setColor(1f, 0.85f, 0f, 1f);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    @Bean(destroyMethod = "dispose")
    public FieldView fieldView(Level level, Batch batch) {
        return new FieldView(level.map, batch, Interpolation.smooth);
    }

    @Bean
    public HealthBarsToggle healthBarsToggle() {
        return new HealthBarsToggle();
    }

    @Bean
    public LevelGraphics levelGraphics(FieldView fieldView,
                                       HealthBarsToggle healthBarsToggle,
                                       Texture blueTankTexture,
                                       Texture greenTreeTexture,
                                       Texture bulletTexture) {
        return new LevelGraphics(fieldView, healthBarsToggle,
                new TextureRegion(blueTankTexture),
                new TextureRegion(greenTreeTexture),
                new TextureRegion(bulletTexture));
    }

    @Bean(destroyMethod = "dispose")
    public HealthBarResources healthBarResources() {
        return new HealthBarResources();
    }
}
