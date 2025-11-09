package ru.mipt.bit.platformer.view;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.TankModel;

public class TankView extends EntityView<TankModel> {

    public TankView(TankModel model, TextureRegion region, FieldView fieldView) {
        super(model, region);
        Rectangle initial = fieldView.mover().rectangleAtTileCenter(bounds, model.getCoords());
        bounds.set(initial);
    }

    @Override
    public void update(FieldView fieldView) {
        Rectangle pos = fieldView.mover().rectangleBetweenTileCenters(bounds, model.getCoords(), model.getDestination(), model.getProgress());
        bounds.set(pos);
    }
}
