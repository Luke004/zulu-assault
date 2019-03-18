package logic;

import models.war_attenders.WarAttender;
import models.war_attenders.tanks.Tank;
import player.Player;
import models.war_attenders.soldiers.Soldier;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

import java.util.List;

public class KeyInputHandler {
    private Player player;
    private List<WarAttender> friendly_war_attenders;
    private boolean KEY_UP_RELEASED, KEY_DOWN_RELEASED;

    public KeyInputHandler(Player player, List<WarAttender> friendly_war_attenders) {
        this.friendly_war_attenders = friendly_war_attenders;
        this.player = player;
    }

    public void update(GameContainer gameContainer, int deltaTime) {
        Input input = gameContainer.getInput();
        switch (player.getWarAttenderType()) {
            case SOLDIER:   // player is a soldier (goes by foot)
                Soldier soldier = (Soldier) player.getWarAttender();

                // forward movement
                if (input.isKeyPressed(Input.KEY_UP)) {
                    soldier.startAnimation();
                    soldier.setMoving(true);
                } else if (KEY_UP_RELEASED) {
                    soldier.stopAnimation();
                    soldier.setMoving(false);
                    KEY_UP_RELEASED = false;
                }
                if (input.isKeyDown(Input.KEY_UP)) {
                    soldier.moveForward(deltaTime);
                }

                // backwards movement
                if (input.isKeyPressed(Input.KEY_DOWN)) {
                    soldier.startAnimation();
                    soldier.setMoving(true);
                } else if (KEY_DOWN_RELEASED) {
                    soldier.stopAnimation();
                    soldier.setMoving(false);
                    KEY_DOWN_RELEASED = false;
                }
                if (input.isKeyDown(Input.KEY_DOWN)) {
                    soldier.moveBackwards(deltaTime);
                }

                // left turn
                if (input.isKeyDown(Input.KEY_LEFT)) {
                    soldier.rotate(WarAttender.RotateDirection.ROTATE_DIRECTION_LEFT, deltaTime);
                }

                // right turn
                if (input.isKeyDown(Input.KEY_RIGHT)) {
                    soldier.rotate(WarAttender.RotateDirection.ROTATE_DIRECTION_RIGHT, deltaTime);
                }

                // shoot
                if (input.isKeyDown(Input.KEY_LCONTROL) || input.isKeyPressed(Input.KEY_RCONTROL)) {
                    soldier.shoot();
                }

                // activate invincibility
                if (input.isKeyPressed(Input.KEY_1)) {
                    player.activateItem(Player.Item.INVINCIBLE);
                }

                // get into WarAttender
                if (input.isKeyPressed(Input.KEY_LSHIFT) || input.isKeyPressed(Input.KEY_RSHIFT)) {
                    for (WarAttender warAttender : friendly_war_attenders) {
                        if (warAttender.getCollisionModel().intersects(soldier.getCollisionModel())) {
                            warAttender.setMoving(true);
                            warAttender.showAccessibleAnimation(false);
                            player.setWarAttender(warAttender, Player.EnterAction.ENTERING);
                            friendly_war_attenders.remove(warAttender);
                            break;
                        }
                    }
                }
                break;
            case TANK:      // player is in a tank
                Tank tank = (Tank) player.getWarAttender();

                // forward movement
                if (input.isKeyPressed(Input.KEY_UP)) {
                    tank.setMoving(true);
                    tank.setCurrentSpeed(WarAttender.Direction.FORWARD);
                    tank.cancelDeceleration();
                }
                if (input.isKeyDown(Input.KEY_UP)) {
                    tank.accelerate(deltaTime);   // accelerate tank until max_speed
                } else if (KEY_UP_RELEASED) {
                    tank.startDeceleration();
                    KEY_UP_RELEASED = false;
                }

                // backwards movement
                if (input.isKeyPressed(Input.KEY_DOWN)) {
                    tank.setMoving(true);
                    tank.setCurrentSpeed(WarAttender.Direction.BACKWARDS);
                } else if(KEY_DOWN_RELEASED){
                    tank.setMoving(false);
                    KEY_DOWN_RELEASED = false;
                }

                if (input.isKeyDown(Input.KEY_DOWN)) {
                    tank.moveBackwards(deltaTime);  // drive tank backwards with its backwards_speed
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

                if (input.isKeyPressed(Input.KEY_1)) {
                    player.activateItem(Player.Item.INVINCIBLE);
                }

                if (input.isKeyPressed(Input.KEY_2)) {
                    player.activateItem(Player.Item.EMP);
                }

                if (input.isKeyPressed(Input.KEY_3)) {
                    player.activateItem(Player.Item.MEGA_PULSE);
                }

                if (input.isKeyPressed(Input.KEY_4)) {
                    player.activateItem(Player.Item.EXPAND);
                }

                // get out of tank
                if (input.isKeyPressed(Input.KEY_LSHIFT) || input.isKeyPressed(Input.KEY_RSHIFT)) {
                    if (!tank.isMoving()) {
                        tank.showAccessibleAnimation(true);
                        friendly_war_attenders.add(tank);
                        player.setWarAttender(tank, Player.EnterAction.LEAVING);
                    }
                }
                break;
            case PLANE:     // player is in a plane

                break;

        }

    }

    public void onKeyRelease(int key) {
        switch (key) {
            case Input.KEY_UP:
                KEY_UP_RELEASED = true;
                break;
            case Input.KEY_DOWN:
                KEY_DOWN_RELEASED = true;
                break;
        }
    }
}
