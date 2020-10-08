package logic;

import audio.MenuSounds;
import settings.UserSettings;
import graphics.hud.Radar;
import models.war_attenders.MovableWarAttender;
import models.war_attenders.planes.Helicopter;
import models.war_attenders.planes.Plane;
import models.war_attenders.robots.Robot;
import models.war_attenders.tanks.Tank;
import player.Player;
import models.war_attenders.soldiers.Soldier;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

import static levels.AbstractLevel.all_movable_war_attenders;
import static levels.AbstractLevel.drivable_war_attenders;

public class KeyInputHandler {
    private Player player;
    private boolean KEY_UP_RELEASED, KEY_DOWN_RELEASED, KEY_UP_PRESSED, KEY_DOWN_PRESSED;

    public void update(GameContainer gameContainer, int deltaTime) {
        Input input = gameContainer.getInput();
        MovableWarAttender playerWarAttender = player.getWarAttender();

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
            if (playerWarAttender.isDestroyed) return;
            playerWarAttender.fireWeapon(MovableWarAttender.WeaponType.WEAPON_1);
        }

        // fire weapon2
        if (input.isKeyDown(Input.KEY_LALT) || input.isKeyPressed(Input.KEY_RALT)) {
            if (playerWarAttender.isDestroyed) return;
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

        // show / hide radar
        if (input.isKeyPressed(Input.KEY_R)) {
            Radar.toggleRadar();
        }

        switch (player.getWarAttenderType()) {
            case SOLDIER:   // player is a soldier (goes by foot)
                Soldier soldier = (Soldier) playerWarAttender;

                // forward movement
                if (input.isKeyPressed(Input.KEY_UP)) {
                    soldier.setMovingForward(true);
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
                    soldier.setMovingForward(false);
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
                            all_movable_war_attenders.remove(warAttender);
                            break;
                        }
                    }
                }
                break;
            case TANK:      // player is in a tank
                Tank tank = (Tank) playerWarAttender;

                // forward movement
                if (input.isKeyPressed(Input.KEY_UP)) {
                    if (KEY_DOWN_PRESSED) return;
                    KEY_UP_PRESSED = true;
                    if (tank.isMovingForward()) {
                        tank.cancelDeceleration();
                    }
                } else if (KEY_UP_RELEASED) {
                    tank.startDeceleration(MovableWarAttender.Direction.FORWARD);
                    KEY_UP_RELEASED = false;
                    KEY_UP_PRESSED = false;
                }

                if (KEY_UP_PRESSED) {
                    if (KEY_DOWN_PRESSED) return;
                    if (tank.isDecelerating()) {
                        return;
                    }
                    tank.setMovingForward(true);
                    tank.setMoving(true);
                    tank.accelerate(deltaTime, MovableWarAttender.Direction.FORWARD);
                }


                // backwards movement
                if (input.isKeyPressed(Input.KEY_DOWN)) {
                    if (KEY_UP_PRESSED) return;
                    KEY_DOWN_PRESSED = true;
                    if (!tank.isMovingForward()) {
                        tank.cancelDeceleration();
                    }
                } else if (KEY_DOWN_RELEASED) {
                    if (!tank.isMovingForward())
                        tank.startDeceleration(MovableWarAttender.Direction.BACKWARDS);
                    KEY_DOWN_RELEASED = false;
                    KEY_DOWN_PRESSED = false;
                }

                if (KEY_DOWN_PRESSED) {
                    if (KEY_UP_PRESSED) return;
                    if (tank.isDecelerating()) {
                        return;
                    }
                    tank.setMovingForward(false);
                    tank.setMoving(true);
                    tank.accelerate(deltaTime, MovableWarAttender.Direction.BACKWARDS);
                }

                if (input.isKeyDown(Input.KEY_X)) {
                    tank.rotateTurret(MovableWarAttender.RotateDirection.ROTATE_DIRECTION_RIGHT, deltaTime);
                }

                if (input.isKeyDown(UserSettings.keyboardLayout_1 ? Input.KEY_Y : Input.KEY_Z)) {
                    tank.rotateTurret(MovableWarAttender.RotateDirection.ROTATE_DIRECTION_LEFT, deltaTime);
                }


                // get out of tank
                if (input.isKeyPressed(Input.KEY_LSHIFT) || input.isKeyPressed(Input.KEY_RSHIFT)) {
                    if (!tank.isMoving()) {
                        tank.showAccessibleAnimation(true);
                        drivable_war_attenders.add(tank);
                        all_movable_war_attenders.add(tank);
                        player.setWarAttender(tank, Player.EnterAction.LEAVING);
                    } else {
                        // tank is moving, can't get out of it
                        MenuSounds.ERROR_SOUND.play(1.f, UserSettings.soundVolume);
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
                    robot.setMovingForward(true);
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
                    robot.setMovingForward(false);
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
                        all_movable_war_attenders.add(robot);
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

                if (input.isKeyDown(Input.KEY_UP)) {
                    plane.increaseSpeed(deltaTime);
                }

                if (input.isKeyDown(Input.KEY_DOWN)) {
                    plane.decreaseSpeed(deltaTime);
                }

                // get out of plane
                if (input.isKeyPressed(Input.KEY_LSHIFT) || input.isKeyPressed(Input.KEY_RSHIFT)) {
                    plane.initLanding();
                }
                if (plane.hasLanded()) {
                    plane.showAccessibleAnimation(true);
                    drivable_war_attenders.add(plane);
                    all_movable_war_attenders.add(plane);
                    player.setWarAttender(plane, Player.EnterAction.LEAVING);
                }
                break;

            case HELICOPTER:     // player is in a helicopter
                Helicopter helicopter = (Helicopter) playerWarAttender;

                // forward movement
                if (input.isKeyPressed(Input.KEY_UP)) {
                    if (KEY_DOWN_PRESSED) return;
                    KEY_UP_PRESSED = true;
                    if (helicopter.isMovingForward()) {
                        helicopter.cancelDeceleration();
                    }
                } else if (KEY_UP_RELEASED) {
                    helicopter.startDeceleration(MovableWarAttender.Direction.FORWARD);
                    KEY_UP_RELEASED = false;
                    KEY_UP_PRESSED = false;
                }

                if (KEY_UP_PRESSED) {
                    if (KEY_DOWN_PRESSED) return;
                    if (helicopter.isDecelerating()) {
                        return;
                    }
                    helicopter.setMovingForward(true);
                    helicopter.setMoving(true);
                    helicopter.increaseSpeed(deltaTime);
                }

                // backwards movement
                if (input.isKeyPressed(Input.KEY_DOWN)) {
                    if (KEY_UP_PRESSED) return;
                    KEY_DOWN_PRESSED = true;
                    if (!helicopter.isMovingForward()) {
                        helicopter.cancelDeceleration();
                    }
                } else if (KEY_DOWN_RELEASED) {
                    if (!helicopter.isMovingForward())
                        helicopter.startDeceleration(MovableWarAttender.Direction.BACKWARDS);
                    KEY_DOWN_RELEASED = false;
                    KEY_DOWN_PRESSED = false;
                }

                if (KEY_DOWN_PRESSED) {
                    if (KEY_UP_PRESSED) return;
                    if (helicopter.isDecelerating()) {
                        return;
                    }
                    helicopter.setMovingForward(false);
                    helicopter.setMoving(true);
                    helicopter.decreaseSpeed(deltaTime);
                }

                // get out of helicopter
                if (input.isKeyPressed(Input.KEY_LSHIFT) || input.isKeyPressed(Input.KEY_RSHIFT)) {
                    helicopter.initLanding();
                }
                if (helicopter.hasLanded()) {
                    helicopter.showAccessibleAnimation(true);
                    drivable_war_attenders.add(helicopter);
                    all_movable_war_attenders.add(helicopter);
                    player.setWarAttender(helicopter, Player.EnterAction.LEAVING);
                }
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

    public void setPlayer(Player player) {
        this.player = player;
    }
}
