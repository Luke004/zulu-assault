package logic;

import models.war_attenders.WarAttender;
import models.war_attenders.soldiers.Soldier;
import models.war_attenders.tanks.Tank;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import player.Player;

import java.util.ArrayList;

public class CollisionHandler {
    private Player player;
    private ArrayList<WarAttender> level_war_attenders;

    public CollisionHandler(Player player, ArrayList<WarAttender> level_war_attenders) {
        this.level_war_attenders = level_war_attenders;
        this.player = player;
    }

    public void update(GameContainer gameContainer, int deltaTime) {
        Input input = gameContainer.getInput();
        switch (player.getWarAttenderType()) {
            case SOLDIER:   // player is a soldier (goes by foot)
                Soldier soldier = (Soldier) player.getWarAttender();

                if(soldier.isMoving()){
                    for(WarAttender warAttender : level_war_attenders){
                        if(soldier.getCollisionModel().intersects(warAttender.getCollisionModel())){
                            soldier.onCollision(warAttender);
                        }
                    }
                }

                break;
            case TANK:      // player is in a tank
                Tank tank = (Tank) player.getWarAttender();

                if(tank.isMoving()){
                    for(WarAttender enemy : level_war_attenders){
                        if(tank.getCollisionModel().intersects(enemy.getCollisionModel())){
                            tank.onCollision(enemy);
                        }
                    }
                }


                break;
            case PLANE:     // player is in a plane

                break;

        }

    }

}
