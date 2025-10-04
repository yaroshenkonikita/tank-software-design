package ru.mipt.bit.platformer.view;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ru.mipt.bit.platformer.model.TankModel;
import ru.mipt.bit.platformer.util.GdxGameUtils;

public class TankView extends EntityView<TankModel> {

    public TankView(TankModel model, TextureRegion region, FieldView fieldView) {
        super(model, region);
        GdxGameUtils.moveRectangleAtTileCenter(fieldView.layer(), bounds, model.getCoords());
    }

    @Override
    public void update(FieldView fieldView) {
        fieldView.mover().moveRectangleBetweenTileCenters(bounds, model.getCoords(), model.getDestination(), model.getProgress());
    }
}

