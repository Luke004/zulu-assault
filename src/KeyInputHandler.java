package map;

import models.war_attenders.WarAttender;
import models.war_attenders.soldiers.PlayerSoldier;
import models.war_attenders.tanks.Tank;
import org.newdawn.slick.geom.Shape;
import player.Player;
import models.war_attenders.soldiers.Soldier;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;

public class KeyInputHandler {
    private Player player;
    private MyTiledMap map;

    public KeyInputHandler(Player player, MyTiledMap map) {
        this.player = player;
        this.map = map;
    }

    public void update(GameContainer gameContainer, int deltaTime) {
        Input input = gameContainer.getInput();
        switch (player.getWarAttenderType()) {
            case SOLDIER:   // player is a soldier (goes by foot)
                Soldier soldier = (Soldier) player.getWarAttender();

                if (input.isKeyDown(Input.KEY_UP)) {
                    soldier.startAnimation();
                    soldier.move(WarAttender.Move.MOVE_UP, deltaTime);
                    Vector2f direction = soldier.getDir();
                    map.move(direction);    // move the map in the direction the soldier is moving
                    //pos.add(direction);
                    soldier.updateCoordinates(direction);

                    // this for loop is needed im case user presses shift and gets out of plane/ tank
                    for (WarAttender old_warAttender : player.getOldWarAttenders()) {
                        old_warAttender.freezePosition(direction);  // old_warAttender stays in the same place
                    }
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
                        if (calculateDistance(player.getWarAttender().getCollisionModel(), old_warAttender.getCollisionModel()) < 30.f) {
                            player.setWarAttender(old_warAttender);
                            break;
                        }
                    }
                }
                break;
            case TANK:      // player is in a tank
                Tank tank = (Tank) player.getWarAttender();

                if (input.isKeyDown(Input.KEY_UP)) {
                    tank.move(WarAttender.Move.MOVE_UP, deltaTime);
                    Vector2f direction = tank.getDir();
                    map.move(direction);    // move the map in the direction the tank is moving
                    tank.updateCoordinates(direction);
                } else if(input.isKeyDown(Input.KEY_DOWN)){

                } else {
                    Vector2f direction = tank.getDir();
                    direction.x *= tank.deceleration_factor;
                    direction.y *= tank.deceleration_factor;
                    map.move(direction);    // move the map in the direction the tank is moving
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
                    soldier = new PlayerSoldier(gameContainer.getWidth() / 2,
                            gameContainer.getHeight() / 2);
                    player.setWarAttender(soldier, tank);
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
