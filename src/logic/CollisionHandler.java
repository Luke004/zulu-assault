package logic;

import models.war_attenders.WarAttender;
import models.war_attenders.soldiers.Soldier;
import models.war_attenders.tanks.Tank;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import player.Player;

import java.util.Iterator;
import java.util.List;

public class CollisionHandler {
    private Player player;
    private List<WarAttender> friendly_war_attenders, hostile_war_attenders;

    public CollisionHandler(Player player, List<WarAttender> friendly_war_attenders, List<WarAttender> hostile_war_attenders) {
        this.friendly_war_attenders = friendly_war_attenders;
        this.hostile_war_attenders = hostile_war_attenders;
        this.player = player;
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
                        if (b.getCollisionModel().intersects(hostile_warAttender.getCollisionModel())) {
                            // BULLET HIT HOSTILE WAR ATTENDER
                            bullet_iterator.remove();
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
