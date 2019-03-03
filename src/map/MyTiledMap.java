package map;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

public class MyTiledMap extends TiledMap {
    private Vector2f pos;
    private int mapX, mapY;
    private final int RENDER_RIGHT = 20;
    private final int RENDER_DOWN = 15;

    public MyTiledMap(String ref, Vector2f start_pos) throws SlickException {
        super(ref);
        this.pos = start_pos;
    }

    public void move(Vector2f vec) {
        pos.add(vec);
    }

    public void render() {
        super.render((int) pos.x - tileHeight, (int) pos.y - tileHeight, mapX, mapY, mapX + RENDER_RIGHT, mapY + RENDER_DOWN);
    }

    public void update() {
        if((int) pos.x < 0){
            mapX++;
            pos.x = tileHeight;
        }
        if((int) pos.x > tileHeight){
            mapX--;
            pos.x = 0;
        }
        if((int) pos.y < 0){
            mapY++;
            pos.y = tileHeight;
        }
        if((int) pos.y > tileHeight){
            mapY--;
            pos.y = 0;
        }
    }
}
