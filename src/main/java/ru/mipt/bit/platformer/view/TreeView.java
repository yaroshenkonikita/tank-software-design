package ru.mipt.bit.platformer.view;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ru.mipt.bit.platformer.model.TreeModel;
import ru.mipt.bit.platformer.util.GdxGameUtils;

public class TreeView extends EntityView<TreeModel> {
    public TreeView(TreeModel model, TextureRegion region, FieldView fieldView) {
        super(model, region);
        GdxGameUtils.moveRectangleAtTileCenter(fieldView.layer(), bounds, model.getCoords());
    }

    @Override
    public void update(FieldView fieldView) {
        GdxGameUtils.moveRectangleAtTileCenter(fieldView.layer(), bounds, model.getCoords());
    }
}

