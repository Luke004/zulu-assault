package logic;

import models.war_attenders.WarAttender;
import models.war_attenders.soldiers.Soldier;
import models.war_attenders.tanks.Tank;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
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
    private int[] destructible_tile_indices, indestructible_tile_indices;
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
        destructible_tile_indices = new int[]{1, 2, 18, 19, 25};
        indestructible_tile_indices = new int[]{40, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 64, 65, 66, 67, 68,
                69, 72, 73, 74, 75, 76, 77, 83, 89};
        destructible_tiles_health_info = new HashMap<>();

        // create TileInfo for 'landscape_tiles' TileSet
        int LANDSCAPE_TILES_TILESET_IDX = 1;
        TileSet landscape_tiles = level_map.getTileSet(LANDSCAPE_TILES_TILESET_IDX);
        if (!landscape_tiles.name.equals("landscape_tiles"))
            throw new IllegalAccessError("Wrong tileset index: [" + LANDSCAPE_TILES_TILESET_IDX + "] is not landscape_tiles");
        else {
            for (int idx = 0; idx < destructible_tile_indices.length; ++idx) {
                destructible_tile_indices[idx] += landscape_tiles.firstGID;
            }
        }
    }

    public void update(GameContainer gameContainer, int deltaTime) {
        Input input = gameContainer.getInput();
        switch (player.getWarAttenderType()) {
            case SOLDIER:   // player is a soldier (goes by foot)
                Soldier soldier = (Soldier) player.getWarAttender();

                if (soldier.isMoving()) {
                    for (WarAttender warAttender : friendly_war_attenders) {
                        if (soldier.getCollisionModel().intersects(warAttender.getCollisionModel())) {
                            soldier.onCollision(warAttender);
                        }
                    }
                }

                break;
            case TANK:      // player is in a tank
                Tank tank = (Tank) player.getWarAttender();

                // COLLISION BETWEEN TANK ITSELF AND HOSTILE WAR ATTENDERS
                for (WarAttender hostile_warAttender : hostile_war_attenders) {
                    if (tank.isMoving()) {
                        if (tank.getCollisionModel().intersects(hostile_warAttender.getCollisionModel())) {
                            tank.onCollision(hostile_warAttender);
                        }
                    }
                }

                // COLLISION BETWEEN TANK ITSELF AND FRIENDLY WAR ATTENDERS
                for (WarAttender friendly_warAttender : friendly_war_attenders) {
                    if (tank.isMoving()) {
                        if (tank.getCollisionModel().intersects(friendly_warAttender.getCollisionModel())) {
                            tank.onCollision(friendly_warAttender);
                        }
                    }
                }

                // TANK BULLET COLLISIONS
                Iterator<WarAttender.Bullet> bullet_iterator = tank.getBullets();
                while (bullet_iterator.hasNext()) {
                    WarAttender.Bullet b = bullet_iterator.next();
                    boolean canContinue = false;

                    // BULLET COLLISION WITH HOSTILE WAR ATTENDER
                    for (WarAttender hostile_warAttender : hostile_war_attenders) {
                        if (b.getCollisionModel().intersects(hostile_warAttender.getCollisionModel())) {
                            bullet_iterator.remove();   // remove bullet
                            hostile_warAttender.changeHealth(-tank.getBulletDamage());  //drain health of hit tank
                            canContinue = true;
                            break;
                        }
                    }

                    if(canContinue) continue;

                    // BULLET COLLISION WITH MAP TILE
                    for (int idx = 0; idx < destructible_tile_indices.length; ++idx) {
                        int x = (int) b.bullet_pos.x / TILE_WIDTH;
                        int y = (int) b.bullet_pos.y / TILE_HEIGHT;
                        int tile_ID = level_map.getTileId(x, y, LANDSCAPE_TILES_LAYER_IDX);
                        if (tile_ID == destructible_tile_indices[idx]) {
                            if (tank.getBulletDamage() >= DESTRUCTIBLE_TILE_MAX_HEALTH) {
                                level_map.setTileId(x, y, LANDSCAPE_TILES_LAYER_IDX, 42);
                            } else {
                                // use a map to track current destructible tile health
                                int key = x > y ? -x * y : x * y;
                                if (destructible_tiles_health_info.containsKey(key)) {
                                    int new_health = destructible_tiles_health_info.get(key) - tank.getBulletDamage();
                                    if (new_health <= 0) {
                                        // TILE DESTROYED
                                        level_map.setTileId(x, y, LANDSCAPE_TILES_LAYER_IDX, 42);
                                        destructible_tiles_health_info.remove(key);
                                    } else {
                                        destructible_tiles_health_info.put(key, new_health);
                                    }
                                } else {
                                    destructible_tiles_health_info.put(key, DESTRUCTIBLE_TILE_MAX_HEALTH - tank.getBulletDamage());
                                }
                            }
                            bullet_iterator.remove();
                            canContinue = true;
                            break;
                        }
                    }

                    if(canContinue) continue;

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
                break;
            case PLANE:     // player is in a plane

                break;

        }

    }
}
