package map;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

public class MyTiledMap extends TiledMap {
    private Vector2f pos;

    public MyTiledMap(String ref, Vector2f start_pos) throws SlickException {
        super(ref);
        this.pos = start_pos;
    }

    public void move(Vector2f vec) {
        pos.add(vec);
    }

    public void render(){
        super.render((int) pos.x, (int) pos.y, 0, 0, 20, 15);
    }
}
