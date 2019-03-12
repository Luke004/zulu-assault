package logic;

import models.war_attenders.WarAttender;
import models.war_attenders.soldiers.Soldier;
import models.war_attenders.tanks.Tank;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.tiled.TiledMap;
import player.Player;

import java.util.Iterator;
import java.util.List;

public class CollisionHandler {
    private Player player;
    private List<WarAttender> friendly_war_attenders, hostile_war_attenders;
    private TiledMap level_map;
    private int MAP_WIDTH, MAP_HEIGHT, TILE_WIDTH, TILE_HEIGHT;

    public CollisionHandler(Player player, TiledMap level_map, List<WarAttender> friendly_war_attenders, List<WarAttender> hostile_war_attenders) {
        this.friendly_war_attenders = friendly_war_attenders;
        this.hostile_war_attenders = hostile_war_attenders;
        this.player = player;
        this.level_map = level_map;
        TILE_WIDTH = level_map.getTileWidth();
        TILE_HEIGHT = level_map.getTileHeight();
        MAP_WIDTH = level_map.getWidth() * TILE_WIDTH;
        MAP_HEIGHT = level_map.getHeight() * TILE_HEIGHT;
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


                // COLLISION BETWEEN TANK + BULLETS AND HOSTILE WAR ATTENDERS
                for (WarAttender hostile_warAttender : hostile_war_attenders) {
                    // TANK ITSELF
                    if (tank.isMoving()) {
                        if (tank.getCollisionModel().intersects(hostile_warAttender.getCollisionModel())) {
                            tank.onCollision(hostile_warAttender);
                        }
                    }
                    // TANK BULLET
                    Iterator<WarAttender.Bullet> bullet_iterator = tank.getBullets();
                    while (bullet_iterator.hasNext()) {
                        WarAttender.Bullet b = bullet_iterator.next();

                        // remove bullet if edge of map was reached
                        if (b.bullet_pos.x < 0) {
                            bullet_iterator.remove();
                            continue;
                        } else if (b.bullet_pos.y < 0) {
                            bullet_iterator.remove();
                            continue;
                        } else if (b.bullet_pos.x > MAP_WIDTH) {
                            bullet_iterator.remove();
                            continue;
                        } else if (b.bullet_pos.y > MAP_HEIGHT) {
                            bullet_iterator.remove();
                            continue;
                        }

                        // BULLET COLLISION WITH MAP TILE
                        //System.out.println(level_map.getTileId(1,4,0));
                        if(level_map.getTileId(
                                (int) b.bullet_pos.x / TILE_WIDTH,
                                (int) b.bullet_pos.y / TILE_HEIGHT,
                                0) != 9){
                            // NOT GRASS, REMOVE BULLET
                            bullet_iterator.remove();
                            continue;
                        }

                        // BULLET COLLISION WITH HOSTILE WAR ATTENDER
                        if (b.getCollisionModel().intersects(hostile_warAttender.getCollisionModel())) {
                            bullet_iterator.remove();   // remove bullet
                            hostile_warAttender.changeHealth(-tank.getBulletDamage());  //drain health of hit tank
                            //continue;
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

                break;
            case PLANE:     // player is in a plane

                break;

        }

    }

}
