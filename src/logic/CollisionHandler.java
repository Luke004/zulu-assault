package logic;

import models.CollisionModel;
import models.war_attenders.WarAttender;
import models.war_attenders.soldiers.Soldier;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.tiled.TileSet;
import org.newdawn.slick.tiled.TiledMap;
import player.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CollisionHandler {
    private Player player;
    private List<WarAttender> friendly_war_attenders, hostile_war_attenders;
    private TiledMap level_map;
    private final int MAP_WIDTH, MAP_HEIGHT, TILE_WIDTH, TILE_HEIGHT;
    private int[] destructible_tile_indices, indestructible_tile_indices, destructible_tile_replace_indices, item_indices;
    private final int DESTRUCTIBLE_TILE_MAX_HEALTH = 15;
    private final int LANDSCAPE_TILES_LAYER_IDX = 0;
    private final int ITEM_TILES_LAYER_IDX = 2;
    private Map<Integer, Integer> destructible_tiles_health_info;


    public CollisionHandler(Player player, TiledMap level_map, List<WarAttender> friendly_war_attenders, List<WarAttender> hostile_war_attenders) {
        this.friendly_war_attenders = friendly_war_attenders;
        this.hostile_war_attenders = hostile_war_attenders;
        this.player = player;
        this.level_map = level_map;
        TILE_WIDTH = level_map.getTileWidth();
        TILE_HEIGHT = level_map.getTileHeight();
        MAP_WIDTH = level_map.getWidth() * TILE_WIDTH;
        MAP_HEIGHT = level_map.getHeight() * TILE_HEIGHT;

        // TileMap related stuff
        item_indices = new int[]{0, 16, 24, 40, 56};
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
        }

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
    }

    public void update(GameContainer gameContainer, int deltaTime) {
        WarAttender player_warAttender = player.getWarAttender();
        handlePlayerCollisions(player_warAttender);
        handleBulletCollisions(player_warAttender);
        handleHostileShots(player_warAttender);

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

    private void handlePlayerCollisions(WarAttender player_warAttender) {
        if (player_warAttender.isMoving()) {
            CollisionModel.Point[] playerCorners = player_warAttender.getCollisionModel().getPoints();
            int idx, landscape_layer_tile_ID, item_layer_tile_ID, x, y;

            for (CollisionModel.Point p : playerCorners) {
                x = (int) p.x / TILE_WIDTH;
                y = (int) p.y / TILE_HEIGHT;
                landscape_layer_tile_ID = level_map.getTileId(x, y, LANDSCAPE_TILES_LAYER_IDX);
                item_layer_tile_ID = level_map.getTileId(x, y, ITEM_TILES_LAYER_IDX);

                // COLLISION BETWEEN PLAYER ITSELF AND DESTRUCTIBLE TILES
                for (idx = 0; idx < destructible_tile_indices.length; ++idx) {
                    if (landscape_layer_tile_ID == destructible_tile_indices[idx]) {
                        // block movement as long as tile exists and damage the destructible tile
                        player_warAttender.blockMovement();

                        // damage ONLY when we are not a solider
                        if (player_warAttender instanceof Soldier) return;

                        damageTile(x, y, 1, destructible_tile_replace_indices[idx]);
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

                // COLLISION BETWEEN PLAYER ITSELF AND ITEMS
                for (idx = 0; idx < item_indices.length; ++idx) {
                    if (item_layer_tile_ID == item_indices[idx]) {
                        player.addItem(Player.Item.INVINCIBLE);
                        return;
                    }
                }
            }


            // COLLISION BETWEEN PLAYER ITSELF AND FRIENDLY WAR ATTENDERS
            for (WarAttender warAttender : friendly_war_attenders) {
                if (player_warAttender.getCollisionModel().intersects(warAttender.getCollisionModel())) {
                    player_warAttender.onCollision(warAttender);
                    return;
                }
            }
            // COLLISION BETWEEN PLAYER ITSELF AND HOSTILE WAR ATTENDERS
            for (WarAttender hostile_warAttender : hostile_war_attenders) {
                if (player_warAttender.getCollisionModel().intersects(hostile_warAttender.getCollisionModel())) {
                    player_warAttender.onCollision(hostile_warAttender);
                    return;
                }
            }
        }
    }

    private void handleBulletCollisions(WarAttender player_warAttender) {
        // PLAYER BULLET COLLISIONS
        Iterator<WarAttender.Bullet> bullet_iterator = player_warAttender.getBullets();
        while (bullet_iterator.hasNext()) {
            WarAttender.Bullet b = bullet_iterator.next();
            boolean canContinue = false;

            // PLAYER BULLET COLLISION WITH HOSTILE WAR ATTENDER
            for (WarAttender hostile_warAttender : hostile_war_attenders) {
                if (b.getCollisionModel().intersects(hostile_warAttender.getCollisionModel())) {
                    bullet_iterator.remove();   // remove bullet
                    hostile_warAttender.changeHealth(-player_warAttender.getBulletDamage());  //drain health of hit tank
                    canContinue = true;
                    break;
                }
            }

            if (canContinue) continue;

            // PLAYER BULLET COLLISION WITH DESTRUCTIBLE MAP TILE
            canContinue = handleBulletTileCollision(b, player_warAttender.getBulletDamage(), bullet_iterator);

            if (canContinue) continue;

            removeBulletAtMapEdge(b, bullet_iterator);
        }
    }

    private void damageTile(int xPos, int yPos, int damage, int replaceTileIndex) {
        // use a map to track current destructible tile health
        int key = xPos > yPos ? -xPos * yPos : xPos * yPos;
        if (destructible_tiles_health_info.containsKey(key)) {
            int new_health = destructible_tiles_health_info.get(key) - damage;
            if (new_health <= 0) {
                // TILE DESTROYED
                level_map.setTileId(xPos, yPos, LANDSCAPE_TILES_LAYER_IDX, replaceTileIndex);
                destructible_tiles_health_info.remove(key);
            } else {
                destructible_tiles_health_info.put(key, new_health);
            }
        } else {
            destructible_tiles_health_info.put(key, DESTRUCTIBLE_TILE_MAX_HEALTH - damage);
        }
    }

    private void handleHostileShots(WarAttender player_warAttender) {
        for (WarAttender hostile_warAttender : hostile_war_attenders) {
            hostile_warAttender.shootAtPlayer(player_warAttender);

            Iterator<WarAttender.Bullet> bullet_iterator = hostile_warAttender.getBullets();
            while (bullet_iterator.hasNext()) {
                WarAttender.Bullet b = bullet_iterator.next();
                boolean canContinue = false;

                // HOSTILE BULLET COLLISION WITH PLAYER
                if (b.getCollisionModel().intersects(player_warAttender.getCollisionModel())) {
                    bullet_iterator.remove();   // remove bullet
                    player_warAttender.changeHealth(-hostile_warAttender.getBulletDamage());  //drain health of player
                    canContinue = true;
                }

                if (canContinue) continue;

                // HOSTILE BULLET COLLISION WITH DESTRUCTIBLE MAP TILE
                canContinue = handleBulletTileCollision(b, hostile_warAttender.getBulletDamage(), bullet_iterator);

                if (canContinue) continue;

                removeBulletAtMapEdge(b, bullet_iterator);
            }
        }
    }

    private boolean handleBulletTileCollision(WarAttender.Bullet b, int bulletDamage, Iterator<WarAttender.Bullet> bullet_iterator) {
        for (int idx = 0; idx < destructible_tile_indices.length; ++idx) {
            int x = (int) b.bullet_pos.x / TILE_WIDTH;
            int y = (int) b.bullet_pos.y / TILE_HEIGHT;
            int tile_ID = level_map.getTileId(x, y, LANDSCAPE_TILES_LAYER_IDX);
            if (tile_ID == destructible_tile_indices[idx]) {
                if (bulletDamage >= DESTRUCTIBLE_TILE_MAX_HEALTH) {
                    // it's a one shot, destroy tile directly
                    level_map.setTileId(x, y, LANDSCAPE_TILES_LAYER_IDX, destructible_tile_replace_indices[idx]);
                } else {
                    damageTile(x, y, bulletDamage, destructible_tile_replace_indices[idx]);
                }
                bullet_iterator.remove();
                return true;
            }
        }
        return false;
    }

    private void removeBulletAtMapEdge(WarAttender.Bullet b, Iterator<WarAttender.Bullet> bullet_iterator) {
        // remove bullet if edge of map was reached
        if (b.bullet_pos.x < 0) {
            bullet_iterator.remove();
        } else if (b.bullet_pos.y < 0) {
            bullet_iterator.remove();
        } else if (b.bullet_pos.x > MAP_WIDTH) {
            bullet_iterator.remove();
        } else if (b.bullet_pos.y > MAP_HEIGHT) {
            bullet_iterator.remove();
        }
    }


}
