package map;

import player.Player;
import models.war_attenders.soldiers.Soldier;
import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

import static player.Player.WarAttenderType.PLANE;
import static player.Player.WarAttenderType.TANK;

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
        switch (player.getWarAttenderType()) {
            case SOLDIER:   // player is a soldier (goes by foot)
                Soldier soldier = (Soldier) player.getWarAttender();
                Animation animation = soldier.getAnimation();
                float rotation = animation.getCurrentFrame().getRotation();
                soldier.getDir().x = (float) Math.sin(rotation * Math.PI / 180);
                soldier.getDir().y = (float) -Math.cos(rotation * Math.PI / 180);

                Input input = gameContainer.getInput();
                if (input.isKeyDown(Input.KEY_UP)) {
                    soldier.startAnimation();
                    float speed = soldier.getMovementSpeed();
                    Vector2f dir = soldier.getDir();
                    dir.x *= deltaTime * speed * -1;
                    dir.y *= deltaTime * speed * -1;
                    pos.x += dir.x;
                    pos.y += dir.y;
                } else {
                    soldier.stopAnimation();
                }

                if (input.isKeyDown(Input.KEY_LEFT)) {
                    for (int idx = 0; idx < animation.getFrameCount(); ++idx) {
                        animation.getImage(idx).rotate(-soldier.getRotateSpeed() * deltaTime);
                    }
                }

                if (input.isKeyDown(Input.KEY_RIGHT)) {
                    for (int idx = 0; idx < animation.getFrameCount(); ++idx) {
                        animation.getImage(idx).rotate(soldier.getRotateSpeed() * deltaTime);
                    }
                }
                break;
            case TANK:      // player is in a tank

                break;
            case PLANE:     // player is in a plane

                break;

        }

    }

    public void render() {
        super.render((int) pos.x, (int) pos.y, 0, 0, 20, 15);
    }
}
