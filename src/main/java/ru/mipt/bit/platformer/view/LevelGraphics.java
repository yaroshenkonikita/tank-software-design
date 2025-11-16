package ru.mipt.bit.platformer.view;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import ru.mipt.bit.platformer.model.BulletModel;
import ru.mipt.bit.platformer.model.EntityModel;
import ru.mipt.bit.platformer.model.LevelListener;
import ru.mipt.bit.platformer.model.TankModel;
import ru.mipt.bit.platformer.model.TreeModel;

public class LevelGraphics implements LevelListener {
    private final FieldView fieldView;
    private final HealthBarsToggle healthBarsToggle;
    private final TextureRegion tankRegion;
    private final TextureRegion treeRegion;
    private final TextureRegion bulletRegion;

    private final ObjectMap<EntityModel, EntityView<?>> views = new ObjectMap<>();
    private final Array<EntityView<?>> orderedViews = new Array<>();

    public LevelGraphics(FieldView fieldView,
                         HealthBarsToggle toggle,
                         TextureRegion tankRegion,
                         TextureRegion treeRegion,
                         TextureRegion bulletRegion) {
        this.fieldView = fieldView;
        this.healthBarsToggle = toggle;
        this.tankRegion = tankRegion;
        this.treeRegion = treeRegion;
        this.bulletRegion = bulletRegion;
    }

    @Override
    public void entityAdded(EntityModel entity) {
        EntityView<?> view = createView(entity);
        views.put(entity, view);
        orderedViews.add(view);
        view.update(fieldView);
    }

    @Override
    public void entityRemoved(EntityModel entity) {
        EntityView<?> view = views.remove(entity);
        if (view != null) {
            orderedViews.removeValue(view, true);
        }
    }

    public void updateViews() {
        for (int i = 0; i < orderedViews.size; i++) {
            orderedViews.get(i).update(fieldView);
        }
    }

    public void render(Batch batch) {
        for (int i = 0; i < orderedViews.size; i++) {
            orderedViews.get(i).render(batch);
        }
    }

    private EntityView<?> createView(EntityModel entity) {
        if (entity instanceof TankModel) {
            TankView tankView = new TankView((TankModel) entity, new TextureRegion(tankRegion), fieldView);
            return new HealthBarDecorator<>(tankView, healthBarsToggle);
        }
        if (entity instanceof TreeModel) {
            return new TreeView((TreeModel) entity, new TextureRegion(treeRegion), fieldView);
        }
        if (entity instanceof BulletModel) {
            return new BulletView((BulletModel) entity, new TextureRegion(bulletRegion), fieldView);
        }
        throw new IllegalArgumentException("Unsupported entity type: " + entity.getClass().getSimpleName());
    }
}
