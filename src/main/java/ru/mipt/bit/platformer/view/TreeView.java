package ru.mipt.bit.platformer.view;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.TreeModel;

public class TreeView extends EntityView<TreeModel> {
    public TreeView(TreeModel model, TextureRegion region, FieldView fieldView) {
        super(model, region);
        Rectangle initial = fieldView.mover().rectangleAtTileCenter(bounds, model.getCoords());
        bounds.set(initial);
    }

    @Override
    public void update(FieldView fieldView) {
        Rectangle current = fieldView.mover().rectangleAtTileCenter(bounds, model.getCoords());
        bounds.set(current);
    }
}
