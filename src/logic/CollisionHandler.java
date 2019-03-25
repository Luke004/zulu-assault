package logic;

import models.CollisionModel;
import models.animations.SmokeAnimation;
import models.interaction_circles.HealthCircle;
import models.interaction_circles.InteractionCircle;
import models.interaction_circles.TeleportCircle;
import models.war_attenders.MovableWarAttender;
import models.war_attenders.WarAttender;
import models.war_attenders.soldiers.Soldier;
import models.war_attenders.windmills.Windmill;
import models.weapons.MegaPulse;
import models.weapons.RocketLauncher;
import models.weapons.Shell;
import models.weapons.Weapon;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.tiled.TileSet;
import org.newdawn.slick.tiled.TiledMap;
import player.Player;
import models.weapons.Weapon.*;

import java.util.*;

public class CollisionHandler {
    private Player player;
    private List<MovableWarAttender> friendly_war_attenders, hostile_war_attenders;
    private List<Windmill> enemy_windmills;
    private List<InteractionCircle> interaction_circles;
    private TiledMap level_map;
    private final int MAP_WIDTH, MAP_HEIGHT, TILE_WIDTH, TILE_HEIGHT;
    private int[] destructible_tile_indices, indestructible_tile_indices, destructible_tile_replace_indices, item_indices,
            windmill_indices, windmill_replace_indices;
    private final float TILE_HEALTH = 100;
    private final float DESTRUCTIBLE_TILE_NORMAL_ARMOR = 5.f;
    private final float DESTRUCTIBLE_TILE_LOW_ARMOR = 1.f;
    private final int LANDSCAPE_TILES_LAYER_IDX = 0;
    private final int ITEM_TILES_LAYER_IDX = 2;
    private final int ENEMY_TILES_LAYER_IDX = 3;
    private final int GRASS_IDX, CONCRETE_IDX, DIRT_IDX;
    private Map<Integer, Float> destructible_tiles_health_info;

    private SmokeAnimation smokeAnimation;


    public CollisionHandler(Player player, TiledMap level_map, List<MovableWarAttender> friendly_war_attenders,
                            List<MovableWarAttender> hostile_war_attenders, List<Windmill> enemy_windmills, List<InteractionCircle> interaction_circles) {
        this.friendly_war_attenders = friendly_war_attenders;
        this.hostile_war_attenders = hostile_war_attenders;
        this.enemy_windmills = enemy_windmills;
        this.interaction_circles = interaction_circles;
        this.player = player;
        this.level_map = level_map;
        TILE_WIDTH = level_map.getTileWidth();
        TILE_HEIGHT = level_map.getTileHeight();
        MAP_WIDTH = level_map.getWidth() * TILE_WIDTH;
        MAP_HEIGHT = level_map.getHeight() * TILE_HEIGHT;

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
        smokeAnimation = new SmokeAnimation(1);

    }

    public void draw() {
        smokeAnimation.draw();
    }

    public void update(GameContainer gameContainer, int deltaTime) {
        smokeAnimation.update(deltaTime);

        MovableWarAttender player_warAttender = player.getWarAttender();
        handlePlayerCollisions(player_warAttender);
        handleBulletCollisions(player_warAttender);
        updateHostileShots(player_warAttender, friendly_war_attenders, deltaTime);

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

    private void handlePlayerCollisions(MovableWarAttender player_warAttender) {
        if (player_warAttender.isMoving()) {
            CollisionModel.Point[] playerCorners = player_warAttender.getCollisionModel().getPoints();
            int idx, landscape_layer_tile_ID, item_layer_tile_ID, enemy_layer_tile_ID, x, y;

            for (CollisionModel.Point p : playerCorners) {
                x = (int) p.x / TILE_WIDTH;
                y = (int) p.y / TILE_HEIGHT;
                landscape_layer_tile_ID = level_map.getTileId(x, y, LANDSCAPE_TILES_LAYER_IDX);
                item_layer_tile_ID = level_map.getTileId(x, y, ITEM_TILES_LAYER_IDX);
                enemy_layer_tile_ID = level_map.getTileId(x, y, ENEMY_TILES_LAYER_IDX);

                // COLLISION BETWEEN PLAYER ITSELF AND DESTRUCTIBLE TILES
                for (idx = 0; idx < destructible_tile_indices.length; ++idx) {
                    if (landscape_layer_tile_ID == destructible_tile_indices[idx]) {
                        // block movement as long as tile exists and damage the destructible tile
                        player_warAttender.blockMovement();

                        // damage ONLY when we are not a solider
                        if (player_warAttender instanceof Soldier) return;

                        damageTile(x, y, MovableWarAttender.DAMAGE_TO_DESTRUCTIBLE_TILE, destructible_tile_replace_indices[idx]);
                        return;
                    }
                }

                // COLLISION BETWEEN PLAYER ITSELF AND INDESTRUCTIBLE TILES
                for (idx = 0; idx < indestructible_tile_indices.length; ++idx) {
                    if (landscape_layer_tile_ID == indestructible_tile_indices[idx]) {
                        // block movement forever because tile is indestructible
                        player_warAttender.blockMovement();
                        return;
                    }
                }

                // COLLISION BETWEEN PLAYER ITSELF AND WINDMILLS
                for (idx = 0; idx < windmill_indices.length; ++idx) {
                    if (enemy_layer_tile_ID == windmill_indices[idx]) {
                        // block movement as long as tile exists and damage the destructible tile
                        player_warAttender.blockMovement();

                        // damage ONLY when we are not a solider
                        if (player_warAttender instanceof Soldier) return;

                        damageWindmill(x, y, 1.f);
                        return;
                    }
                }

                // COLLISION BETWEEN PLAYER ITSELF AND ITEMS
                for (idx = 0; idx < item_indices.length; ++idx) {
                    if (item_layer_tile_ID == item_indices[idx]) {
                        switch (idx) {
                            case 0:
                                player.addItem(Player.Item.INVINCIBLE);
                                break;
                            case 1:
                                player.addItem(Player.Item.EMP);
                                break;
                            case 2:
                                player.addItem(Player.Item.MEGA_PULSE);
                                break;
                            case 3:
                                player.addItem(Player.Item.EXPAND);
                                break;
                            case 4: // silver wrench
                                // don't take the wrench if player is at max health
                                if (player_warAttender.isMaxHealth()) return;
                                player_warAttender.changeHealth(10);
                                break;
                            case 5: // golden wrench
                                // don't take the wrench if player is at max health
                                if (player_warAttender.isMaxHealth()) return;
                                player_warAttender.changeHealth(50);
                                break;
                            default:
                                return;
                        }
                        level_map.setTileId(x, y, ITEM_TILES_LAYER_IDX, 0); // delete the item tile
                    }
                }
            }


            // COLLISION BETWEEN PLAYER ITSELF AND FRIENDLY WAR ATTENDERS
            for (MovableWarAttender warAttender : friendly_war_attenders) {
                if (player_warAttender.getCollisionModel().intersects(warAttender.getCollisionModel())) {
                    player_warAttender.onCollision(warAttender);
                    return;
                }
            }
            // COLLISION BETWEEN PLAYER ITSELF AND HOSTILE WAR ATTENDERS
            for (MovableWarAttender hostile_warAttender : hostile_war_attenders) {
                if (player_warAttender.getCollisionModel().intersects(hostile_warAttender.getCollisionModel())) {
                    player_warAttender.onCollision(hostile_warAttender);
                    return;
                }
            }
        }
    }

    private void handleBulletCollisions(MovableWarAttender player_warAttender) {
        // PLAYER BULLET COLLISIONS
        for (Weapon weapon : player_warAttender.getWeapons()) {
            Iterator<Bullet> bullet_iterator = weapon.getBullets();
            while (bullet_iterator.hasNext()) {
                Bullet b = bullet_iterator.next();
                boolean canContinue;

                canContinue = removeBulletAtMapEdge(b, bullet_iterator);

                if (canContinue) continue;

                // PLAYER BULLET COLLISION WITH HOSTILE WAR ATTENDER
                for (int idx = 0; idx < hostile_war_attenders.size(); ++idx) {
                    if (b.getCollisionModel().intersects(hostile_war_attenders.get(idx).getCollisionModel())) {
                        if (weapon instanceof MegaPulse) {
                            if (!((MegaPulse) weapon).hasAlreadyHit(idx)) {
                                hostile_war_attenders.get(idx).changeHealth(-weapon.getBulletDamage()); //drain health of hit tank
                            }
                            continue;
                        }
                        hostile_war_attenders.get(idx).changeHealth(-weapon.getBulletDamage()); //drain health of hit tank
                        bullet_iterator.remove();   // remove bullet
                        canContinue = true;
                        break;
                    }
                }

                if (canContinue) continue;

                // PLAYER BULLET COLLISION WITH DESTRUCTIBLE MAP TILE
                canContinue = handleBulletTileCollision(b, weapon, bullet_iterator);

                if (canContinue) continue;

                // PLAYER BULLET COLLISION WITH WINDMILL
                handleBulletWindmillCollision(b, weapon, bullet_iterator);

            }
        }


    }

    private void damageWindmill(int xPos, int yPos, Weapon weapon) {
        // use a map to track current destructible tile health
        int key = xPos > yPos ? -xPos * yPos : xPos * yPos;
        int idx;

        if (weapon instanceof MegaPulse) {
            for (idx = 0; idx < enemy_windmills.size(); ++idx) {
                if (enemy_windmills.get(idx).getKey() == key) {
                    if (!((MegaPulse) weapon).hasAlreadyHit(key)) {
                        enemy_windmills.get(idx).changeHealth(-weapon.getBulletDamage()); //drain health of hit tank
                        break;
                    }
                    return;
                }
            }
        } else {
            for (idx = 0; idx < enemy_windmills.size(); ++idx) {
                if (enemy_windmills.get(idx).getKey() == key) {
                    enemy_windmills.get(idx).changeHealth(-weapon.getBulletDamage());
                    break;
                }
            }
        }
        damageWindmill_part2(key, idx, xPos, yPos, weapon.getBulletDamage());
    }

    private void damageWindmill(int xPos, int yPos, float damage) {
        // use a map to track current destructible tile health
        int key = xPos > yPos ? -xPos * yPos : xPos * yPos;
        int idx;

        for (idx = 0; idx < enemy_windmills.size(); ++idx) {
            if (enemy_windmills.get(idx).getKey() == key) {
                enemy_windmills.get(idx).changeHealth(-damage / Windmill.ARMOR);
                break;
            }
        }
        damageWindmill_part2(key, idx, xPos, yPos, damage);
    }

    private void damageWindmill_part2(int key, int idx, int xPos, int yPos, float damage) {
        if (destructible_tiles_health_info.containsKey(key)) {
            float new_health = destructible_tiles_health_info.get(key) - damage / Windmill.ARMOR;
            if (new_health <= 0) {
                // TILE DESTROYED

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
                destructible_tiles_health_info.remove(key);
            } else {
                destructible_tiles_health_info.put(key, new_health);
            }
        } else {
            destructible_tiles_health_info.put(key, Windmill.HEALTH - damage / Windmill.ARMOR);
        }
    }

    private void damageTile(int xPos, int yPos, float damage, int replaceTileIndex) {
        // use a map to track current destructible tile health
        int key = xPos > yPos ? -xPos * yPos : xPos * yPos;
        if (destructible_tiles_health_info.containsKey(key)) {
            float new_health = destructible_tiles_health_info.get(key) - damage / DESTRUCTIBLE_TILE_NORMAL_ARMOR;
            if (new_health <= 0) {
                // TILE DESTROYED
                if(damage == MovableWarAttender.DAMAGE_TO_DESTRUCTIBLE_TILE){
                    // show smoke animation only when drove over tile, not bullet destruction
                    MovableWarAttender playerWarAttender = player.getWarAttender();
                    smokeAnimation.play(playerWarAttender.position.x, playerWarAttender.position.y, playerWarAttender.getRotation());
                }
                level_map.setTileId(xPos, yPos, LANDSCAPE_TILES_LAYER_IDX, replaceTileIndex);
                destructible_tiles_health_info.remove(key);
            } else {
                destructible_tiles_health_info.put(key, new_health);
            }
        } else {
            if (replaceTileIndex == 44) {  // this ONE low health tile cactus thing LOL
                destructible_tiles_health_info.put(key, TILE_HEALTH - damage / DESTRUCTIBLE_TILE_LOW_ARMOR);
                return;
            }
            destructible_tiles_health_info.put(key, TILE_HEALTH - damage / DESTRUCTIBLE_TILE_NORMAL_ARMOR);
        }
    }

    private void updateHostileShots(MovableWarAttender player_warAttender, List<MovableWarAttender> friendly_war_attenders, int deltaTime) {
        for (MovableWarAttender hostile_warAttender : hostile_war_attenders) {
            hostile_warAttender.shootAtEnemies(player_warAttender, friendly_war_attenders, deltaTime);
            hostileShotCollision(hostile_warAttender, player_warAttender);
        }

        for (WarAttender enemy_windmill : enemy_windmills) {
            enemy_windmill.shootAtEnemies(player_warAttender, friendly_war_attenders, deltaTime);
            hostileShotCollision(enemy_windmill, player_warAttender);
        }
    }

    private void hostileShotCollision(WarAttender w, MovableWarAttender player) {
        for (Weapon weapon : w.getWeapons()) {
            Iterator<Bullet> bullet_iterator = weapon.getBullets();
            while (bullet_iterator.hasNext()) {
                Bullet b = bullet_iterator.next();
                boolean canContinue;

                canContinue = removeBulletAtMapEdge(b, bullet_iterator);

                if (canContinue) continue;

                // HOSTILE SHOT COLLISION WITH PLAYER
                if (b.getCollisionModel().intersects(player.getCollisionModel())) {
                    bullet_iterator.remove();   // remove bullet
                    if (!player.isInvincible()) {
                        player.changeHealth(-weapon.getBulletDamage());  //drain health of player
                    }
                    canContinue = true;
                }

                if (canContinue) continue;

                // HOSTILE BULLET COLLISION WITH DESTRUCTIBLE MAP TILE
                canContinue = handleBulletTileCollision(b, weapon, bullet_iterator);

                if (canContinue) continue;

                // HOSTILE SHOT COLLISION WITH FRIENDLY WAR ATTENDERS
                for (MovableWarAttender friendly_warAttender : friendly_war_attenders) {
                    if (b.getCollisionModel().intersects(friendly_warAttender.getCollisionModel())) {
                        bullet_iterator.remove();
                        friendly_warAttender.changeHealth(-weapon.getBulletDamage());  //drain health of friend
                    }
                }
            }
        }
    }

    private boolean handleBulletTileCollision(Bullet b, Weapon weapon, Iterator<Bullet> bullet_iterator) {
        int x = (int) b.bullet_pos.x / TILE_WIDTH;
        int y = (int) b.bullet_pos.y / TILE_HEIGHT;
        int tile_ID = level_map.getTileId(x, y, LANDSCAPE_TILES_LAYER_IDX);

        for (int idx = 0; idx < destructible_tile_indices.length; ++idx) {
            if (tile_ID == destructible_tile_indices[idx]) {
                if (weapon instanceof RocketLauncher || weapon instanceof Shell) {
                    // it's a one shot, destroy tile directly and maybe other tiles around
                    doCollateralTileDamage(x, y, idx);
                } else {
                    damageTile(x, y, weapon.getBulletDamage(), destructible_tile_replace_indices[idx]);
                }
                if (weapon instanceof MegaPulse) continue;
                bullet_iterator.remove();
                return true;
            }
        }
        return false;
    }

    private void doCollateralTileDamage(int x, int y, int idx) {
        // destroy the hit tile directly
        level_map.setTileId(x, y, LANDSCAPE_TILES_LAYER_IDX, destructible_tile_replace_indices[idx]);

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

    private void handleBulletWindmillCollision(Bullet b, Weapon weapon, Iterator<Bullet> bullet_iterator) {
        for (int idx = 0; idx < windmill_indices.length; ++idx) {
            int x = (int) b.bullet_pos.x / TILE_WIDTH;
            int y = (int) b.bullet_pos.y / TILE_HEIGHT;
            int tile_ID = level_map.getTileId(x, y, ENEMY_TILES_LAYER_IDX);
            if (tile_ID == windmill_indices[idx]) {
                damageWindmill(x, y, weapon);
                if (weapon instanceof MegaPulse) continue;
                bullet_iterator.remove();
            }
        }
    }

    private boolean removeBulletAtMapEdge(Bullet b, Iterator<Bullet> bullet_iterator) {
        // remove bullet if edge of map was reached
        if (b.bullet_pos.x < 0) {
            bullet_iterator.remove();
            return true;
        } else if (b.bullet_pos.y < 0) {
            bullet_iterator.remove();
            return true;
        } else if (b.bullet_pos.x > MAP_WIDTH - 1) {
            bullet_iterator.remove();
            return true;
        } else if (b.bullet_pos.y > MAP_HEIGHT - 1) {
            bullet_iterator.remove();
            return true;
        }
        return false;
    }

    private class Tile {
        int tileID, xVal, yVal, key;

        Tile(int xVal, int yVal, int tileID) {
            this.tileID = tileID;
            this.xVal = xVal;
            this.yVal = yVal;
            this.key = xVal > yVal ? -xVal * yVal : xVal * yVal;
        }
    }
}
