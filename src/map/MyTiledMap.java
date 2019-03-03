package map;

import models.war_attenders.WarAttender;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

public class MyTiledMap extends TiledMap {
    private WarAttender warAttender;
    private Vector2f pos;
    private int mapX, mapY;
    private boolean left_end_reached, right_end_reached, bottom_reached, top_reached;
    private final int RENDER_RIGHT = 22;
    private final int RENDER_DOWN = 17;

    public MyTiledMap(String ref, Vector2f start_pos, WarAttender warAttender) throws SlickException {
        super(ref);
        this.warAttender = warAttender;
        this.pos = start_pos;
    }

    public void move(Vector2f vec) {
        if (mapX < 0) {
            left_end_reached = true;
        } else if (mapY < 0) {
            top_reached = true;
        } else if (mapY > height - 16) {
            bottom_reached = true;
        } else if (mapX > width - 21) {
            right_end_reached = true;
        } else {
            pos.add(vec);
        }
    }

    public void render() {
        super.render((int) pos.x - tileHeight, (int) pos.y - tileHeight, mapX, mapY, mapX + RENDER_RIGHT, mapY + RENDER_DOWN);
    }

    public void update(GameContainer gameContainer, int deltaTime) {
        Input input = gameContainer.getInput();
        System.out.println(mapY);
        if ((int) pos.x < 0) {
            mapX++;
            pos.x = tileHeight;
        }
        if ((int) pos.x > tileHeight) {
            mapX--;
            pos.x = 0;
        }
        if ((int) pos.y < 0) {
            mapY++;
            pos.y = tileHeight;
        }
        if ((int) pos.y > tileHeight) {
            mapY--;
            pos.y = 0;
        }

        if(left_end_reached){
            if (input.isKeyDown(Input.KEY_UP)) {

            }
        }
    }
}
