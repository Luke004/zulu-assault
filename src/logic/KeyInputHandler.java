package logic;

import models.war_attenders.WarAttender;
import models.war_attenders.soldiers.PlayerSoldier;
import models.war_attenders.tanks.Tank;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;
import player.Player;
import models.war_attenders.soldiers.Soldier;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

public class KeyInputHandler {
    private Player player;

    public KeyInputHandler(Player player, TiledMap map) {
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
                    for (WarAttender old_warAttender : player.getOldWarAttenders()) {
                        if(old_warAttender.getCollisionModel().intersects(soldier.getCollisionModel())){
                            player.setWarAttender(old_warAttender);
                            break;
                            // don't forget the break; here
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

                if (input.isKeyPressed(Input.KEY_LSHIFT) || input.isKeyPressed(Input.KEY_RSHIFT)) {
                    if (!tank.isMoving()) {
                        Vector2f spawn_position = tank.calculateSoldierSpawnPosition();
                        soldier = new PlayerSoldier(spawn_position.x, spawn_position.y);
                        player.setWarAttender(soldier, tank);
                    }
                }
                break;
            case PLANE:     // player is in a plane

                break;

        }

    }

    private float calculateDistance(Shape obj1, Shape obj2) {
        float horizontal_distance = obj1.getCenterX() - obj2.getCenterX();
        float vertical_distance = obj1.getCenterY() - obj2.getCenterY();
        float sum = (float) (Math.pow(horizontal_distance, 2) + Math.pow(vertical_distance, 2));
        return (float) Math.sqrt(sum);
    }
}
