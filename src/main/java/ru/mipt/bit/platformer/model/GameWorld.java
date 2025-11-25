package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import ru.mipt.bit.platformer.level.Level;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class GameWorld implements LevelObservable, BulletSpawnListener {
    private static final int BULLET_DAMAGE = 25;
    private static final float BULLET_TILE_TIME = 0.12f;

    private final FieldModel fieldModel;
    private final MovementReservations reservations;
    private final MovementPassability movementPassability;
    private final CombinedPassability worldPassability;
    private final Array<TreeModel> treeModels = new Array<>();
    private final Array<TankModel> enemyTankModels = new Array<>();
    private final Array<BulletModel> bulletModels = new Array<>();
    private final Array<BulletModel> bulletsToRemove = new Array<>();
    private final Array<LevelListener> listeners = new Array<>();
    private final Random random = new Random();

    private TankModel playerTank;

    public GameWorld(Level levelData) {
        this.fieldModel = new FieldModel(levelData.width, levelData.height);
        this.reservations = new MovementReservations();
        this.movementPassability = new MovementPassability(reservations);
        this.worldPassability = new CombinedPassability(fieldModel, movementPassability);

        spawnPlayer(levelData.playerStart);
        buildTrees(levelData);
        spawnEnemies(levelData);
    }

    public FieldModel getFieldModel() {
        return fieldModel;
    }

    public CombinedPassability getWorldPassability() {
        return worldPassability;
    }

    public MovementPassability getMovementPassability() {
        return movementPassability;
    }

    public TankModel getPlayerTank() {
        return playerTank;
    }

    public Array<TankModel> getEnemyTankModels() {
        return enemyTankModels;
    }

    public Array<BulletModel> getBulletModels() {
        return bulletModels;
    }

    public boolean hasBlockingObstacle(GridPoint2 tile) {
        return fieldModel.hasObstacle(tile);
    }

    public boolean hasTankAt(GridPoint2 tile) {
        return findTankAt(tile) != null;
    }

    public TankModel addEnemy(GridPoint2 position) {
        if (position == null) return null;
        if (!fieldModel.inside(position)) return null;
        if (fieldModel.hasObstacle(position)) return null;
        if (findTankAt(position) != null) return null;
        if (!worldPassability.passable(position)) return null;
        TankModel tank = new TankModel(new GridPoint2(position));
        enemyTankModels.add(tank);
        notifyAdded(tank);
        return tank;
    }

    @Override
    public void addListener(LevelListener listener) {
        if (listener == null || listeners.contains(listener, true)) return;
        listeners.add(listener);
        emitSnapshot(listener);
    }

    @Override
    public void removeListener(LevelListener listener) {
        listeners.removeValue(listener, true);
    }

    public void refreshOccupancy() {
        HashMap<Object, GridPoint2> positions = new HashMap<>();
        if (playerTank != null) positions.put(playerTank, playerTank.getCoords());
        for (int i = 0; i < enemyTankModels.size; i++) {
            TankModel tank = enemyTankModels.get(i);
            positions.put(tank, tank.getCoords());
        }
        movementPassability.setTankPositions(positions);
    }

    public void releaseFinishedReservations() {
        if (playerTank != null && playerTank.isIdle()) {
            movementPassability.release(playerTank);
        }
        for (int i = 0; i < enemyTankModels.size; i++) {
            TankModel tank = enemyTankModels.get(i);
            if (tank.isIdle()) {
                movementPassability.release(tank);
            }
        }
    }

    public void update(float deltaTime) {
        if (playerTank != null) {
            playerTank.update(deltaTime);
        }
        for (int i = 0; i < enemyTankModels.size; i++) {
            enemyTankModels.get(i).update(deltaTime);
        }
        updateBullets(deltaTime);
        cleanupBullets();
    }

    private void spawnPlayer(GridPoint2 start) {
        playerTank = new TankModel(new GridPoint2(start));
        playerTank.addBulletSpawnListener(this);
        notifyAdded(playerTank);
    }

    private void buildTrees(Level levelData) {
        for (int i = 0; i < levelData.treePositions.size; i++) {
            GridPoint2 pos = levelData.treePositions.get(i);
            TreeModel tree = new TreeModel(new GridPoint2(pos));
            treeModels.add(tree);
            fieldModel.addObstacle(tree);
            notifyAdded(tree);
        }
    }

    private void spawnEnemies(Level levelData) {
        HashSet<String> occupied = new HashSet<>();
        if (playerTank != null) {
            occupied.add(key(playerTank.getCoords()));
        }
        for (int i = 0; i < treeModels.size; i++) {
            occupied.add(key(treeModels.get(i).getCoords()));
        }

        int attempts = 0;
        int maxAttempts = levelData.width * levelData.height * 10;
        while (enemyTankModels.size < levelData.enemyCount && attempts++ < maxAttempts) {
            int x = random.nextInt(levelData.width);
            int y = random.nextInt(levelData.height);
            GridPoint2 pos = new GridPoint2(x, y);
            String k = key(pos);
            if (occupied.contains(k)) continue;
            if (!worldPassability.passable(pos)) continue;
            TankModel enemy = new TankModel(pos);
            enemy.addBulletSpawnListener(this);
            enemyTankModels.add(enemy);
            occupied.add(k);
            notifyAdded(enemy);
        }
    }

    private static String key(GridPoint2 p) {
        return p.x + "," + p.y;
    }

    private void updateBullets(float deltaTime) {
        for (int i = 0; i < bulletModels.size; i++) {
            BulletModel bullet = bulletModels.get(i);
            bullet.advance(deltaTime);
            while (bullet.shouldStep()) {
                bullet.stepForward();
                handleBulletEnterTile(bullet);
                if (bullet.isDestroyed()) break;
            }
        }
    }

    @Override
    public void onBulletSpawnRequested(TankModel shooter) {
        spawnBullet(shooter);
    }

    private void spawnBullet(TankModel shooter) {
        if (shooter == null) return;
        Direction direction = shooter.getFacing();
        GridPoint2 origin = direction.next(shooter.getCoords());
        if (!fieldModel.inside(origin)) return;
        BulletModel bullet = new BulletModel(new GridPoint2(origin), direction, shooter, BULLET_DAMAGE, BULLET_TILE_TIME);
        bulletModels.add(bullet);
        shooter.onShotFired();
        notifyAdded(bullet);
        handleBulletEnterTile(bullet);
        cleanupBullets();
    }

    private void handleBulletEnterTile(BulletModel bullet) {
        if (bullet.isDestroyed()) return;
        GridPoint2 tile = bullet.getCoords();
        if (!fieldModel.inside(tile)) {
            destroyBullet(bullet);
            return;
        }
        if (fieldModel.hasObstacle(tile)) {
            destroyBullet(bullet);
            return;
        }

        TankModel tank = findTankAt(tile);
        if (tank != null && tank != bullet.getShooter()) {
            tank.damage(bullet.getDamage());
            if (tank.getHealth() <= 0) {
                removeTank(tank);
            }
            destroyBullet(bullet);
            return;
        }

        BulletModel other = findOtherBulletAt(bullet, tile);
        if (other != null) {
            destroyBullet(other);
            destroyBullet(bullet);
        }
    }

    private TankModel findTankAt(GridPoint2 tile) {
        if (playerTank != null) {
            GridPoint2 playerCoords = playerTank.getCoords();
            if (playerCoords.x == tile.x && playerCoords.y == tile.y) {
                return playerTank;
            }
        }
        for (int i = 0; i < enemyTankModels.size; i++) {
            TankModel tank = enemyTankModels.get(i);
            GridPoint2 coords = tank.getCoords();
            if (coords.x == tile.x && coords.y == tile.y) {
                return tank;
            }
        }
        return null;
    }

    private BulletModel findOtherBulletAt(BulletModel source, GridPoint2 tile) {
        for (int i = 0; i < bulletModels.size; i++) {
            BulletModel bullet = bulletModels.get(i);
            if (bullet == source || bullet.isDestroyed()) continue;
            GridPoint2 coords = bullet.getCoords();
            if (coords.x == tile.x && coords.y == tile.y) {
                return bullet;
            }
        }
        return null;
    }

    private void destroyBullet(BulletModel bullet) {
        if (bullet.isDestroyed()) return;
        bullet.destroy();
        bulletsToRemove.add(bullet);
    }

    private void cleanupBullets() {
        if (bulletsToRemove.size == 0) return;
        for (int i = 0; i < bulletsToRemove.size; i++) {
            BulletModel bullet = bulletsToRemove.get(i);
            bulletModels.removeValue(bullet, true);
            notifyRemoved(bullet);
        }
        bulletsToRemove.clear();
    }

    private void removeTank(TankModel tank) {
        if (tank == null) return;
        tank.removeBulletSpawnListener(this);
        movementPassability.release(tank);
        if (tank == playerTank) {
            playerTank = null;
        } else {
            enemyTankModels.removeValue(tank, true);
        }
        notifyRemoved(tank);
    }

    private void notifyAdded(EntityModel entity) {
        for (int i = 0; i < listeners.size; i++) {
            listeners.get(i).entityAdded(entity);
        }
    }

    private void notifyRemoved(EntityModel entity) {
        for (int i = 0; i < listeners.size; i++) {
            listeners.get(i).entityRemoved(entity);
        }
    }

    private void emitSnapshot(LevelListener listener) {
        if (playerTank != null) {
            listener.entityAdded(playerTank);
        }
        for (int i = 0; i < treeModels.size; i++) {
            listener.entityAdded(treeModels.get(i));
        }
        for (int i = 0; i < enemyTankModels.size; i++) {
            listener.entityAdded(enemyTankModels.get(i));
        }
        for (int i = 0; i < bulletModels.size; i++) {
            listener.entityAdded(bulletModels.get(i));
        }
    }
}
