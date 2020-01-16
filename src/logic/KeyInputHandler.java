package logic;

import levels.AbstractLevel;
import models.war_attenders.MovableWarAttender;
import models.war_attenders.planes.Plane;
import models.war_attenders.robots.Robot;
import models.war_attenders.tanks.Tank;
import player.Player;
import models.war_attenders.soldiers.Soldier;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

import java.util.List;

public class KeyInputHandler {
    private Player player;
    private List<MovableWarAttender> drivable_war_attenders;
    private boolean KEY_UP_RELEASED, KEY_DOWN_RELEASED;

    public void update(GameContainer gameContainer, int deltaTime) {
        Input input = gameContainer.getInput();
        MovableWarAttender playerWarAttender = player.getWarAttender();
        switch (player.getWarAttenderType()) {
            case SOLDIER:   // player is a soldier (goes by foot)
                Soldier soldier = (Soldier) playerWarAttender;

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

                // fire weapon1
                if (input.isKeyDown(Input.KEY_LCONTROL) || input.isKeyPressed(Input.KEY_RCONTROL)) {
                    soldier.fireWeapon(MovableWarAttender.WeaponType.WEAPON_1);
                }

                // fire weapon2
                if (input.isKeyDown(Input.KEY_LALT) || input.isKeyPressed(Input.KEY_RALT)) {
                    soldier.fireWeapon(MovableWarAttender.WeaponType.WEAPON_2);
                }

                // activate invincibility
                if (input.isKeyPressed(Input.KEY_1)) {
                    player.activateItem(Player.Item_e.INVINCIBILITY);
                }

                // get into MovableWarAttender
                if (input.isKeyPressed(Input.KEY_LSHIFT) || input.isKeyPressed(Input.KEY_RSHIFT)) {
                    for (MovableWarAttender warAttender : drivable_war_attenders) {
                        if (warAttender.getCollisionModel().intersects(soldier.getCollisionModel())) {
                            warAttender.setMoving(true);
                            warAttender.showAccessibleAnimation(false);
                            player.setWarAttender(warAttender, Player.EnterAction.ENTERING);
                            drivable_war_attenders.remove(warAttender);
                            break;
                        }
                    }
                }
                break;
            case TANK:      // player is in a tank
                Tank tank = (Tank) playerWarAttender;

                // forward movement
                if (input.isKeyPressed(Input.KEY_UP)) {
                    tank.setMoving(true);
                    tank.setCurrentSpeed(MovableWarAttender.Direction.FORWARD);
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
                    tank.setCurrentSpeed(MovableWarAttender.Direction.BACKWARDS);
                } else if (KEY_DOWN_RELEASED) {
                    tank.setMoving(false);
                    KEY_DOWN_RELEASED = false;
                }

                if (input.isKeyDown(Input.KEY_DOWN)) {
                    tank.moveBackwards(deltaTime);  // drive tank backwards with its backwards_speed
                }

                if (input.isKeyDown(Input.KEY_X)) {
                    tank.rotateTurret(MovableWarAttender.RotateDirection.ROTATE_DIRECTION_RIGHT, deltaTime);
                }

                if (input.isKeyDown(Input.KEY_Y)) {
                    tank.rotateTurret(MovableWarAttender.RotateDirection.ROTATE_DIRECTION_LEFT, deltaTime);
                }

                // get out of tank
                if (input.isKeyPressed(Input.KEY_LSHIFT) || input.isKeyPressed(Input.KEY_RSHIFT)) {
                    if (!tank.isMoving()) {
                        tank.showAccessibleAnimation(true);
                        drivable_war_attenders.add(tank);
                        player.setWarAttender(tank, Player.EnterAction.LEAVING);
                    }
                }

                // auto center turret
                if (input.isKeyPressed(Input.KEY_A)) {
                    tank.autoCenterTurret();
                }
                break;
            case ROBOT:   // player is in a robot
                Robot robot = (Robot) playerWarAttender;

                // forward movement
                if (input.isKeyPressed(Input.KEY_UP)) {
                    robot.startAnimation();
                    robot.setMoving(true);
                } else if (KEY_UP_RELEASED) {
                    robot.stopAnimation();
                    robot.setMoving(false);
                    KEY_UP_RELEASED = false;
                }
                if (input.isKeyDown(Input.KEY_UP)) {
                    robot.moveForward(deltaTime);
                }

                // backwards movement
                if (input.isKeyPressed(Input.KEY_DOWN)) {
                    robot.startAnimation();
                    robot.setMoving(true);
                } else if (KEY_DOWN_RELEASED) {
                    robot.stopAnimation();
                    robot.setMoving(false);
                    KEY_DOWN_RELEASED = false;
                }
                if (input.isKeyDown(Input.KEY_DOWN)) {
                    robot.moveBackwards(deltaTime);
                }

                if (input.isKeyDown(Input.KEY_X)) {
                    robot.rotateTurret(MovableWarAttender.RotateDirection.ROTATE_DIRECTION_RIGHT, deltaTime);
                }

                if (input.isKeyDown(Input.KEY_Y)) {
                    robot.rotateTurret(MovableWarAttender.RotateDirection.ROTATE_DIRECTION_LEFT, deltaTime);
                }

                // get out of robot
                if (input.isKeyPressed(Input.KEY_LSHIFT) || input.isKeyPressed(Input.KEY_RSHIFT)) {
                    if (!robot.isMoving()) {
                        robot.showAccessibleAnimation(true);
                        drivable_war_attenders.add(robot);
                        player.setWarAttender(robot, Player.EnterAction.LEAVING);
                    }
                }

                // auto center turret
                if (input.isKeyPressed(Input.KEY_A)) {
                    robot.autoCenterTurret();
                }

                break;

            case PLANE:     // player is in a plane
                Plane plane = (Plane) playerWarAttender;

                // get out of plane
                if (input.isKeyPressed(Input.KEY_LSHIFT) || input.isKeyPressed(Input.KEY_RSHIFT)) {
                    plane.land();
                }
                if (plane.hasLanded()) {
                    plane.showAccessibleAnimation(true);
                    drivable_war_attenders.add(plane);
                    player.setWarAttender(plane, Player.EnterAction.LEAVING);
                }

                break;
        }
        // keys for all types:

        // left turn
        if (input.isKeyDown(Input.KEY_LEFT)) {
            playerWarAttender.rotate(MovableWarAttender.RotateDirection.ROTATE_DIRECTION_LEFT, deltaTime);
        }

        // right turn
        if (input.isKeyDown(Input.KEY_RIGHT)) {
            playerWarAttender.rotate(MovableWarAttender.RotateDirection.ROTATE_DIRECTION_RIGHT, deltaTime);
        }

        // fire weapon1
        if (input.isKeyDown(Input.KEY_LCONTROL) || input.isKeyPressed(Input.KEY_RCONTROL)) {
            playerWarAttender.fireWeapon(MovableWarAttender.WeaponType.WEAPON_1);
        }

        // fire weapon2
        if (input.isKeyDown(Input.KEY_LALT) || input.isKeyPressed(Input.KEY_RALT)) {
            playerWarAttender.fireWeapon(MovableWarAttender.WeaponType.WEAPON_2);
        }

        // activate items
        if (input.isKeyPressed(Input.KEY_1)) {
            player.activateItem(Player.Item_e.INVINCIBILITY);
        }

        if (input.isKeyPressed(Input.KEY_2)) {
            player.activateItem(Player.Item_e.EMP);
        }

        if (input.isKeyPressed(Input.KEY_3)) {
            player.activateItem(Player.Item_e.MEGA_PULSE);
        }

        if (input.isKeyPressed(Input.KEY_4)) {
            player.activateItem(Player.Item_e.EXPAND);
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

    public void setLevel(AbstractLevel level) {
        this.player = level.player;
        this.drivable_war_attenders = level.drivable_war_attenders;
    }
}
