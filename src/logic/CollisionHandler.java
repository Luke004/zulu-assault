package logic;

import models.war_attenders.WarAttender;
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
    private int MAP_WIDTH, MAP_HEIGHT, TILE_WIDTH, TILE_HEIGHT;
    private int[] destructible_tile_indices, indestructible_tile_indices, destructible_tile_replace_indices;
    final int DESTRUCTIBLE_TILE_MAX_HEALTH = 15;
    private final int LANDSCAPE_TILES_LAYER_IDX = 0;
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
        destructible_tile_indices = new int[]{1, 2, 18, 19, 25, 65, 68, 83, 88, 89};
        destructible_tile_replace_indices = new int[]{32, 33, 34, 35, 36, 37, 95, 94, 93, 91};
        indestructible_tile_indices = new int[]{40, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 64, 65, 66, 67, 68,
                69, 72, 73, 74, 75, 76, 77, 83, 89};
        destructible_tiles_health_info = new HashMap<>();

        // create TileInfo for 'landscape_tiles' TileSet
        final int LANDSCAPE_TILES_TILESET_IDX = 1;
        TileSet landscape_tiles = level_map.getTileSet(LANDSCAPE_TILES_TILESET_IDX);
        if (!landscape_tiles.name.equals("landscape_tiles"))
            throw new IllegalAccessError("Wrong tileset index: [" + LANDSCAPE_TILES_TILESET_IDX + "] is not landscape_tiles");
        else {
            for (int idx = 0; idx < destructible_tile_indices.length; ++idx) {
                destructible_tile_indices[idx] += landscape_tiles.firstGID;
                destructible_tile_replace_indices[idx] += landscape_tiles.firstGID;
            }
        }
    }

    public void update(GameContainer gameContainer, int deltaTime) {
        WarAttender player_warAttender = player.getWarAttender();
        if (player_warAttender.isMoving()) {
            // COLLISION BETWEEN PLAYER ITSELF AND FRIENDLY WAR ATTENDERS
            for (WarAttender warAttender : friendly_war_attenders) {
                if (player_warAttender.getCollisionModel().intersects(warAttender.getCollisionModel())) {
                    player_warAttender.onCollision(warAttender);
                }
            }
            // COLLISION PLAYER ITSELF AND HOSTILE WAR ATTENDERS
            for (WarAttender hostile_warAttender : hostile_war_attenders) {
                if (player_warAttender.getCollisionModel().intersects(hostile_warAttender.getCollisionModel())) {
                    player_warAttender.onCollision(hostile_warAttender);
                }
            }
        }

        // PLAYER BULLET COLLISIONS
        Iterator<WarAttender.Bullet> bullet_iterator = player_warAttender.getBullets();
        while (bullet_iterator.hasNext()) {
            WarAttender.Bullet b = bullet_iterator.next();
            boolean canContinue = false;

            // BULLET COLLISION WITH HOSTILE WAR ATTENDER
            for (WarAttender hostile_warAttender : hostile_war_attenders) {
                if (b.getCollisionModel().intersects(hostile_warAttender.getCollisionModel())) {
                    bullet_iterator.remove();   // remove bullet
                    hostile_warAttender.changeHealth(-player_warAttender.getBulletDamage());  //drain health of hit tank
                    canContinue = true;
                    break;
                }
            }

            if (canContinue) continue;

            // BULLET COLLISION WITH MAP TILE
            for (int idx = 0; idx < destructible_tile_indices.length; ++idx) {
                int x = (int) b.bullet_pos.x / TILE_WIDTH;
                int y = (int) b.bullet_pos.y / TILE_HEIGHT;
                int tile_ID = level_map.getTileId(x, y, LANDSCAPE_TILES_LAYER_IDX);
                if (tile_ID == destructible_tile_indices[idx]) {
                    if (player_warAttender.getBulletDamage() >= DESTRUCTIBLE_TILE_MAX_HEALTH) {
                        level_map.setTileId(x, y, LANDSCAPE_TILES_LAYER_IDX, 42);
                    } else {
                        // use a map to track current destructible tile health
                        int key = x > y ? -x * y : x * y;
                        if (destructible_tiles_health_info.containsKey(key)) {
                            int new_health = destructible_tiles_health_info.get(key) - player_warAttender.getBulletDamage();
                            if (new_health <= 0) {
                                // TILE DESTROYED
                                level_map.setTileId(x, y, LANDSCAPE_TILES_LAYER_IDX, destructible_tile_replace_indices[idx]);
                                destructible_tiles_health_info.remove(key);
                            } else {
                                destructible_tiles_health_info.put(key, new_health);
                            }
                        } else {
                            destructible_tiles_health_info.put(key, DESTRUCTIBLE_TILE_MAX_HEALTH - player_warAttender.getBulletDamage());
                        }
                    }
                    bullet_iterator.remove();
                    canContinue = true;
                    break;
                }
            }

            if (canContinue) continue;

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
}
