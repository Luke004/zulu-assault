package logic;

import logic.level_listeners.WarAttenderDeleteListener;
import settings.UserSettings;
import models.CollisionModel;
import models.StaticWarAttender;
import graphics.animations.damage.PlasmaDamageAnimation;
import graphics.animations.damage.UziDamageAnimation;
import graphics.animations.explosion.BigExplosionAnimation;
import graphics.animations.explosion.UziHitExplosionAnimation;
import graphics.animations.smoke.SmokeAnimation;
import models.interaction_circles.HealthCircle;
import models.war_attenders.MovableWarAttender;
import models.war_attenders.WarAttender;
import models.war_attenders.aircraft.friendly.Plane;
import models.war_attenders.soldiers.Soldier;
import models.weapons.*;
import models.weapons.projectiles.AirProjectile;
import models.weapons.projectiles.Projectile;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Vector2f;
import player.Player;

import static levels.AbstractLevel.*;
import static logic.TileMapInfo.*;

import java.util.*;


public class CollisionHandler {
    private Player player;

    // tile specs TODO: create own tile helper class
    private static final float TILE_HEALTH = 100.f;
    private static final float DESTRUCTIBLE_TILE_NORMAL_ARMOR = 5.f;
    private static final float DESTRUCTIBLE_TILE_LOW_ARMOR = 1.f;

    // animations
    private static WarAttenderDeleteListener level_delete_listener;
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

    public void addListener(WarAttenderDeleteListener delete_listener) {
        level_delete_listener = delete_listener;
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

        MovableWarAttender player_warAttender = player.getWarAttender();
        handleMovableWarAttenderCollisions(player_warAttender);
        handleBulletCollisions(player_warAttender);
        handleHostileCollisions(player_warAttender, friendly_movable_war_attenders, deltaTime);

        // PLAYER COLLIDES WITH HEALTH CIRCLE
        for (HealthCircle health_circle : health_circles) {
            if (player_warAttender.getCollisionModel().intersects(health_circle.getCollisionModel())) {
                if (player_warAttender.isMaxHealth()) return;
                player_warAttender.changeHealth(HealthCircle.HEAL_SPEED);
                break;
            }
        }

        // PLAYER COLLIDES WITH TELEPORT CIRCLE
        boolean noIntersection = true;
        for (int idx = 0; idx < teleport_circles.size(); ++idx) {
            if (player_warAttender.getCollisionModel().intersects(teleport_circles.get(idx).getCollisionModel())) {
                // teleport the player
                if ((idx & 1) == 0) {
                    // even idx -> teleport the player to the next teleport circle in the list
                    player_warAttender.teleport(teleport_circles.get(idx + 1).getPosition());
                } else {
                    // odd idx -> teleport the player to the previous teleport circle in the list
                    player_warAttender.teleport(teleport_circles.get(idx - 1).getPosition());
                }
                noIntersection = false;
                break;
            }
        }
        if (noIntersection) {
            // allow the player to teleport again as soon as he is not in any teleport circle anymore
            player_warAttender.allowTeleport();
        }

        // PLAYER COLLIDES WITH ITEMS
        for (int idx = 0; idx < items.size(); ++idx) {
            if (player_warAttender.getCollisionModel().intersects(items.get(idx).getCollisionModel())) {
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
                        if (player.getWarAttender().isMaxHealth()) return;
                        player.getWarAttender().changeHealth(10);
                        break;
                    case "GOLDEN_WRENCH":
                        if (player.getWarAttender().isMaxHealth()) return;
                        player.getWarAttender().changeHealth(50);
                        break;
                }
                new_item_sound.play(1.f, UserSettings.soundVolume);
                items.remove(idx); // remove the item
                break;
            }
        }
    }

    private void handleMovableWarAttenderCollisions(MovableWarAttender current_warAttender) {
        if (!current_warAttender.isMoving()) return;
        if (current_warAttender instanceof Plane) return;

        CollisionModel.Point[] playerCorners = current_warAttender.getCollisionModel().getPoints();
        int idx, landscape_layer_tile_ID, enemy_layer_tile_ID, x, y;

        for (CollisionModel.Point p : playerCorners) {
            x = (int) p.x / TILE_WIDTH;
            y = (int) p.y / TILE_HEIGHT;

            // return when "out of map"
            if (x < 0 || x >= map.getWidth() || y < 0 || y >= map.getHeight()) return;

            landscape_layer_tile_ID = map.getTileId(x, y, LANDSCAPE_TILES_LAYER_IDX);
            enemy_layer_tile_ID = map.getTileId(x, y, ENEMY_TILES_LAYER_IDX);

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

            if (!current_warAttender.isHostile) {
                // COLLISION BETWEEN FRIENDLY WAR ATTENDER AND STATIC WAR ATTENDERS
                for (idx = 0; idx < staticWarAttender_indices.length; ++idx) {
                    if (enemy_layer_tile_ID == staticWarAttender_indices[idx]) {
                        // block movement as long as tile exists and damage the destructible tile
                        current_warAttender.blockMovement();

                        // damage ONLY when we are not a solider
                        if (current_warAttender instanceof Soldier) return;

                        damageStaticWarAttender(x, y);
                        return;
                    }
                }
            }
        }

        // COLLISION BETWEEN WAR ATTENDER ITSELF AND OTHER WAR ATTENDERS
        for (MovableWarAttender movableWarAttender : all_movable_war_attenders) {
            if (movableWarAttender.getPosition() == current_warAttender.getPosition()) continue;    // its himself
            if (current_warAttender.getCollisionModel().intersects(movableWarAttender.getCollisionModel())) {
                current_warAttender.onCollision(movableWarAttender);
            }
        }

        // COLLISION BETWEEN WAR ATTENDER ITSELF AND THE PLAYER
        if (current_warAttender.getCollisionModel().intersects(player.getWarAttender().getCollisionModel())) {
            if (player.getWarAttender().getPosition() == current_warAttender.getPosition()) return;    // its himself
            current_warAttender.onCollision(player.getWarAttender());
        }
    }

    public static boolean intersectsWithTileMap(MovableWarAttender movableWarAttender, boolean xPos) {
        CollisionModel.Point[] collisionPoints = movableWarAttender.getCollisionModel().getPoints();
        // use first two (idx 0 + 1) coll points when is moving forward, else use last two (idx 2 + 3)
        int start_idx = movableWarAttender.isMovingForward() ? 0 : 2;

        for (int idx = start_idx; idx < collisionPoints.length; ++idx) {
            int x = (int) (collisionPoints[idx].x + (xPos ? movableWarAttender.dir.x : 0)) / TILE_WIDTH;
            int y = (int) (collisionPoints[idx].y + (xPos ? 0 : movableWarAttender.dir.y)) / TILE_HEIGHT;

            // return when "out of map"
            if (x < 0 || x >= map.getWidth() || y < 0 || y >= map.getHeight()) return false;

            int landscape_layer_tile_ID = map.getTileId(x, y, LANDSCAPE_TILES_LAYER_IDX);

            // COLLISION BETWEEN WAR ATTENDER ITSELF AND INDESTRUCTIBLE TILES
            for (int indestructible_tile_index : indestructible_tile_indices) {
                if (landscape_layer_tile_ID == indestructible_tile_index) {
                    return true;
                }
            }

            if (movableWarAttender.isMovingForward() && idx >= 1) break;
        }
        return false;
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

                if (!(projectile instanceof AirProjectile)) {
                    // PLAYER GROUND PROJECTILE COLLISION WITH HOSTILE WAR ATTENDER
                    canContinue = handleGroundProjectileWarAttenderCollision(projectile, weapon, projectile_iterator);

                    if (canContinue) continue;

                    // PLAYER GROUND PROJECTILE COLLISION WITH DESTRUCTIBLE MAP TILE
                    canContinue = handleGroundProjectileTileCollision(projectile, weapon, projectile_iterator);

                    if (canContinue) continue;

                    // PLAYER GROUND PROJECTILE COLLISION WITH STATIC WAR ATTENDERS
                    handleGroundProjectileStaticWarAttenderCollision(projectile, weapon, projectile_iterator);
                } else {    // AIR PROJECTILE
                    if (!((AirProjectile) projectile).hasHitGround()) {
                        continue;    // wait, since the projectile has not hit the ground yet
                    }

                    // PLAYER AIR PROJECTILE COLLISION WITH HOSTILE WAR ATTENDER
                    handleAirProjectileWarAttenderCollision(projectile, weapon);

                    // PLAYER AIR PROJECTILE COLLISION WITH DESTRUCTIBLE MAP TILE
                    handleAirProjectileTileCollision(projectile, weapon);

                    // PLAYER AIR PROJECTILE COLLISION WITH STATIC WAR ATTENDERS
                    handleAirProjectileStaticWarAttenderCollision(projectile, weapon);
                }
            }
        }
    }

    private boolean handleGroundProjectileWarAttenderCollision(Projectile projectile, Weapon weapon, Iterator<Projectile> projectile_iterator) {
        for (MovableWarAttender hostileWarAttender : hostile_movable_war_attenders) {
            if (projectile.getCollisionModel().intersects(hostileWarAttender.getCollisionModel())) {
                if (weapon instanceof PiercingWeapon) {
                    if (!((PiercingWeapon) weapon).hasAlreadyHit(hostileWarAttender)) {
                        //drain health of hit tank:
                        hostileWarAttender.changeHealth(-weapon.getBulletDamage());
                    }
                    continue;
                } else if (weapon instanceof Uzi) {
                    bullet_hit_sound.play(1.f, UserSettings.soundVolume);
                    if (!(hostileWarAttender instanceof Soldier)) {
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
                hostileWarAttender.changeHealth(-weapon.getBulletDamage());
                projectile_iterator.remove();   // remove bullet
                return true;
            }
        }
        return false;

        /*
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
                    plasmaDamageAnimation.play(projectile.pos.x, projectile.pos.y, 0);
                }
                hostile_war_attenders.get(idx).changeHealth(-weapon.getBulletDamage());
                projectile_iterator.remove();   // remove bullet
                return true;
            }
        }
        return false;
        */
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

    private void handleGroundProjectileStaticWarAttenderCollision(Projectile projectile, Weapon weapon, Iterator<Projectile> bullet_iterator) {
        int x = (int) projectile.projectile_pos.x / TILE_WIDTH;
        int y = (int) projectile.projectile_pos.y / TILE_HEIGHT;
        int tile_ID = map.getTileId(x, y, ENEMY_TILES_LAYER_IDX);

        for (int idx = 0; idx < staticWarAttender_indices.length; ++idx) {
            if (tile_ID == staticWarAttender_indices[idx]) {
                damageStaticWarAttender(x, y, weapon);
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

    private void handleAirProjectileWarAttenderCollision(Projectile projectile, Weapon weapon) {
        if (((AirProjectile) projectile).hasChecked(AirProjectile.Target.WarAttenders)) return;
        for (int idx = 0; idx < hostile_movable_war_attenders.size(); ++idx) {
            if (projectile.getCollisionModel().intersects(hostile_movable_war_attenders.get(idx).getCollisionModel())) {
                hostile_movable_war_attenders.get(idx).changeHealth(-weapon.getBulletDamage());
            }
        }
        ((AirProjectile) projectile).setChecked(AirProjectile.Target.WarAttenders);
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

    private void handleAirProjectileStaticWarAttenderCollision(Projectile projectile, Weapon weapon) {
        if (((AirProjectile) projectile).hasChecked(AirProjectile.Target.StaticWarAttender)) return;
        CollisionModel.Point[] collision_points = projectile.getCollisionModel().collision_points;
        for (int i = 0; i < collision_points.length; ++i) {
            for (int idx = 0; idx < staticWarAttender_indices.length; ++idx) {
                int x = (int) collision_points[i].x / TILE_WIDTH;
                int y = (int) collision_points[i].y / TILE_HEIGHT;
                int tile_ID = map.getTileId(x, y, ENEMY_TILES_LAYER_IDX);
                if (tile_ID == staticWarAttender_indices[idx]) {
                    damageStaticWarAttender(x, y, weapon);
                    if (weapon instanceof PiercingWeapon) continue;
                    else showBulletHitAnimation(weapon, projectile);
                }
            }
        }
        ((AirProjectile) projectile).setChecked(AirProjectile.Target.StaticWarAttender);
    }

    private void damageStaticWarAttender(int xPos, int yPos, Weapon weapon) {
        for (int idx = 0; idx < static_enemies.size(); ++idx) {
            StaticWarAttender staticWarAttender = static_enemies.get(idx);
            if (!staticWarAttender.containsTilePosition(xPos, yPos)) continue;
            if (weapon instanceof PiercingWeapon) {
                if (!((PiercingWeapon) weapon).hasAlreadyHit(staticWarAttender)) {
                    staticWarAttender.changeHealth(-weapon.getBulletDamage()); //drain health of hit tank
                    break;
                }
            } else {
                staticWarAttender.changeHealth(-weapon.getBulletDamage());
            }
            if (staticWarAttender.isDestroyed) {
                replaceStaticWarAttenderTile(idx);
            }
        }
    }

    private void damageStaticWarAttender(int xPos, int yPos) {
        for (int idx = 0; idx < static_enemies.size(); ++idx) {
            if (static_enemies.get(idx).containsTilePosition(xPos, yPos)) {   // find the windmill by its tile position
                static_enemies.get(idx).changeHealth(-MovableWarAttender.DAMAGE_TO_DESTRUCTIBLE_TILE);
                if (static_enemies.get(idx).isDestroyed) {
                    replaceStaticWarAttenderTile(idx);
                }
                break;
            }
        }
    }

    private void replaceStaticWarAttenderTile(int idx) {
        StaticWarAttender staticWarAttender = static_enemies.get(idx);
        level_delete_listener.notifyForWarAttenderDeletion(staticWarAttender);
        explosion_sound.play(1.f, UserSettings.soundVolume);

        Vector2f[] collision_tiles = staticWarAttender.getCollisionTiles();

        for (Vector2f collision_tile : collision_tiles) {
            // look what tile lies below destroyed windmill (grass, dirt or concrete)
            int tileID = map.getTileId((int) collision_tile.x, (int) collision_tile.y, LANDSCAPE_TILES_LAYER_IDX);
            int replacement_tile_id = TileMapInfo.getReplacementTileID(tileID);
            if (replacement_tile_id != -1)
                map.setTileId((int) collision_tile.x, (int) collision_tile.y, DESTRUCTION_TILES_LAYER_IDX, replacement_tile_id);

            map.setTileId((int) collision_tile.x, (int) collision_tile.y, ENEMY_TILES_LAYER_IDX, 0);
        }

        Vector2f[] replacement_tiles = staticWarAttender.getReplacementTiles();
        if (replacement_tiles == null) return;

        for (Vector2f replacement_tile : replacement_tiles) {
            // simply remove the replacement tiles
            map.setTileId((int) replacement_tile.x, (int) replacement_tile.y, ENEMY_TILES_LAYER_IDX, 0);
        }
    }

    private static void damageTile(int xPos, int yPos, Weapon weapon, int replaceTileIndex, MovableWarAttender warAttender) {
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
                    smokeAnimation.play(warAttender.getPosition().x, warAttender.getPosition().y, warAttender.getRotation());
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

    private void handleHostileCollisions(MovableWarAttender player_warAttender, List<MovableWarAttender> friendly_war_attenders, int deltaTime) {
        for (MovableWarAttender hostile_warAttender : hostile_movable_war_attenders) {
            hostile_warAttender.shootAtEnemies(player_warAttender, friendly_war_attenders, deltaTime);
            handleShotCollisions(hostile_warAttender, player_warAttender);
            handleMovableWarAttenderCollisions(hostile_warAttender);
        }

        for (StaticWarAttender enemy_staticWarAttender : static_enemies) {
            enemy_staticWarAttender.shootAtEnemies(player_warAttender, friendly_war_attenders, deltaTime);
            handleShotCollisions(enemy_staticWarAttender, player_warAttender);
        }

        for (MovableWarAttender friendly_war_attender : friendly_war_attenders) {
            friendly_war_attender.shootAtEnemies(null, all_hostile_war_attenders, deltaTime);
            handleMovableWarAttenderCollisions(friendly_war_attender);
            handleShotCollisions(friendly_war_attender, null);
        }
    }

    private void handleShotCollisions(WarAttender w, MovableWarAttender player) {
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
                            player.changeHealth(-weapon.getBulletDamage());  //drain health of player
                        }
                        continue;
                    }
                }

                // BULLET COLLISION WITH DESTRUCTIBLE MAP TILE
                canContinue = handleGroundProjectileTileCollision(projectile, weapon, projectile_iterator);

                if (canContinue) continue;

                if (player == null) {
                    // FRIENDLY SHOT COLLISION WITH HOSTILE WAR ATTENDERS
                    for (MovableWarAttender hostile_warAttender : hostile_movable_war_attenders) {
                        if (projectile.getCollisionModel().intersects(hostile_warAttender.getCollisionModel())) {
                            showBulletHitAnimation(weapon, projectile);
                            projectile_iterator.remove();   // TODO: FIX CRASH BUG ON REMOVE
                            hostile_warAttender.changeHealth(-weapon.getBulletDamage());  //drain health of enemy
                            canContinue = true;
                        }
                    }
                    if (canContinue) continue;
                    // FRIENDLY SHOT COLLISION WITH STATIC WAR ATTENDERS
                    handleGroundProjectileStaticWarAttenderCollision(projectile, weapon, projectile_iterator);
                } else {
                    // HOSTILE SHOT COLLISION WITH FRIENDLY WAR ATTENDERS
                    for (MovableWarAttender friendly_warAttender : friendly_movable_war_attenders) {
                        if (projectile.getCollisionModel().intersects(friendly_warAttender.getCollisionModel())) {
                            showBulletHitAnimation(weapon, projectile);
                            projectile_iterator.remove();
                            friendly_warAttender.changeHealth(-weapon.getBulletDamage());  //drain health of friend
                        }
                    }
                    // HOSTILE SHOT COLLISION WITH FRIENDLY DRIVABLE ATTENDERS
                    for (MovableWarAttender drivable_warAttender : drivable_war_attenders) {
                        if (projectile.getCollisionModel().intersects(drivable_warAttender.getCollisionModel())) {
                            showBulletHitAnimation(weapon, projectile);
                            projectile_iterator.remove();
                            drivable_warAttender.changeHealth(-weapon.getBulletDamage());
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
    }
}
