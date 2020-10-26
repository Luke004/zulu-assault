package game.logic;

import settings.UserSettings;
import game.models.CollisionModel;
import game.graphics.animations.damage.PlasmaDamageAnimation;
import game.graphics.animations.damage.UziDamageAnimation;
import game.graphics.animations.explosion.BigExplosionAnimation;
import game.graphics.animations.explosion.UziHitExplosionAnimation;
import game.graphics.animations.smoke.SmokeAnimation;
import game.models.interaction_circles.HealthCircle;
import game.models.entities.MovableEntity;
import game.models.entities.Entity;
import game.models.entities.aircraft.Aircraft;
import game.models.entities.soldiers.Soldier;
import game.models.weapons.*;
import game.models.weapons.projectiles.AirProjectile;
import game.models.weapons.projectiles.Projectile;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import game.player.Player;

import static game.util.TileMapUtil.doCollateralTileDamage;
import static game.util.TileMapUtil.generateKey;
import static game.levels.AbstractLevel.*;
import static settings.TileMapData.*;

import java.util.*;


public class CollisionHandler {

    private Player player;
    private MovableEntity playerEntity;

    // tile specs TODO: create own tile helper class
    private static final float TILE_HEALTH = 100.f;
    private static final float DESTRUCTIBLE_TILE_NORMAL_ARMOR = 5.f;
    private static final float DESTRUCTIBLE_TILE_LOW_ARMOR = 1.f;

    // animations
    private static SmokeAnimation smokeAnimation;
    private static UziHitExplosionAnimation uziHitExplosionAnimation;
    private static UziDamageAnimation uziDamageAnimation;
    private static BigExplosionAnimation bigExplosionAnimation;
    private static PlasmaDamageAnimation plasmaDamageAnimation;
    private static Random random;
    // sounds
    protected static Sound bullet_hit_sound, explosion_sound, new_item_sound;  // fire sound of the weapon;

    public CollisionHandler() {
        try {
            bullet_hit_sound = new Sound("audio/sounds/bullet_hit.ogg");
            explosion_sound = new Sound("audio/sounds/explosion.ogg");
            new_item_sound = new Sound("audio/sounds/new_item.ogg");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    static {
        smokeAnimation = new SmokeAnimation(5);
        uziHitExplosionAnimation = new UziHitExplosionAnimation(20);
        uziDamageAnimation = new UziDamageAnimation(20);
        bigExplosionAnimation = new BigExplosionAnimation(100);
        plasmaDamageAnimation = new PlasmaDamageAnimation(20);
        random = new Random();
    }

    public void draw() {
        smokeAnimation.draw();
        uziHitExplosionAnimation.draw();
        uziDamageAnimation.draw();
        bigExplosionAnimation.draw();
        plasmaDamageAnimation.draw();
    }

    public void update(int deltaTime) {
        smokeAnimation.update(deltaTime);
        uziHitExplosionAnimation.update(deltaTime);
        uziDamageAnimation.update(deltaTime);
        bigExplosionAnimation.update(deltaTime);
        plasmaDamageAnimation.update(deltaTime);

        // movement collisions for the game.player entity
        handleMovableEntityCollisions(playerEntity);

        // bullet collisions for the game.player entity
        handleBulletCollisions(playerEntity);

        // movement and bullet collisions for all other entities
        handleAllEntityCollisions(playerEntity, all_friendly_entities, deltaTime);


        // ----- individual game.player collisions -----

        // PLAYER COLLIDES WITH HEALTH CIRCLE
        for (HealthCircle health_circle : health_circles) {
            if (playerEntity.getCollisionModel().intersects(health_circle.getCollisionModel())) {
                if (playerEntity.isMaxHealth()) return;
                playerEntity.changeHealth(HealthCircle.HEAL_SPEED);
                break;
            }
        }

        // PLAYER COLLIDES WITH TELEPORT CIRCLE
        boolean noIntersection = true;
        for (int idx = 0; idx < teleport_circles.size(); ++idx) {
            if (playerEntity.getCollisionModel().intersects(teleport_circles.get(idx).getCollisionModel())) {
                // teleport the game.player to any random teleport circle in the list
                int idxToTeleportTo;
                do {
                    // if it's the same circle, get another random idx again
                    idxToTeleportTo = random.nextInt(teleport_circles.size());
                } while (idxToTeleportTo == idx);
                // teleport
                playerEntity.teleport(teleport_circles.get(idxToTeleportTo).getPosition());
                noIntersection = false;
                break;
            }
        }
        if (noIntersection) {
            // allow the game.player to teleport again as soon as he is not in any teleport circle anymore
            playerEntity.allowTeleport();
        }

        // PLAYER COLLIDES WITH ITEMS
        for (int idx = 0; idx < items.size(); ++idx) {
            if (playerEntity.getCollisionModel().intersects(items.get(idx).getCollisionModel())) {
                switch (items.get(idx).getName()) {
                    case "MEGA_PULSE":
                        player.addItem(Player.Item_e.MEGA_PULSE);
                        break;
                    case "INVINCIBILITY":
                        player.addItem(Player.Item_e.INVINCIBILITY);
                        break;
                    case "EMP":
                        player.addItem(Player.Item_e.EMP);
                        break;
                    case "EXPAND":
                        player.addItem(Player.Item_e.EXPAND);
                        break;
                    case "SILVER_WRENCH":
                        if (player.getEntity().isMaxHealth()) return;
                        player.getEntity().changeHealth(10);
                        break;
                    case "GOLDEN_WRENCH":
                        if (player.getEntity().isMaxHealth()) return;
                        player.getEntity().changeHealth(50);
                        break;
                }
                new_item_sound.play(1.f, UserSettings.soundVolume);
                items.remove(idx); // remove the item
                break;
            }
        }
    }

    private void handleMovableEntityCollisions(Entity entity) {
        if (!(entity instanceof MovableEntity)) return;
        MovableEntity movableEntity = (MovableEntity) entity;
        if (!movableEntity.isMoving()) return;
        if (movableEntity instanceof Aircraft) return;

        CollisionModel.Point[] playerCorners = movableEntity.getCollisionModel().getPoints();
        int idx, landscape_layer_tile_ID, x, y;

        for (CollisionModel.Point p : playerCorners) {
            x = (int) p.x / TILE_WIDTH;
            y = (int) p.y / TILE_HEIGHT;

            // return when "out of map"
            if (x < 0 || x >= map.getWidth() || y < 0 || y >= map.getHeight()) return;

            landscape_layer_tile_ID = map.getTileId(x, y, LANDSCAPE_TILES_LAYER_IDX);
            //enemy_layer_tile_ID = map.getTileId(x, y, ENEMY_TILES_LAYER_IDX);

            // COLLISION BETWEEN ENTITY ITSELF AND DESTRUCTIBLE MAP TILES
            for (idx = 0; idx < destructible_tile_indices.length; ++idx) {
                if (landscape_layer_tile_ID == destructible_tile_indices[idx]) {
                    // block movement as long as tile exists and damage the destructible tile
                    movableEntity.blockMovement();

                    // damage ONLY when we are not a solider
                    if (movableEntity instanceof Soldier) return;

                    damageTile(x, y, null, destructible_tile_replace_indices[idx], movableEntity);
                    return;
                }
            }

            /*
            if (!current_movableEntity.isHostile) {
                // COLLISION BETWEEN FRIENDLY ENTITY AND STATIC ENTITIES
                for (idx = 0; idx < static_entity_indices.length; ++idx) {
                    if (enemy_layer_tile_ID == static_entity_indices[idx]) {
                        // block movement as long as tile exists and damage the destructible tile
                        current_movableEntity.blockMovement();

                        // damage ONLY when we are not a solider
                        if (current_movableEntity instanceof Soldier) return;

                        damageStaticEntity(x, y);
                        return;
                    }
                }
            }

             */
        }

        // COLLISION BETWEEN ENTITY ITSELF AND OTHER ENTITIES
        for (Entity otherEntity : all_entities) {
            if (movableEntity.getPosition() == otherEntity.getPosition()) continue;    // its himself
            if (movableEntity.getCollisionModel().intersects(otherEntity.getCollisionModel())) {
                movableEntity.onCollision(otherEntity);
            }
        }

        // COLLISION BETWEEN ENTITY ITSELF AND THE PLAYER
        if (movableEntity.getCollisionModel().intersects(player.getEntity().getCollisionModel())) {
            if (player.getEntity().getPosition() == movableEntity.getPosition()) return;    // its himself
            movableEntity.onCollision(player.getEntity());
        }
    }

    public static boolean intersectsWithTileMap(MovableEntity movableEntity, boolean xPos) {
        CollisionModel.Point[] collisionPoints = movableEntity.getCollisionModel().getPoints();
        // use first two (idx 0 + 1) coll points when is moving forward, else use last two (idx 2 + 3)
        int start_idx = movableEntity.isMovingForward() ? 0 : 2;

        for (int idx = start_idx; idx < collisionPoints.length; ++idx) {
            int x = (int) (collisionPoints[idx].x + (xPos ? movableEntity.dir.x : 0)) / TILE_WIDTH;
            int y = (int) (collisionPoints[idx].y + (xPos ? 0 : movableEntity.dir.y)) / TILE_HEIGHT;

            // return when "out of map"
            if (x < 0 || x >= map.getWidth() || y < 0 || y >= map.getHeight()) return false;

            int landscape_layer_tile_ID = map.getTileId(x, y, LANDSCAPE_TILES_LAYER_IDX);

            // COLLISION BETWEEN ENTITY ITSELF AND INDESTRUCTIBLE TILES
            for (int indestructible_tile_index : indestructible_tile_indices) {
                if (landscape_layer_tile_ID == indestructible_tile_index) {
                    return true;
                }
            }

            if (movableEntity.isMovingForward() && idx >= 1) break;
        }
        return false;
    }

    private void handleBulletCollisions(MovableEntity player_entity) {
        // PLAYER BULLET COLLISIONS
        for (Weapon weapon : player_entity.getWeapons()) {
            Iterator<Projectile> projectile_iterator = weapon.getProjectiles();
            while (projectile_iterator.hasNext()) {
                Projectile projectile = projectile_iterator.next();
                boolean canContinue;

                canContinue = removeProjectileAtMapEdge(projectile, projectile_iterator);

                if (canContinue) continue;

                if (!(projectile instanceof AirProjectile)) {
                    // PLAYER GROUND PROJECTILE COLLISION WITH HOSTILE ENTITIES
                    canContinue = handleGroundProjectileEntityCollision(projectile, weapon, projectile_iterator);

                    if (canContinue) continue;

                    // PLAYER GROUND PROJECTILE COLLISION WITH DESTRUCTIBLE MAP TILE
                    canContinue = handleGroundProjectileTileCollision(projectile, weapon, projectile_iterator);

                    if (canContinue) continue;

                    // PLAYER GROUND PROJECTILE COLLISION WITH STATIC ENTITIES
                    //handleGroundProjectileStaticEntityCollision(projectile, weapon, projectile_iterator);
                } else {    // AIR PROJECTILE
                    if (!((AirProjectile) projectile).hasHitGround()) {
                        continue;    // wait, since the projectile has not hit the ground yet
                    }

                    // PLAYER AIR PROJECTILE COLLISION WITH HOSTILE ENTITIES
                    handleAirProjectileEntityCollision(projectile, weapon);

                    // PLAYER AIR PROJECTILE COLLISION WITH DESTRUCTIBLE MAP TILE
                    handleAirProjectileTileCollision(projectile, weapon);

                    // PLAYER AIR PROJECTILE COLLISION WITH STATIC ENTITIES
                    //handleAirProjectileStaticEntityCollision(projectile, weapon);
                }
            }
        }
    }

    private boolean handleGroundProjectileEntityCollision(Projectile projectile, Weapon weapon, Iterator<Projectile> projectile_iterator) {
        for (Entity hostileEntity : all_hostile_entities) {
            if (projectile.getCollisionModel().intersects(hostileEntity.getCollisionModel())) {
                if (weapon instanceof PiercingWeapon) {
                    if (!((PiercingWeapon) weapon).hasAlreadyHit(hostileEntity)) {
                        //drain health of hit tank:
                        hostileEntity.changeHealth(-weapon.getBulletDamage());
                    }
                    continue;
                } else if (weapon instanceof Uzi) {
                    bullet_hit_sound.play(1.f, UserSettings.soundVolume);
                    if (!(hostileEntity instanceof Soldier)) {
                        uziHitExplosionAnimation.play(projectile.projectile_pos.x, projectile.projectile_pos.y,
                                random.nextInt(360));

                        uziDamageAnimation.play(projectile.projectile_pos.x, projectile.projectile_pos.y,
                                projectile.projectile_image.getRotation() - 90
                                        + random.nextInt(30 + 1 + 30) - 30);
                        // add random extra rotation [-30 , +30]
                    }
                } else if (weapon instanceof Shell || weapon instanceof RocketLauncher) {
                    explosion_sound.play(1.f, UserSettings.soundVolume);
                    bigExplosionAnimation.play(projectile.projectile_pos.x, projectile.projectile_pos.y, 90);
                } else if (weapon instanceof Plasma) {
                    bullet_hit_sound.play(1.f, UserSettings.soundVolume);
                    plasmaDamageAnimation.play(projectile.projectile_pos.x, projectile.projectile_pos.y, 0);
                }
                hostileEntity.changeHealth(-weapon.getBulletDamage());
                projectile_iterator.remove();   // remove bullet
                return true;
            }
        }
        return false;
    }

    private boolean handleGroundProjectileTileCollision(Projectile projectile, Weapon weapon, Iterator<Projectile> bullet_iterator) {
        int x = (int) projectile.projectile_pos.x / TILE_WIDTH;
        int y = (int) projectile.projectile_pos.y / TILE_HEIGHT;
        int tile_ID = map.getTileId(x, y, LANDSCAPE_TILES_LAYER_IDX);


        for (int idx = 0; idx < destructible_tile_indices.length; ++idx) {
            if (tile_ID == destructible_tile_indices[idx]) {
                if (weapon instanceof RocketLauncher || weapon instanceof Shell) {
                    // it's a one shot, destroy tile directly
                    bigExplosionAnimation.playTenTimes(x * TILE_WIDTH + 20, y * TILE_HEIGHT + 20, 0);
                    // destroy the hit tile directly
                    map.setTileId(x, y, LANDSCAPE_TILES_LAYER_IDX, destructible_tile_replace_indices[idx]);

                    // maybe also destroy other tiles around
                    doCollateralTileDamage(x, y);
                } else {
                    if (weapon instanceof PiercingWeapon) {
                        if (weapon instanceof MegaPulse) {
                            // it's a one shot, destroy tile directly
                            map.setTileId(x, y, LANDSCAPE_TILES_LAYER_IDX, destructible_tile_replace_indices[idx]);
                            bigExplosionAnimation.playTenTimes(x * TILE_WIDTH + 20, y * TILE_HEIGHT + 20, 0);
                        } else if (!((PiercingWeapon) weapon).hasAlreadyHit(generateKey(x, y))) {
                            damageTile(x, y, weapon, destructible_tile_replace_indices[idx], null);
                        }
                        continue;
                    } else if (weapon instanceof Uzi) {
                        bullet_hit_sound.play(1.f, UserSettings.soundVolume);
                        uziHitExplosionAnimation.play(projectile.projectile_pos.x, projectile.projectile_pos.y, random.nextInt(360));
                    } else if (weapon instanceof Plasma) {
                        bullet_hit_sound.play(1.f, UserSettings.soundVolume);
                        plasmaDamageAnimation.play(projectile.projectile_pos.x, projectile.projectile_pos.y, 0);
                    }
                    damageTile(x, y, weapon, destructible_tile_replace_indices[idx], null);
                }
                bullet_iterator.remove();
                return true;
            }
        }
        return false;
    }

    /*
    private void handleGroundProjectileStaticEntityCollision(Projectile projectile, Weapon weapon, Iterator<Projectile> bullet_iterator) {
        int x = (int) projectile.projectile_pos.x / TILE_WIDTH;
        int y = (int) projectile.projectile_pos.y / TILE_HEIGHT;
        int tile_ID = map.getTileId(x, y, ENEMY_TILES_LAYER_IDX);

        for (int idx = 0; idx < static_entity_indices.length; ++idx) {
            if (tile_ID == static_entity_indices[idx]) {
                damageStaticEntity(x, y, weapon);
                if (weapon instanceof Uzi || weapon instanceof Plasma)
                    bullet_hit_sound.play(1.f, UserSettings.soundVolume);
                else if (weapon instanceof Shell || weapon instanceof RocketLauncher)
                    explosion_sound.play(1.f, UserSettings.soundVolume);
                if (weapon instanceof PiercingWeapon) continue;
                else showBulletHitAnimation(weapon, projectile);
                bullet_iterator.remove();
            }
        }
    }
     */

    private void handleAirProjectileEntityCollision(Projectile projectile, Weapon weapon) {
        if (((AirProjectile) projectile).hasChecked(AirProjectile.Target.Entities)) return;
        for (int idx = 0; idx < all_hostile_entities.size(); ++idx) {
            if (projectile.getCollisionModel().intersects(all_hostile_entities.get(idx).getCollisionModel())) {
                all_hostile_entities.get(idx).changeHealth(-weapon.getBulletDamage());
            }
        }
        ((AirProjectile) projectile).setChecked(AirProjectile.Target.Entities);
    }

    private void handleAirProjectileTileCollision(Projectile projectile, Weapon weapon) {
        if (((AirProjectile) projectile).hasChecked(AirProjectile.Target.Tiles)) return;
        CollisionModel.Point[] collision_points = projectile.getCollisionModel().collision_points;
        for (int i = 0; i < collision_points.length; ++i) {
            int x = (int) collision_points[i].x / TILE_WIDTH;
            int y = (int) collision_points[i].y / TILE_HEIGHT;
            int tile_ID = map.getTileId(x, y, LANDSCAPE_TILES_LAYER_IDX);

            for (int idx = 0; idx < destructible_tile_indices.length; ++idx) {
                if (tile_ID == destructible_tile_indices[idx]) {
                    if (weapon instanceof AGM || weapon instanceof Goliath) {
                        // it's a one shot, destroy tile directly
                        map.setTileId(x, y, LANDSCAPE_TILES_LAYER_IDX, destructible_tile_replace_indices[idx]);
                        // maybe also destroy other tiles around
                        doCollateralTileDamage(x, y);
                    }
                }
            }
        }
        ((AirProjectile) projectile).setChecked(AirProjectile.Target.Tiles);
    }

    /*
    private void handleAirProjectileStaticEntityCollision(Projectile projectile, Weapon weapon) {
        if (((AirProjectile) projectile).hasChecked(AirProjectile.Target.StaticEntities)) return;
        CollisionModel.Point[] collision_points = projectile.getCollisionModel().collision_points;
        for (int i = 0; i < collision_points.length; ++i) {
            for (int idx = 0; idx < static_entity_indices.length; ++idx) {
                int x = (int) collision_points[i].x / TILE_WIDTH;
                int y = (int) collision_points[i].y / TILE_HEIGHT;
                int tile_ID = map.getTileId(x, y, ENEMY_TILES_LAYER_IDX);
                if (tile_ID == static_entity_indices[idx]) {
                    damageStaticEntity(x, y, weapon);
                    if (weapon instanceof PiercingWeapon) continue;
                    else showBulletHitAnimation(weapon, projectile);
                }
            }
        }
        ((AirProjectile) projectile).setChecked(AirProjectile.Target.StaticEntities);
    }

     */

    /*
    private void replaceStaticEntityTile(int idx) {
        StaticEntity staticEntity = static_enemy_entities.get(idx);
        level_delete_listener.notifyForEntityDestruction(staticEntity);
        explosion_sound.play(1.f, UserSettings.soundVolume);

        Vector2f[] collision_tiles = staticEntity.getCollisionTiles();

        for (Vector2f collision_tile : collision_tiles) {
            // look what tile lies below destroyed windmill (grass, dirt or concrete)
            int tileID = map.getTileId((int) collision_tile.x, (int) collision_tile.y, LANDSCAPE_TILES_LAYER_IDX);
            int replacement_tile_id = TileMapInfo.getReplacementTileID(tileID);
            if (replacement_tile_id != -1)
                map.setTileId((int) collision_tile.x, (int) collision_tile.y, DESTRUCTION_TILES_LAYER_IDX, replacement_tile_id);

            map.setTileId((int) collision_tile.x, (int) collision_tile.y, ENEMY_TILES_LAYER_IDX, 0);
        }

        Vector2f[] replacement_tiles = staticEntity.getReplacementTiles();
        if (replacement_tiles == null) return;

        for (Vector2f replacement_tile : replacement_tiles) {
            // simply remove the replacement tiles
            map.setTileId((int) replacement_tile.x, (int) replacement_tile.y, ENEMY_TILES_LAYER_IDX, 0);
        }
    }
     */

    private static void damageTile(int xPos, int yPos, Weapon weapon, int replaceTileIndex, MovableEntity movableEntity) {
        // if weapon is null, the tile was damaged by contact
        float bullet_damage = weapon == null ? MovableEntity.DAMAGE_TO_DESTRUCTIBLE_TILE : weapon.getBulletDamage();
        // use a map to track current destructible tile health
        int key = generateKey(xPos, yPos);
        if (destructible_tiles_health_info.containsKey(key)) {
            float new_health = destructible_tiles_health_info.get(key) - bullet_damage / DESTRUCTIBLE_TILE_NORMAL_ARMOR;
            if (new_health <= 0) {
                // TILE DESTROYED
                if (weapon == null) {
                    // show smoke animation only when drove over tile, not bullet destruction
                    smokeAnimation.play(movableEntity.getPosition().x, movableEntity.getPosition().y, movableEntity.getRotation());
                } else {
                    // destroyed by bullet, show destruction animation using level listener
                    if (!(weapon instanceof Napalm)) {
                        bigExplosionAnimation.playTenTimes(xPos * TILE_WIDTH + 20,
                                yPos * TILE_HEIGHT + 20, 0);
                    }
                }
                map.setTileId(xPos, yPos, LANDSCAPE_TILES_LAYER_IDX, replaceTileIndex);
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

    private void handleAllEntityCollisions(MovableEntity player_entity, List<Entity> friendly_entities, int deltaTime) {
        for (Entity hostile_entity : all_hostile_entities) {
            hostile_entity.shootAtEnemies(player_entity, friendly_entities, deltaTime);
            handleShotCollisions(hostile_entity, player_entity);
            handleMovableEntityCollisions(hostile_entity);
        }

        /*
        for (StaticEntity enemy_staticEntity : static_enemy_entities) {
            enemy_staticEntity.shootAtEnemies(player_entity, friendly_entities, deltaTime);
            handleShotCollisions(enemy_staticEntity, player_entity);
        }

         */

        for (Entity friendly_entity : friendly_entities) {
            friendly_entity.shootAtEnemies(null, all_hostile_entities, deltaTime);
            handleMovableEntityCollisions(friendly_entity);
            handleShotCollisions(friendly_entity, null);
        }
    }

    private void handleShotCollisions(Entity w, MovableEntity player) {
        for (Weapon weapon : w.getWeapons()) {
            Iterator<Projectile> projectile_iterator = weapon.getProjectiles();

            while (projectile_iterator.hasNext()) {
                Projectile projectile = projectile_iterator.next();
                boolean canContinue;

                canContinue = removeProjectileAtMapEdge(projectile, projectile_iterator);

                if (canContinue) continue;

                if (player != null) {
                    // HOSTILE SHOT COLLISION WITH PLAYER
                    if (projectile.getCollisionModel().intersects(player.getCollisionModel())) {
                        if (weapon instanceof RocketLauncher || weapon instanceof Shell) {
                            explosion_sound.play(1.f, UserSettings.soundVolume);
                        } else if (weapon instanceof Uzi || weapon instanceof Plasma) {
                            bullet_hit_sound.play(1.f, UserSettings.soundVolume);
                        }
                        showBulletHitAnimation(weapon, projectile);
                        projectile_iterator.remove();   // remove bullet
                        if (!player.isInvincible()) {
                            player.changeHealth(-weapon.getBulletDamage());  //drain health of game.player
                        }
                        continue;
                    }
                }

                // BULLET COLLISION WITH DESTRUCTIBLE MAP TILE
                canContinue = handleGroundProjectileTileCollision(projectile, weapon, projectile_iterator);

                if (canContinue) continue;

                if (player == null) {
                    // FRIENDLY SHOT COLLISION WITH HOSTILE ENTITIES
                    for (Entity hostile_entity : all_hostile_entities) {
                        if (projectile.getCollisionModel().intersects(hostile_entity.getCollisionModel())) {
                            showBulletHitAnimation(weapon, projectile);
                            projectile_iterator.remove();   // TODO: FIX CRASH BUG ON REMOVE
                            hostile_entity.changeHealth(-weapon.getBulletDamage());  //drain health of enemy
                            canContinue = true;
                        }
                    }
                    if (canContinue) continue;
                    // FRIENDLY SHOT COLLISION WITH STATIC ENTITIES
                    //handleGroundProjectileStaticEntityCollision(projectile, weapon, projectile_iterator);
                } else {
                    // HOSTILE SHOT COLLISION WITH FRIENDLY ENTITIES
                    for (Entity friendly_entity : all_friendly_entities) {
                        if (projectile.getCollisionModel().intersects(friendly_entity.getCollisionModel())) {
                            showBulletHitAnimation(weapon, projectile);
                            projectile_iterator.remove();
                            friendly_entity.changeHealth(-weapon.getBulletDamage());  //drain health of friend
                        }
                    }
                    // HOSTILE SHOT COLLISION WITH FRIENDLY DRIVABLE ENTITIES
                    for (MovableEntity drivable_entity : drivable_entities) {
                        if (projectile.getCollisionModel().intersects(drivable_entity.getCollisionModel())) {
                            showBulletHitAnimation(weapon, projectile);
                            projectile_iterator.remove();
                            drivable_entity.changeHealth(-weapon.getBulletDamage());
                        }
                    }
                }
            }
        }
    }

    private void showBulletHitAnimation(Weapon weapon, Projectile projectile) {
        if (weapon instanceof Uzi) {
            uziHitExplosionAnimation.play(projectile.projectile_pos.x, projectile.projectile_pos.y, random.nextInt(360));
            uziDamageAnimation.play(projectile.projectile_pos.x, projectile.projectile_pos.y, projectile.projectile_image.getRotation() - 90
                    + random.nextInt(30 + 1 + 30) - 30);  // add random extra rotation [-30 , +30]
        } else if (weapon instanceof Shell || weapon instanceof RocketLauncher) {
            bigExplosionAnimation.play(projectile.projectile_pos.x, projectile.projectile_pos.y, 90);
        } else if (weapon instanceof Plasma) {
            plasmaDamageAnimation.play(projectile.projectile_pos.x, projectile.projectile_pos.y, 0);
        }
    }

    private boolean removeProjectileAtMapEdge(Projectile projectile, Iterator<Projectile> bullet_iterator) {
        // remove bullet if edge of map was reached
        if (projectile.projectile_pos.x < 0) {
            bullet_iterator.remove();
            return true;
        } else if (projectile.projectile_pos.y < 0) {
            bullet_iterator.remove();
            return true;
        } else if (projectile.projectile_pos.x > LEVEL_WIDTH_PIXELS - 1) {
            bullet_iterator.remove();
            return true;
        } else if (projectile.projectile_pos.y > LEVEL_HEIGHT_PIXELS - 1) {
            bullet_iterator.remove();
            return true;
        }
        return false;
    }

    public void setPlayer(Player player) {
        this.player = player;
        this.playerEntity = player.getEntity();
    }
}
