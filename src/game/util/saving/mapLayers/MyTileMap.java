package game.util.saving.mapLayers;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.Layer;
import org.newdawn.slick.tiled.TiledMap;

import java.util.ArrayList;

/**
 * Extension of TiledMap with save and load feature.
 * This works by saving and loading the map layers.
 */
public class MyTileMap extends TiledMap {

    public MyTileMap(String ref, ArrayList<Layer> layers) throws SlickException {
        super(ref);
        // set existing map layers
        this.layers = layers;
    }

    public MyTileMap(String ref) throws SlickException {
        super(ref);
    }

    /**
     * Get all layers of the TileMap in order to save them on hard drive.
     * @return all map layers
     */
    public ArrayList<Layer> getMapLayers() {
        return this.layers;
    }

}
