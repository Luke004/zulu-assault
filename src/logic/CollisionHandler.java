package logic;

import models.CollisionModel;
import models.animations.*;
import models.interaction_circles.HealthCircle;
import models.interaction_circles.InteractionCircle;
import models.interaction_circles.TeleportCircle;
import models.war_attenders.MovableWarAttender;
import models.war_attenders.WarAttender;
import models.war_attenders.soldiers.Soldier;
import models.war_attenders.windmills.Windmill;
import models.weapons.*;
import models.weapons.projectiles.Projectile;
import models.weapons.projectiles.iAirProjectile;
import org.lwjgl.Sys;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.tiled.TileSet;
import org.newdawn.slick.tiled.TiledMap;
import player.Player;

import java.util.*;

import static levels.LevelInfo.*;

public class CollisionHandler {
    private Player player;
    private List<MovableWarAttender> friendly_war_attenders, hostile_war_attenders, drivable_war_attenders, all_movable_war_attenders;
    private List<Windmill> enemy_windmills;
    private List<InteractionCircle> interaction_circles;
    private TiledMap level_map;
    private int[] destructible_tile_indices, indestructible_tile_indices, destructible_tile_replace_indices, item_indices,
            windmill_indices, windmill_replace_indices;
    private final float TILE_HEALTH = 100.f;
    private final float DESTRUCTIBLE_TILE_NORMAL_ARMOR = 5.f;
    private final float DESTRUCTIBLE_TILE_LOW_ARMOR = 1.f;
    private final int LANDSCAPE_TILES_LAYER_IDX = 0;
    private final int ITEM_TILES_LAYER_IDX = 2;
    private final int ENEMY_TILES_LAYER_IDX = 3;
    private final int GRASS_IDX, CONCRETE_IDX, DIRT_IDX;
    private Map<Integer, Float> destructible_tiles_health_info;
    protected WarAttenderDeleteListener level_delete_listener;
    private SmokeAnimation smokeAnimation;
    private UziHitExplosionAnimation uziHitExplosionAnimation;
    private UziDamageAnimation uziDamageAnimation;
    private BigExplosionAnimation bigExplosionAnimation;
    private PlasmaHitAnimation plasmaHitAnimation;
    private Random random;


    public CollisionHandler(Player player, TiledMap level_map, List<MovableWarAttender> friendly_war_attenders,
                            List<MovableWarAttender> hostile_war_attenders, List<MovableWarAttender> drivable_war_attenders,
                            List<Windmill> enemy_windmills, List<InteractionCircle> interaction_circles) {
        this.friendly_war_attenders = friendly_war_attenders;
        this.hostile_war_attenders = hostile_war_attenders;
        this.drivable_war_attenders = drivable_war_attenders;
        this.enemy_windmills = enemy_windmills;
        this.interaction_circles = interaction_circles;
        this.player = player;
        this.level_map = level_map;

        // create a global movableWarAttender list for collisions between them
        all_movable_war_attenders = new ArrayList<>(friendly_war_attenders);
        all_movable_war_attenders.addAll(hostile_war_attenders);
        all_movable_war_attenders.add(player.getWarAttender());
        all_movable_war_attenders.addAll(drivable_war_attenders);

        // TileMap related stuff
        item_indices = new int[]{0, 16, 32, 40, 56};
        windmill_indices = new int[]{0, 1, 2};
        windmill_replace_indices = new int[]{96, 97, 98, 99};
        destructible_tile_indices = new int[]{1, 2, 18, 19, 25, 65, 68, 83, 88, 89};
        destructible_tile_replace_indices = new int[]{32, 33, 34, 35, 36, 37, 95, 94, 93, 91};
        indestructible_tile_indices = new int[]{40, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 64, 66, 67, 68,
                72, 73, 74, 75, 76, 77};
        destructible_tiles_health_info = new HashMap<>();

        int idx;
        // create TileInfo for 'landscape_tiles' TileSet
        final int LANDSCAPE_TILES_TILESET_IDX = 1;
        TileSet landscape_tiles = level_map.getTileSet(LANDSCAPE_TILES_TILESET_IDX);
        if (!landscape_tiles.name.equals("landscape_tiles"))
            throw new IllegalAccessError("Wrong tileset index: [" + LANDSCAPE_TILES_TILESET_IDX + "] is not landscape_tiles");
        else {
            for (idx = 0; idx < destructible_tile_indices.length; ++idx) {
                destructible_tile_indices[idx] += landscape_tiles.firstGID;
                destructible_tile_replace_indices[idx] += landscape_tiles.firstGID;
            }
            for (idx = 0; idx < indestructible_tile_indices.length; ++idx) {
                indestructible_tile_indices[idx] += landscape_tiles.firstGID;
            }
            for (idx = 0; idx < windmill_replace_indices.length; ++idx) {
                windmill_replace_indices[idx] += landscape_tiles.firstGID;
            }
        }
        GRASS_IDX = 0 + landscape_tiles.firstGID;
        DIRT_IDX = 16 + landscape_tiles.firstGID;
        CONCRETE_IDX = 80 + landscape_tiles.firstGID;

        // create TileInfo for 'item_tiles' TileSet
        final int ITEM_TILES_TILESET_IDX = 3;
        TileSet item_tiles = level_map.getTileSet(ITEM_TILES_TILESET_IDX);
        if (!item_tiles.name.equals("item_tiles"))
            throw new IllegalAccessError("Wrong tileset index: [" + ITEM_TILES_TILESET_IDX + "] is not item_tiles");
        else {
            for (idx = 0; idx < item_indices.length; ++idx) {
                item_indices[idx] += item_tiles.firstGID;
            }
        }

        // create TileInfo for 'enemy_tiles' TileSet
        final int ENEMY_TILES_TILESET_IDX = 0;
        TileSet enemy_tiles = level_map.getTileSet(ENEMY_TILES_TILESET_IDX);
        if (!enemy_tiles.name.equals("enemy_tiles"))
            throw new IllegalAccessError("Wrong tileset index: [" + ENEMY_TILES_TILESET_IDX + "] is not enemy_tiles");
        else {
            for (idx = 0; idx < windmill_indices.length; ++idx) {
                windmill_indices[idx] += enemy_tiles.firstGID;
            }
        }
        smokeAnimation = new SmokeAnimation(3);
        uziHitExplosionAnimation = new UziHitExplosionAnimation(10);
        uziDamageAnimation = new UziDamageAnimation(10);
        bigExplosionAnimation = new BigExplosionAnimation(10);
        plasmaHitAnimation = new PlasmaHitAnimation(10);
        random = new Random();
    }

    public void addListener(WarAttenderDeleteListener delete_listener) {
        this.level_delete_listener = delete_listener;
    }

    public void draw() {
        smokeAnimation.draw();
        uziHitExplosionAnimation.draw();
        uziDamageAnimation.draw();
        bigExplosionAnimation.draw();
        plasmaHitAnimation.draw();
    }

    public void update(GameContainer gameContainer, int deltaTime) {
        smokeAnimation.update(deltaTime);
        uziHitExplosionAnimation.update(deltaTime);
        uziDamageAnimation.update(deltaTime);
        bigExplosionAnimation.update(deltaTime);
        plasmaHitAnimation.update(deltaTime);

        MovableWarAttender player_warAttender = player.getWarAttender();
        handleMovableWarAttenderCollisions(player_warAttender);
        handleBulletCollisions(player_warAttender);
        handleHostileCollisions(player_warAttender, friendly_war_attenders, deltaTime);

        for (InteractionCircle interaction_circle : interaction_circles) {
            if (player_warAttender.getCollisionModel().intersects(interaction_circle.getCollisionModel())) {
                if (interaction_circle instanceof HealthCircle) {
                    if (player_warAttender.isMaxHealth()) return;
                    player_warAttender.changeHealth(HealthCircle.HEAL_SPEED);
                    return;
                }
                if (interaction_circle instanceof TeleportCircle) {
                    // TODO: teleport player here
                    return;
                }
            }
        }

        /*
        // make enemy soldiers flee from player if he's in a tank or robot
        if(player_warAttender instanceof Tank || player_warAttender instanceof Robot){
            for(MovableWarAttender enemy_soldier : hostile_war_attenders){
                if(enemy_soldier instanceof Soldier){
                    ((Soldier)enemy_soldier).fleeFromPlayer(player_warAttender);
                }
            }
        }

*/

        /*

        switch (player.getWarAttenderType()) {
            case SOLDIER:   // player is a soldier (goes by foot)
                Soldier soldier = (Soldier) player_warAttender;




                break;
            case TANK:      // player is in a tank
                Tank tank = (Tank) player.getWarAttender();


                break;
            case PLANE:     // player is in a plane

                break;

        }
        */
    }

    private void handleMovableWarAttenderCollisions(MovableWarAttender current_warAttender) {
        if (!current_warAttender.isMoving()) return;

        CollisionModel.Point[] playerCorners = current_warAttender.getCollisionModel().getPoints();
        int idx, landscape_layer_tile_ID, item_layer_tile_ID, enemy_layer_tile_ID, x, y;

        for (CollisionModel.Point p : playerCorners) {
            x = (int) p.x / TILE_WIDTH;
            y = (int) p.y / TILE_HEIGHT;

            // return when "out of map"
            if (x < 0 || x >= level_map.getWidth() || y < 0 || y >= level_map.getHeight()) return;

            landscape_layer_tile_ID = level_map.getTileId(x, y, LANDSCAPE_TILES_LAYER_IDX);
            item_layer_tile_ID = level_map.getTileId(x, y, ITEM_TILES_LAYER_IDX);
            enemy_layer_tile_ID = level_map.getTileId(x, y, ENEMY_TILES_LAYER_IDX);

            // COLLISION BETWEEN WAR ATTENDER ITSELF AND DESTRUCTIBLE TILES
            for (idx = 0; idx < destructible_tile_indices.length; ++idx) {
                if (landscape_layer_tile_ID == destructible_tile_indices[idx]) {
                    // block movement as long as tile exists and damage the destructible tile
                    current_warAttender.blockMovement();

                    // damage ONLY when we are not a solider
                    if (current_warAttender instanceof Soldier) return;

                    damageTile(x, y, null, destructible_tile_replace_indices[idx], current_warAttender);
                    return;
                }
            }

            // COLLISION BETWEEN WAR ATTENDER ITSELF AND INDESTRUCTIBLE TILES
            for (idx = 0; idx < indestructible_tile_indices.length; ++idx) {
                if (landscape_layer_tile_ID == indestructible_tile_indices[idx]) {
                    // block movement forever because tile is indestructible
                    current_warAttender.blockMovement();
                    return;
                }
            }

            if (!current_warAttender.isHostile) {
                // COLLISION BETWEEN FRIENDLY WAR ATTENDER AND WINDMILLS
                for (idx = 0; idx < windmill_indices.length; ++idx) {
                    if (enemy_layer_tile_ID == windmill_indices[idx]) {
                        // block movement as long as tile exists and damage the destructible tile
                        current_warAttender.blockMovement();

                        // damage ONLY when we are not a solider
                        if (current_warAttender instanceof Soldier) return;

                        damageWindmill(x, y);
                        return;
                    }
                }
            }

            // COLLISION BETWEEN WAR ATTENDER ITSELF AND ITEMS
            for (idx = 0; idx < item_indices.length; ++idx) {
                if (item_layer_tile_ID == item_indices[idx]) {
                    if (current_warAttender.isHostile) return;   // enemies can't pick ups items
                    switch (idx) {
                        case 0:
                            if (current_warAttender != player.getWarAttender()) break;
                            player.addItem(Player.Item.INVINCIBLE);
                            break;
                        case 1:
                            if (current_warAttender != player.getWarAttender()) break;
                            player.addItem(Player.Item.EMP);
                            break;
                        case 2:
                            if (current_warAttender != player.getWarAttender()) break;
                            player.addItem(Player.Item.MEGA_PULSE);
                            break;
                        case 3:
                            if (current_warAttender != player.getWarAttender()) break;
                            player.addItem(Player.Item.EXPAND);
                            break;
                        case 4: // silver wrench
                            // don't take the wrench if warAttender is at max health
                            if (current_warAttender.isMaxHealth()) return;
                            current_warAttender.changeHealth(10);
                            break;
                        case 5: // golden wrench
                            if (current_warAttender.isMaxHealth()) return;
                            current_warAttender.changeHealth(50);
                            break;
                        default:
                            return;
                    }
                    level_map.setTileId(x, y, ITEM_TILES_LAYER_IDX, 0); // delete the item tile
                }
            }
        }

        // COLLISION BETWEEN WAR ATTENDER ITSELF AND OTHER WAR ATTENDERS
        for (MovableWarAttender movableWarAttender : all_movable_war_attenders) {
            if (movableWarAttender.position == current_warAttender.position) continue;    // its himself
            if (current_warAttender.getCollisionModel().intersects(movableWarAttender.getCollisionModel())) {
                current_warAttender.onCollision(movableWarAttender);
                return;
            }
        }
    }

    private void handleBulletCollisions(MovableWarAttender player_warAttender) {
        // PLAYER BULLET COLLISIONS
        for (Weapon weapon : player_warAttender.getWeapons()) {
            Iterator<Projectile> projectile_iterator = weapon.getProjectiles();
            while (projectile_iterator.hasNext()) {
                Projectile projectile = projectile_iterator.next();
                boolean canContinue;

                canContinue = removeProjectileAtMapEdge(projectile, projectile_iterator);

                if (canContinue) continue;

                if (projectile.isGroundProjectile) {
                    // PLAYER GROUND PROJECTILE COLLISION WITH HOSTILE WAR ATTENDER
                    canContinue = handleGroundProjectileWarAttenderCollision(projectile, weapon, projectile_iterator);

                    if (canContinue) continue;

                    // PLAYER GROUND PROJECTILE COLLISION WITH DESTRUCTIBLE MAP TILE
                    canContinue = handleGroundProjectileTileCollision(projectile, weapon, projectile_iterator);

                    if (canContinue) continue;

                    // PLAYER GROUND PROJECTILE COLLISION WITH WINDMILL
                    handleGroundProjectileWindmillCollision(projectile, weapon, projectile_iterator);
                } else {
                    if (!((iAirProjectile) projectile).hasHitGround()) {
                        continue;    // wait, since the projectile has not hit the ground yet
                    }

                    // PLAYER AIR PROJECTILE COLLISION WITH HOSTILE WAR ATTENDER
                    handleAirProjectileWarAttenderCollision(projectile, weapon);

                    // PLAYER AIR PROJECTILE COLLISION WITH DESTRUCTIBLE MAP TILE
                    handleAirProjectileTileCollision(projectile, weapon);

                    // PLAYER AIR PROJECTILE COLLISION WITH WINDMILL
                    handleAirProjectileWindmillCollision(projectile, weapon);
                }
            }
        }
    }

    private boolean handleGroundProjectileWarAttenderCollision(Projectile projectile, Weapon weapon, Iterator<Projectile> projectile_iterator) {
        for (int idx = 0; idx < hostile_war_attenders.size(); ++idx) {
            if (projectile.getCollisionModel().intersects(hostile_war_attenders.get(idx).getCollisionModel())) {
                if (weapon instanceof PiercingWeapon) {
                    if (!((PiercingWeapon) weapon).hasAlreadyHit(idx)) {
                        //drain health of hit tank:
                        hostile_war_attenders.get(idx).changeHealth(-weapon.getBulletDamage());
                    }
                    continue;
                } else if (weapon instanceof Uzi) {
                    if (!(hostile_war_attenders.get(idx) instanceof Soldier)) {
                        uziHitExplosionAnimation.play(projectile.pos.x, projectile.pos.y,
                                random.nextInt(360));

                        uziDamageAnimation.play(projectile.pos.x, projectile.pos.y,
                                projectile.image.getRotation() - 90
                                        + random.nextInt(30 + 1 + 30) - 30);
                        // add random extra rotation [-30 , +30]
                    }
                } else if (weapon instanceof Shell || weapon instanceof RocketLauncher) {
                    bigExplosionAnimation.play(projectile.pos.x, projectile.pos.y, 90);
                } else if (weapon instanceof Plasma) {
                    plasmaHitAnimation.play(projectile.pos.x, projectile.pos.y, 0);
                }
                hostile_war_attenders.get(idx).changeHealth(-weapon.getBulletDamage());
                projectile_iterator.remove();   // remove bullet
                return true;
            }
        }
        return false;
    }

    private boolean handleGroundProjectileTileCollision(Projectile projectile, Weapon weapon, Iterator<Projectile> bullet_iterator) {
        int x = (int) projectile.pos.x / TILE_WIDTH;
        int y = (int) projectile.pos.y / TILE_HEIGHT;
        int tile_ID = level_map.getTileId(x, y, LANDSCAPE_TILES_LAYER_IDX);


        for (int idx = 0; idx < destructible_tile_indices.length; ++idx) {
            if (tile_ID == destructible_tile_indices[idx]) {
                if (weapon instanceof RocketLauncher || weapon instanceof Shell) {
                    // it's a one shot, destroy tile directly
                    bigExplosionAnimation.play(projectile.pos.x, projectile.pos.y, 90);

                    // destroyed by bullet, show destruction animation using level listener
                    level_delete_listener.notifyForDeletion(x * TILE_WIDTH + 20, y * TILE_HEIGHT + 20);

                    // destroy the hit tile directly
                    level_map.setTileId(x, y, LANDSCAPE_TILES_LAYER_IDX, destructible_tile_replace_indices[idx]);

                    // maybe also destroy other tiles around
                    doCollateralTileDamage(x, y, idx);
                } else {
                    if (weapon instanceof PiercingWeapon) {
                        if (weapon instanceof MegaPulse) {
                            // it's a one shot, destroy tile directly
                            level_map.setTileId(x, y, LANDSCAPE_TILES_LAYER_IDX, destructible_tile_replace_indices[idx]);
                            level_delete_listener.notifyForDeletion(x * TILE_WIDTH + 20, y * TILE_HEIGHT + 20);
                        } else if (!((PiercingWeapon) weapon).hasAlreadyHit(generateKey(x, y))) {
                            damageTile(x, y, weapon, destructible_tile_replace_indices[idx], null);
                        }
                        continue;
                    } else if (weapon instanceof Uzi) {
                        uziHitExplosionAnimation.play(projectile.pos.x, projectile.pos.y, random.nextInt(360));
                    } else if (weapon instanceof Plasma) {
                        plasmaHitAnimation.play(projectile.pos.x, projectile.pos.y, 0);
                    }
                    damageTile(x, y, weapon, destructible_tile_replace_indices[idx], null);
                }
                bullet_iterator.remove();
                return true;
            }
        }
        return false;
    }

    private void handleGroundProjectileWindmillCollision(Projectile projectile, Weapon weapon, Iterator<Projectile> bullet_iterator) {
        for (int idx = 0; idx < windmill_indices.length; ++idx) {
            int x = (int) projectile.pos.x / TILE_WIDTH;
            int y = (int) projectile.pos.y / TILE_HEIGHT;
            int tile_ID = level_map.getTileId(x, y, ENEMY_TILES_LAYER_IDX);
            if (tile_ID == windmill_indices[idx]) {
                damageWindmill(x, y, weapon);
                if (weapon instanceof PiercingWeapon) continue;
                else showBulletHitAnimation(weapon, projectile);
                bullet_iterator.remove();
            }
        }
    }

    private void handleAirProjectileWarAttenderCollision(Projectile projectile, Weapon weapon) {
        if (((iAirProjectile) projectile).hasChecked(iAirProjectile.Target.WarAttenders)) return;
        for (int idx = 0; idx < hostile_war_attenders.size(); ++idx) {
            if (projectile.getCollisionModel().intersects(hostile_war_attenders.get(idx).getCollisionModel())) {
                if (weapon instanceof DoubleRocketLauncher) {
                    //bigExplosionAnimation.play(projectile.pos.x, projectile.pos.y, 90);
                }
                hostile_war_attenders.get(idx).changeHealth(-weapon.getBulletDamage());
            }
        }
        ((iAirProjectile) projectile).setChecked(iAirProjectile.Target.WarAttenders);
    }

    private void handleAirProjectileTileCollision(Projectile projectile, Weapon weapon) {
        if (((iAirProjectile) projectile).hasChecked(iAirProjectile.Target.Tiles)) return;
        CollisionModel.Point[] collision_points = projectile.getCollisionModel().collision_points;
        for (int i = 0; i < collision_points.length; ++i) {
            int x = (int) collision_points[i].x / TILE_WIDTH;
            int y = (int) collision_points[i].y / TILE_HEIGHT;
            int tile_ID = level_map.getTileId(x, y, LANDSCAPE_TILES_LAYER_IDX);

            for (int idx = 0; idx < destructible_tile_indices.length; ++idx) {
                if (tile_ID == destructible_tile_indices[idx]) {
                    if (weapon instanceof DoubleRocketLauncher) {
                        // it's a one shot, destroy tile directly
                        // destroyed by bullet, show destruction animation using level listener
                        level_delete_listener.notifyForDeletion(x * TILE_WIDTH + 20, y * TILE_HEIGHT + 20);
                        // destroy the hit tile directly
                        level_map.setTileId(x, y, LANDSCAPE_TILES_LAYER_IDX, destructible_tile_replace_indices[idx]);
                        // maybe also destroy other tiles around
                        doCollateralTileDamage(x, y, idx);
                    }
                }
            }
        }
        ((iAirProjectile) projectile).setChecked(iAirProjectile.Target.Tiles);
    }

    private void handleAirProjectileWindmillCollision(Projectile projectile, Weapon weapon) {
        if (((iAirProjectile) projectile).hasChecked(iAirProjectile.Target.Windmills)) return;
        CollisionModel.Point[] collision_points = projectile.getCollisionModel().collision_points;
        for (int i = 0; i < collision_points.length; ++i) {
            for (int idx = 0; idx < windmill_indices.length; ++idx) {
                int x = (int) collision_points[i].x / TILE_WIDTH;
                int y = (int) collision_points[i].y / TILE_HEIGHT;
                int tile_ID = level_map.getTileId(x, y, ENEMY_TILES_LAYER_IDX);
                if (tile_ID == windmill_indices[idx]) {
                    damageWindmill(x, y, weapon);
                    if (weapon instanceof PiercingWeapon) continue;
                    else showBulletHitAnimation(weapon, projectile);
                }
            }
        }
        ((iAirProjectile) projectile).setChecked(iAirProjectile.Target.Windmills);
    }

    private void damageWindmill(int xPos, int yPos, Weapon weapon) {
        // use a map to track current destructible tile health
        int key = generateKey(xPos, yPos);
        int idx;

        for (idx = 0; idx < enemy_windmills.size(); ++idx) {
            if (enemy_windmills.get(idx).getKey() != key) continue;
            if (weapon instanceof PiercingWeapon) {
                if (!((PiercingWeapon) weapon).hasAlreadyHit(key)) {
                    enemy_windmills.get(idx).changeHealth(-weapon.getBulletDamage()); //drain health of hit tank
                    break;
                }
            } else {
                enemy_windmills.get(idx).changeHealth(-weapon.getBulletDamage());
            }
            if (enemy_windmills.get(idx).isDestroyed) {
                replaceWindmillTile(idx, xPos, yPos);
            }
        }
    }

    private void damageWindmill(int xPos, int yPos) {
        // find the windmill by its key
        int key = xPos > yPos ? -xPos * yPos : xPos * yPos;
        int idx;

        for (idx = 0; idx < enemy_windmills.size(); ++idx) {
            if (enemy_windmills.get(idx).getKey() == key) {
                enemy_windmills.get(idx).changeHealth(-MovableWarAttender.DAMAGE_TO_DESTRUCTIBLE_TILE);
                if (enemy_windmills.get(idx).isDestroyed) {
                    replaceWindmillTile(idx, xPos, yPos);
                }
                break;
            }
        }
    }

    private void replaceWindmillTile(int idx, int xPos, int yPos) {
        level_delete_listener.notifyForDeletion(enemy_windmills.get(idx));
        enemy_windmills.remove(idx);

        // look what tile lies below destroyed windmill (grass, dirt or concrete)
        int tileID = level_map.getTileId(xPos, yPos, LANDSCAPE_TILES_LAYER_IDX);
        int replacement_idx = windmill_replace_indices[3];  // standard damaged tile with transparent background

        if (tileID == CONCRETE_IDX) {
            replacement_idx = windmill_replace_indices[1];
        } else if (tileID == DIRT_IDX) {
            replacement_idx = windmill_replace_indices[0];
        } else if (tileID == GRASS_IDX) {
            replacement_idx = windmill_replace_indices[2];
        }
        level_map.setTileId(xPos, yPos, ENEMY_TILES_LAYER_IDX, 0);
        level_map.setTileId(xPos, yPos, LANDSCAPE_TILES_LAYER_IDX, replacement_idx);
    }

    private void damageTile(int xPos, int yPos, Weapon weapon, int replaceTileIndex, MovableWarAttender warAttender) {
        // if weapon is null, the tile was damaged by contact
        float bullet_damage = weapon == null ? MovableWarAttender.DAMAGE_TO_DESTRUCTIBLE_TILE : weapon.getBulletDamage();
        // use a map to track current destructible tile health
        int key = generateKey(xPos, yPos);
        if (destructible_tiles_health_info.containsKey(key)) {
            float new_health = destructible_tiles_health_info.get(key) - bullet_damage / DESTRUCTIBLE_TILE_NORMAL_ARMOR;
            if (new_health <= 0) {
                // TILE DESTROYED
                if (weapon == null) {
                    // show smoke animation only when drove over tile, not bullet destruction
                    smokeAnimation.play(warAttender.position.x, warAttender.position.y, warAttender.getRotation());
                } else {
                    // destroyed by bullet, show destruction animation using level listener
                    if (weapon instanceof Plasma)
                        bigExplosionAnimation.play(xPos * TILE_WIDTH + 20, yPos * TILE_HEIGHT + 20, 0);
                    else if (!(weapon instanceof Napalm))
                        level_delete_listener.notifyForDeletion(xPos * TILE_WIDTH + 20, yPos * TILE_HEIGHT + 20);
                }
                level_map.setTileId(xPos, yPos, LANDSCAPE_TILES_LAYER_IDX, replaceTileIndex);
                destructible_tiles_health_info.remove(key);
            } else {
                destructible_tiles_health_info.put(key, new_health);
            }
        } else {
            if (replaceTileIndex == 44) {  // this ONE low health tile cactus thing LOL
                destructible_tiles_health_info.put(key, TILE_HEALTH - bullet_damage / DESTRUCTIBLE_TILE_LOW_ARMOR);
                return;
            }
            destructible_tiles_health_info.put(key, TILE_HEALTH - bullet_damage / DESTRUCTIBLE_TILE_NORMAL_ARMOR);
        }
    }

    private void handleHostileCollisions(MovableWarAttender player_warAttender, List<MovableWarAttender> friendly_war_attenders, int deltaTime) {
        for (MovableWarAttender hostile_warAttender : hostile_war_attenders) {
            hostile_warAttender.shootAtEnemies(player_warAttender, friendly_war_attenders, deltaTime);
            hostileShotCollision(hostile_warAttender, player_warAttender);
            handleMovableWarAttenderCollisions(hostile_warAttender);
        }

        for (WarAttender enemy_windmill : enemy_windmills) {
            enemy_windmill.shootAtEnemies(player_warAttender, friendly_war_attenders, deltaTime);
            hostileShotCollision(enemy_windmill, player_warAttender);
        }

        for (MovableWarAttender friendly_war_attender : friendly_war_attenders) {
            friendly_war_attender.shootAtEnemies(null, hostile_war_attenders, deltaTime);
            friendly_war_attender.shootAtEnemies(null, enemy_windmills, deltaTime);
            handleMovableWarAttenderCollisions(friendly_war_attender);
            hostileShotCollision(friendly_war_attender, null);
        }
    }

    private void hostileShotCollision(WarAttender w, MovableWarAttender player) {
        for (Weapon weapon : w.getWeapons()) {
            Iterator<Projectile> bullet_iterator = weapon.getProjectiles();
            while (bullet_iterator.hasNext()) {
                Projectile b = bullet_iterator.next();
                boolean canContinue;

                canContinue = removeProjectileAtMapEdge(b, bullet_iterator);

                if (canContinue) continue;

                if (player != null) {
                    // HOSTILE SHOT COLLISION WITH PLAYER
                    if (b.getCollisionModel().intersects(player.getCollisionModel())) {
                        showBulletHitAnimation(weapon, b);
                        bullet_iterator.remove();   // remove bullet
                        if (!player.isInvincible()) {
                            player.changeHealth(-weapon.getBulletDamage());  //drain health of player
                        }
                        continue;
                    }
                }

                // HOSTILE BULLET COLLISION WITH DESTRUCTIBLE MAP TILE
                canContinue = handleGroundProjectileTileCollision(b, weapon, bullet_iterator);

                if (canContinue) continue;

                if (player == null) {
                    // FRIENDLY SHOT COLLISION WITH HOSTILE WAR ATTENDERS
                    for (MovableWarAttender hostile_warAttender : hostile_war_attenders) {
                        if (b.getCollisionModel().intersects(hostile_warAttender.getCollisionModel())) {
                            showBulletHitAnimation(weapon, b);
                            bullet_iterator.remove();
                            hostile_warAttender.changeHealth(-weapon.getBulletDamage());  //drain health of enemy
                        }
                    }
                    // FRIENDLY SHOT COLLISION WITH WINDMILLS
                    handleGroundProjectileWindmillCollision(b, weapon, bullet_iterator);
                } else {
                    // HOSTILE SHOT COLLISION WITH FRIENDLY WAR ATTENDERS
                    for (MovableWarAttender friendly_warAttender : friendly_war_attenders) {
                        if (b.getCollisionModel().intersects(friendly_warAttender.getCollisionModel())) {
                            showBulletHitAnimation(weapon, b);
                            bullet_iterator.remove();
                            friendly_warAttender.changeHealth(-weapon.getBulletDamage());  //drain health of friend
                        }
                    }
                }
            }
        }
    }

    private void showBulletHitAnimation(Weapon weapon, Projectile projectile) {
        if (weapon instanceof Uzi) {
            uziHitExplosionAnimation.play(projectile.pos.x, projectile.pos.y, random.nextInt(360));
            uziDamageAnimation.play(projectile.pos.x, projectile.pos.y, projectile.image.getRotation() - 90
                    + random.nextInt(30 + 1 + 30) - 30);  // add random extra rotation [-30 , +30]
        } else if (weapon instanceof Shell || weapon instanceof RocketLauncher) {
            bigExplosionAnimation.play(projectile.pos.x, projectile.pos.y, 90);
        } else if (weapon instanceof Plasma) {
            plasmaHitAnimation.play(projectile.pos.x, projectile.pos.y, 0);
        }
    }


    private void doCollateralTileDamage(int x, int y, int idx) {
        // maybe destroy nearby tiles
        List<Tile> tiles = new ArrayList<>();
        if (y > 0) {
            // top tile
            tiles.add(new Tile(x, y - 1, level_map.getTileId(x, y - 1, LANDSCAPE_TILES_LAYER_IDX)));
            if (x > 0) {
                // top left tile
                tiles.add(new Tile(x - 1, y - 1, level_map.getTileId(x - 1, y - 1, LANDSCAPE_TILES_LAYER_IDX)));
            }
            if (x < level_map.getWidth() - 1) {
                // top right tile
                tiles.add(new Tile(x + 1, y - 1, level_map.getTileId(x + 1, y - 1, LANDSCAPE_TILES_LAYER_IDX)));
            }
        }

        if (y < level_map.getHeight() - 1) {
            // bottom tile
            tiles.add(new Tile(x, y + 1, level_map.getTileId(x, y + 1, LANDSCAPE_TILES_LAYER_IDX)));
            if (x > 0) {
                // bottom left tile
                tiles.add(new Tile(x - 1, y + 1, level_map.getTileId(x - 1, y + 1, LANDSCAPE_TILES_LAYER_IDX)));
            }
            if (x < level_map.getWidth() - 1) {
                // bottom right tile
                tiles.add(new Tile(x + 1, y + 1, level_map.getTileId(x + 1, y + 1, LANDSCAPE_TILES_LAYER_IDX)));
            }
        }

        if (x > 0) {
            // left tile
            tiles.add(new Tile(x - 1, y, level_map.getTileId(x - 1, y, LANDSCAPE_TILES_LAYER_IDX)));
        }

        if (x < level_map.getWidth() - 1) {
            // right tile
            tiles.add(new Tile(x + 1, y, level_map.getTileId(x + 1, y, LANDSCAPE_TILES_LAYER_IDX)));
        }

        for (Tile tile : tiles) {
            for (int idx2 = 0; idx2 < destructible_tile_indices.length; ++idx2) {
                if (tile.tileID == destructible_tile_indices[idx2]) {
                    double d = Math.random();
                    if (d < 0.3) {
                        // 30% chance of tile getting destroyed
                        level_map.setTileId(tile.xVal, tile.yVal, LANDSCAPE_TILES_LAYER_IDX, destructible_tile_replace_indices[idx2]);
                        if (destructible_tiles_health_info.containsKey(tile.key)) {
                            destructible_tiles_health_info.remove(tile.key);
                        }
                    }
                }
            }
        }
    }

    private boolean removeProjectileAtMapEdge(Projectile projectile, Iterator<Projectile> bullet_iterator) {
        // remove bullet if edge of map was reached
        if (projectile.pos.x < 0) {
            bullet_iterator.remove();
            return true;
        } else if (projectile.pos.y < 0) {
            bullet_iterator.remove();
            return true;
        } else if (projectile.pos.x > LEVEL_WIDTH_PIXELS - 1) {
            bullet_iterator.remove();
            return true;
        } else if (projectile.pos.y > LEVEL_HEIGHT_PIXELS - 1) {
            bullet_iterator.remove();
            return true;
        }
        return false;
    }

    private int generateKey(int xPos, int yPos) {
        return xPos > yPos ? -xPos * yPos : xPos * yPos;
    }

    private class Tile {
        int tileID, xVal, yVal, key;

        Tile(int xVal, int yVal, int tileID) {
            this.tileID = tileID;
            this.xVal = xVal;
            this.yVal = yVal;
            this.key = generateKey(xVal, yVal);
        }
    }
}
