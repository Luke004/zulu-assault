package logic;

import models.war_attenders.WarAttender;
import models.war_attenders.soldiers.PlayerSoldier;
import models.war_attenders.tanks.Tank;
import org.newdawn.slick.geom.Vector2f;
import player.Player;
import models.war_attenders.soldiers.Soldier;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

import java.util.ArrayList;

public class KeyInputHandler {
    private Player player;
    private ArrayList<WarAttender> level_war_attenders;

    public KeyInputHandler(Player player, ArrayList<WarAttender> level_war_attenders) {
        this.level_war_attenders = level_war_attenders;
        this.player = player;
    }

    public void update(GameContainer gameContainer, int deltaTime) {
        Input input = gameContainer.getInput();
        switch (player.getWarAttenderType()) {
            case SOLDIER:   // player is a soldier (goes by foot)
                Soldier soldier = (Soldier) player.getWarAttender();

                if (input.isKeyDown(Input.KEY_UP)) {
                    soldier.startAnimation();
                    soldier.accelerate(WarAttender.Move.MOVE_UP, deltaTime);
                } else {
                    soldier.stopAnimation();
                }

                if (input.isKeyDown(Input.KEY_LEFT)) {
                    soldier.rotate(WarAttender.RotateDirection.ROTATE_DIRECTION_LEFT, deltaTime);
                }

                if (input.isKeyDown(Input.KEY_RIGHT)) {
                    soldier.rotate(WarAttender.RotateDirection.ROTATE_DIRECTION_RIGHT, deltaTime);
                }

                if (input.isKeyPressed(Input.KEY_LSHIFT) || input.isKeyPressed(Input.KEY_RSHIFT)) {
                    for(WarAttender warAttender : level_war_attenders){
                        if(!warAttender.isHostile()){
                            if (warAttender.getCollisionModel().intersects(soldier.getCollisionModel())) {
                                warAttender.showAccessibleAnimation(false);
                                player.setWarAttender(warAttender);
                                level_war_attenders.remove(warAttender);
                                break;
                            }
                        }
                    }
                }
                break;
            case TANK:      // player is in a tank
                Tank tank = (Tank) player.getWarAttender();

                if (input.isKeyDown(Input.KEY_UP)) {
                    tank.accelerate(WarAttender.Move.MOVE_UP, deltaTime);   // accelerate tank until max_speed
                } else if (input.isKeyDown(Input.KEY_DOWN)) {

                } else if (tank.isMoving()) {
                    tank.decelerate(WarAttender.Move.MOVE_UP, deltaTime);   // decelerate as long as the tank is still moving
                }

                if (input.isKeyDown(Input.KEY_LEFT)) {
                    tank.rotate(WarAttender.RotateDirection.ROTATE_DIRECTION_LEFT, deltaTime);
                }

                if (input.isKeyDown(Input.KEY_RIGHT)) {
                    tank.rotate(WarAttender.RotateDirection.ROTATE_DIRECTION_RIGHT, deltaTime);
                }

                if (input.isKeyDown(Input.KEY_X)) {
                    tank.rotateTurret(WarAttender.RotateDirection.ROTATE_DIRECTION_RIGHT, deltaTime);
                }

                if (input.isKeyDown(Input.KEY_Y)) {
                    tank.rotateTurret(WarAttender.RotateDirection.ROTATE_DIRECTION_LEFT, deltaTime);
                }

                if (input.isKeyDown(Input.KEY_LCONTROL) || input.isKeyPressed(Input.KEY_RCONTROL)) {
                   tank.shoot();
                }

                if (input.isKeyPressed(Input.KEY_LSHIFT) || input.isKeyPressed(Input.KEY_RSHIFT)) {
                    if (!tank.isMoving()) {
                        Vector2f spawn_position = tank.calculateSoldierSpawnPosition();
                        soldier = new PlayerSoldier(spawn_position.x, spawn_position.y);
                        tank.showAccessibleAnimation(true);
                        level_war_attenders.add(tank);
                        player.setWarAttender(soldier);
                    }
                }
                break;
            case PLANE:     // player is in a plane

                break;

        }

    }
}
