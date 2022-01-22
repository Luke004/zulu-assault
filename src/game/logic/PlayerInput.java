package game.logic;

import game.audio.MenuSounds;
import game.models.entities.Entity;
import game.models.weapons.Napalm;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import settings.UserSettings;
import game.graphics.hud.Radar;
import game.models.entities.MovableEntity;
import game.models.entities.aircraft.AttackHelicopter;
import game.models.entities.aircraft.Aircraft;
import game.models.entities.robots.Robot;
import game.models.entities.tanks.Tank;
import game.player.Player;
import game.models.entities.soldiers.Soldier;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

import static game.levels.Level.*;

public class PlayerInput {

    private boolean KEY_UP_RELEASED, KEY_DOWN_RELEASED, KEY_UP_PRESSED, KEY_DOWN_PRESSED;
    private static Sound turretRotateSound;

    static {
        try {
            turretRotateSound = new Sound("audio/sounds/rotate_turret.ogg");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public void update(GameContainer gameContainer, int deltaTime) {
        Input input = gameContainer.getInput();
        MovableEntity playerEntity = player.getEntity();

        // keys for all types:

        // left turn
        if (input.isKeyDown(Input.KEY_LEFT)) {
            playerEntity.rotate(MovableEntity.RotateDirection.ROTATE_DIRECTION_LEFT, deltaTime);
        }

        // right turn
        if (input.isKeyDown(Input.KEY_RIGHT)) {
            playerEntity.rotate(MovableEntity.RotateDirection.ROTATE_DIRECTION_RIGHT, deltaTime);
        }

        // fire weapon1
        if (input.isKeyDown(Input.KEY_RCONTROL)) {
            if (playerEntity.isDestroyed) return;
            playerEntity.fireWeapon(Entity.WeaponType.WEAPON_1);
        }

        // fire weapon1
        if (input.isKeyDown(Input.KEY_LCONTROL)) {
            // next if-statement fixes a bug from slick, where 'KEY_RALT' is also fired on 'KEY_LCONTROL' press
            if (!input.isKeyDown(Input.KEY_RALT)) {
                if (playerEntity.isDestroyed) return;
                playerEntity.fireWeapon(Entity.WeaponType.WEAPON_1);
            }
        }

        // fire weapon2
        if (input.isKeyDown(Input.KEY_LALT) || input.isKeyDown(Input.KEY_RALT)) {
            if (playerEntity.isDestroyed) return;
            playerEntity.fireWeapon(Entity.WeaponType.WEAPON_2);
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

        switch (player.getEntityType()) {
            case SOLDIER:   // player is a soldier (goes by foot)
                Soldier soldier = (Soldier) playerEntity;

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
                    soldier.fireWeapon(Entity.WeaponType.WEAPON_1);
                }

                // fire weapon2
                if (input.isKeyDown(Input.KEY_LALT) || input.isKeyPressed(Input.KEY_RALT)) {
                    soldier.fireWeapon(Entity.WeaponType.WEAPON_2);
                }

                // activate invincibility
                if (input.isKeyPressed(Input.KEY_1)) {
                    player.activateItem(Player.Item_e.INVINCIBILITY);
                }

                // get into vehicle
                if (input.isKeyPressed(Input.KEY_LSHIFT) || input.isKeyPressed(Input.KEY_RSHIFT)) {
                    for (MovableEntity movableEntity : drivable_entities) {
                        if (movableEntity.getCollisionModel().intersects(soldier.getCollisionModel())) {
                            movableEntity.setMoving(true);
                            movableEntity.showAccessibleAnimation(false);
                            player.setEntity(movableEntity, Player.EnterAction.ENTERING);
                            drivable_entities.remove(movableEntity);
                            all_entities.remove(movableEntity);
                            break;
                        }
                    }
                }
                break;
            case TANK:      // player is in a tank
                Tank tank = (Tank) playerEntity;

                // forward movement
                if (input.isKeyPressed(Input.KEY_UP)) {

                    if (KEY_DOWN_PRESSED) return;
                    KEY_UP_PRESSED = true;
                    if (tank.isMovingForward()) {
                        tank.cancelDeceleration();
                    }
                } else if (KEY_UP_RELEASED) {
                    tank.startDeceleration(MovableEntity.Direction.FORWARD);
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
                    tank.accelerate(deltaTime, MovableEntity.Direction.FORWARD);
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
                        tank.startDeceleration(MovableEntity.Direction.BACKWARDS);
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
                    tank.accelerate(deltaTime, MovableEntity.Direction.BACKWARDS);
                }

                if (input.isKeyDown(Input.KEY_X)) {
                    tank.rotateTurret(MovableEntity.RotateDirection.ROTATE_DIRECTION_RIGHT, deltaTime);
                    if (!turretRotateSound.playing()) {
                        turretRotateSound.play(1.f, UserSettings.soundVolume);
                    }
                }

                if (input.isKeyDown(UserSettings.keyboardLayout_1 ? Input.KEY_Z : Input.KEY_Y)) {
                    tank.rotateTurret(MovableEntity.RotateDirection.ROTATE_DIRECTION_LEFT, deltaTime);
                    if (!turretRotateSound.playing()) {
                        turretRotateSound.play(1.f, UserSettings.soundVolume);
                    }
                }


                // get out of tank
                if (input.isKeyPressed(Input.KEY_LSHIFT) || input.isKeyPressed(Input.KEY_RSHIFT)) {
                    if (!tank.isMoving()) {
                        tank.showAccessibleAnimation(true);
                        drivable_entities.add(tank);
                        all_entities.add(tank);
                        player.setEntity(tank, Player.EnterAction.LEAVING);
                    } else {
                        // tank is moving, can't get out of it
                        MenuSounds.ERROR_SOUND.play(1.f, UserSettings.soundVolume);
                    }
                }

                // auto center turret
                if (input.isKeyPressed(Input.KEY_A)) {
                    tank.autoCenterTurret();
                }
                // play rotate turret sound while centering it
                if (tank.isCenteringTurret) {
                    if (!turretRotateSound.playing()) {
                        turretRotateSound.play(1.f, UserSettings.soundVolume);
                    }
                }
                break;
            case ROBOT:   // player is in a robot
                Robot robot = (Robot) playerEntity;

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
                    robot.rotateTurret(MovableEntity.RotateDirection.ROTATE_DIRECTION_RIGHT, deltaTime);
                    if (!turretRotateSound.playing()) {
                        turretRotateSound.play(1.f, UserSettings.soundVolume);
                    }
                }

                if (input.isKeyDown(UserSettings.keyboardLayout_1 ? Input.KEY_Z : Input.KEY_Y)) {
                    robot.rotateTurret(MovableEntity.RotateDirection.ROTATE_DIRECTION_LEFT, deltaTime);
                    if (!turretRotateSound.playing()) {
                        turretRotateSound.play(1.f, UserSettings.soundVolume);
                    }
                }

                // get out of robot
                if (input.isKeyPressed(Input.KEY_LSHIFT) || input.isKeyPressed(Input.KEY_RSHIFT)) {
                    if (!robot.isMoving()) {
                        robot.showAccessibleAnimation(true);
                        drivable_entities.add(robot);
                        all_entities.add(robot);
                        player.setEntity(robot, Player.EnterAction.LEAVING);
                    } else {
                        // robot is moving, can't get out of it
                        MenuSounds.ERROR_SOUND.play(1.f, UserSettings.soundVolume);
                    }
                }

                // auto center turret
                if (input.isKeyPressed(Input.KEY_A)) {
                    robot.autoCenterTurret();
                    turretRotateSound.play(1.f, UserSettings.soundVolume);
                }
                // play rotate turret sound while centering it
                if (robot.isCenteringTurret) {
                    if (!turretRotateSound.playing()) {
                        turretRotateSound.play(1.f, UserSettings.soundVolume);
                    }
                }
                break;

            case PLANE:     // player is in a plane
                Aircraft aircraft = (Aircraft) playerEntity;

                if (input.isKeyDown(Input.KEY_UP)) {
                    aircraft.increaseSpeed(deltaTime);
                }

                if (input.isKeyDown(Input.KEY_DOWN)) {
                    aircraft.decreaseSpeed(deltaTime);
                }

                // get out of plane
                if (input.isKeyPressed(Input.KEY_LSHIFT) || input.isKeyPressed(Input.KEY_RSHIFT)) {
                    aircraft.initLanding();
                }
                if (aircraft.hasLanded()) {
                    aircraft.showAccessibleAnimation(true);
                    drivable_entities.add(aircraft);
                    all_entities.add(aircraft);
                    player.setEntity(aircraft, Player.EnterAction.LEAVING);
                }
                break;

            case HELICOPTER:     // player is in a helicopter
                AttackHelicopter attackHelicopter = (AttackHelicopter) playerEntity;

                // forward movement
                if (input.isKeyPressed(Input.KEY_UP)) {
                    if (KEY_DOWN_PRESSED) return;
                    KEY_UP_PRESSED = true;
                    if (attackHelicopter.isMovingForward()) {
                        attackHelicopter.cancelDeceleration();
                    }
                } else if (KEY_UP_RELEASED) {
                    attackHelicopter.startDeceleration(MovableEntity.Direction.FORWARD);
                    KEY_UP_RELEASED = false;
                    KEY_UP_PRESSED = false;
                }

                if (KEY_UP_PRESSED) {
                    if (KEY_DOWN_PRESSED) return;
                    if (attackHelicopter.isDecelerating()) {
                        return;
                    }
                    attackHelicopter.setMovingForward(true);
                    attackHelicopter.setMoving(true);
                    attackHelicopter.increaseSpeed(deltaTime);
                }

                // backwards movement
                if (input.isKeyPressed(Input.KEY_DOWN)) {
                    if (KEY_UP_PRESSED) return;
                    KEY_DOWN_PRESSED = true;
                    if (!attackHelicopter.isMovingForward()) {
                        attackHelicopter.cancelDeceleration();
                    }
                } else if (KEY_DOWN_RELEASED) {
                    if (!attackHelicopter.isMovingForward())
                        attackHelicopter.startDeceleration(MovableEntity.Direction.BACKWARDS);
                    KEY_DOWN_RELEASED = false;
                    KEY_DOWN_PRESSED = false;
                }

                if (KEY_DOWN_PRESSED) {
                    if (KEY_UP_PRESSED) return;
                    if (attackHelicopter.isDecelerating()) {
                        return;
                    }
                    attackHelicopter.setMovingForward(false);
                    attackHelicopter.setMoving(true);
                    attackHelicopter.decreaseSpeed(deltaTime);
                }

                // get out of helicopter
                if (input.isKeyPressed(Input.KEY_LSHIFT) || input.isKeyPressed(Input.KEY_RSHIFT)) {
                    attackHelicopter.initLanding();
                }
                if (attackHelicopter.hasLanded()) {
                    attackHelicopter.showAccessibleAnimation(true);
                    drivable_entities.add(attackHelicopter);
                    all_entities.add(attackHelicopter);
                    player.setEntity(attackHelicopter, Player.EnterAction.LEAVING);
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
            case Input.KEY_Y:
            case Input.KEY_Z:
            case Input.KEY_X:
                turretRotateSound.stop();
                break;
            case Input.KEY_RALT:
            case Input.KEY_LALT:
                // stop the napalm fire sound if players 2nd weapon is napalm
                // not the most beautiful solution, but it works ...
                if (player.getEntity().getWeapon(Entity.WeaponType.WEAPON_2) instanceof Napalm) {
                    Napalm.napalm_fire_sound.stop();
                }
                break;
        }
    }

}
