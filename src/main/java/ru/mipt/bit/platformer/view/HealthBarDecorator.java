package ru.mipt.bit.platformer.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.EntityModel;
import ru.mipt.bit.platformer.model.HealthProvider;

public class HealthBarDecorator<M extends EntityModel & HealthProvider> extends EntityView<M> {
    private final EntityView<M> inner;
    private final HealthBarsToggle toggle;

    private static Texture pixel;

    public HealthBarDecorator(EntityView<M> inner, HealthBarsToggle toggle) {
        super(inner.model, inner.region);
        this.inner = inner;
        this.toggle = toggle;
        this.bounds.set(inner.bounds);
    }

    @Override
    public void update(FieldView fieldView) {
        inner.update(fieldView);
        this.bounds.set(inner.bounds);
    }

    @Override
    public void render(Batch batch) {
        inner.render(batch);
        if (!toggle.isEnabled()) return;
        renderHealthBar(batch, bounds, model);
    }

    private static void ensurePixel() {
        if (pixel == null) {
            Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pm.setColor(Color.WHITE);
            pm.fill();
            pixel = new Texture(pm);
            pm.dispose();
        }
    }

    private static void drawRect(Batch batch, float x, float y, float w, float h, Color color) {
        ensurePixel();

        Color old = new Color(batch.getColor());
        batch.setColor(color);
        batch.draw(new TextureRegion(pixel), x, y, w, h);
        batch.setColor(old);
    }

    private static void renderHealthBar(Batch batch, Rectangle bounds, HealthProvider hp) {
        float pad = 6f;
        float barWidth = Math.max(10f, bounds.width - 2f * pad);
        float barHeight = 8f;
        float x = bounds.x + pad;
        float y = bounds.y + bounds.height + 4f;

        float frac = Math.max(0f, Math.min(1f, (float) hp.getHealth() / (float) hp.getMaxHealth()));


        drawRect(batch, x - 1, y - 1, barWidth + 2, barHeight + 2, Color.BLACK);
        drawRect(batch, x, y, barWidth, barHeight, new Color(0.35f, 0f, 0f, 1f));

        float fillW = barWidth * frac;
        drawRect(batch, x, y, fillW, barHeight, Color.GREEN);
    }

    public static void disposeStatic() {
        if (pixel != null) {
            pixel.dispose();
            pixel = null;
        }
    }
}
