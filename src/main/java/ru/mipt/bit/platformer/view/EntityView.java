package ru.mipt.bit.platformer.view;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.EntityModel;

public abstract class EntityView<M extends EntityModel> {
    protected final M model;
    protected final TextureRegion region;
    protected final Rectangle bounds;

    protected EntityView(M model, TextureRegion region) {
        this.model = model;
        this.region = region;
        this.bounds = new Rectangle()
                .setWidth(region.getRegionWidth())
                .setHeight(region.getRegionHeight());
    }

    public abstract void update(FieldView fieldView);

    public void render(Batch batch) {
        int regionWidth = region.getRegionWidth();
        int regionHeight = region.getRegionHeight();
        float originX = regionWidth / 2f;
        float originY = regionHeight / 2f;
        batch.draw(region, bounds.x, bounds.y, originX, originY, regionWidth, regionHeight, 1f, 1f, model.getRotationDeg());
    }
}
