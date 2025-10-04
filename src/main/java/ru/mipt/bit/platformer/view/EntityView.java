package ru.mipt.bit.platformer.view;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.EntityModel;
import ru.mipt.bit.platformer.util.GdxGameUtils;

public abstract class EntityView<M extends EntityModel> {
    protected final M model;
    protected final TextureRegion region;
    protected final Rectangle bounds;

    protected EntityView(M model, TextureRegion region) {
        this.model = model;
        this.region = region;
        this.bounds = GdxGameUtils.createBoundingRectangle(region);
    }

    public abstract void update(FieldView fieldView);

    public void render(Batch batch) {
        GdxGameUtils.drawTextureRegionUnscaled(batch, region, bounds, model.getRotationDeg());
    }
}

