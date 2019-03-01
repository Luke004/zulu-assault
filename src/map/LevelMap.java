package map;

import models.war_attenders.WarAttender;
import models.war_attenders.soldiers.PlayerSoldier;
import models.war_attenders.tanks.Tank;
import player.Player;
import models.war_attenders.soldiers.Soldier;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

public class LevelMap extends TiledMap {
    private Vector2f pos;
    private Player player;

    public LevelMap(String ref, float start_xPos, float start_yPos, Player player) throws SlickException {
        this(ref, new Vector2f(start_xPos, start_yPos), player);
    }

    public LevelMap(String ref, Vector2f startPos, Player player) throws SlickException {
        super(ref);
        this.pos = startPos;
        this.player = player;
    }

    public void update(GameContainer gameContainer, int deltaTime) {
        float rotation;
        Input input = gameContainer.getInput();
        switch (player.getWarAttenderType()) {
            case SOLDIER:   // player is a soldier (goes by foot)
                Soldier soldier = (Soldier) player.getWarAttender();
                rotation = soldier.getRotation();
                soldier.getDir().x = (float) Math.sin(rotation * Math.PI / 180);
                soldier.getDir().y = (float) -Math.cos(rotation * Math.PI / 180);

                if (input.isKeyDown(Input.KEY_UP)) {
                    soldier.startAnimation();
                    float speed = soldier.getMovementSpeed();
                    Vector2f dir = soldier.getDir();
                    dir.x *= deltaTime * speed * -1;
                    dir.y *= deltaTime * speed * -1;
                    pos.add(dir);
                    player.setMapDir(dir);
                } else {
                    soldier.stopAnimation();
                }

                if (input.isKeyDown(Input.KEY_LEFT)) {
                    soldier.rotate(WarAttender.RotateDirection.ROTATE_DIRECTION_LEFT, deltaTime);
                }

                if (input.isKeyDown(Input.KEY_RIGHT)) {
                    soldier.rotate(WarAttender.RotateDirection.ROTATE_DIRECTION_RIGHT, deltaTime);
                }
                break;
            case TANK:      // player is in a tank
                Tank tank = (Tank) player.getWarAttender();
                rotation = tank.getRotation();
                tank.getDir().x = (float) Math.sin(rotation * Math.PI / 180);
                tank.getDir().y = (float) -Math.cos(rotation * Math.PI / 180);

                if (input.isKeyDown(Input.KEY_UP)) {
                    float speed = tank.getMovementSpeed();
                    Vector2f dir = tank.getDir();
                    dir.x *= deltaTime * speed * -1;
                    dir.y *= deltaTime * speed * -1;
                    pos.add(dir);
                    tank.updateCoordinates(dir);
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

    public void render() {
        super.render((int) pos.x, (int) pos.y, 0, 0, 20, 15);
    }
}
