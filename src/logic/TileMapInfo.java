package logic;

import org.newdawn.slick.tiled.TileSet;
import org.newdawn.slick.tiled.TiledMap;

import java.util.HashMap;
import java.util.Map;

public class TileMapInfo {

    // tile size info
    public static int TILE_WIDTH, TILE_HEIGHT, LEVEL_WIDTH_TILES, LEVEL_HEIGHT_TILES, LEVEL_WIDTH_PIXELS,
            LEVEL_HEIGHT_PIXELS;

    // tilesets
    public static final int ENEMY_TILES_TILESET_IDX = 0;
    public static final int LANDSCAPE_TILES_TILESET_IDX = 1;
    public static final int PLANE_TILES_TILESET_IDX = 2;

    // layers
    public static final int LANDSCAPE_TILES_LAYER_IDX = 0;
    public static final int ENEMY_TILES_LAYER_IDX = 1;

    public static int GRASS_IDX, CONCRETE_IDX, DIRT_IDX;

    public static int[] staticWarAttender_indices;

    public static final int[] windmill_indices;
    public static final int[] static_plane_creation_indices;
    public static final int[] static_plane_collision_indices;
    public static final int[] windmill_replace_indices;
    public static final int[] destructible_tile_indices;
    public static final int[] destructible_tile_replace_indices;
    public static final int[] indestructible_tile_indices;

    public static Map<Integer, Float> destructible_tiles_health_info;

    static {
        windmill_indices = new int[]{0, 1, 2};
        static_plane_creation_indices = new int[]{  // indices for creation of big static plane
                22, // the plane that's facing right in the tileset
                27, // the plane that's facing down in the tileset
                72, // the plane that's facing left in the tileset
                77  // the plane that's facing up in the tileset
        };
        static_plane_collision_indices = new int[]{ // indices for collision with big static plane
                12, 21, 22, 23, 32,  // the plane that's facing right in the tileset
                17, 26, 27, 28, 37,  // the plane that's facing down in the tileset
                62, 71, 72, 73, 82,  // the plane that's facing left in the tileset
                67, 76, 77, 78, 87   // the plane that's facing up in the tileset
        };
        windmill_replace_indices = new int[]{96, 97, 98, 99};
        destructible_tile_indices = new int[]{1, 2, 18, 19, 25, 65, 68, 83, 88, 89};
        destructible_tile_replace_indices = new int[]{32, 33, 34, 35, 36, 37, 95, 94, 93, 91};
        indestructible_tile_indices = new int[]{40, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 64, 66, 67,
                68, 72, 73, 74, 75, 76, 77};
    }

    public static void init(TiledMap map) {
        TILE_WIDTH = map.getTileWidth();
        TILE_HEIGHT = map.getTileHeight();
        LEVEL_WIDTH_TILES = map.getWidth();
        LEVEL_HEIGHT_TILES = map.getHeight();
        LEVEL_WIDTH_PIXELS = LEVEL_WIDTH_TILES * TILE_WIDTH;
        LEVEL_HEIGHT_PIXELS = LEVEL_HEIGHT_TILES * TILE_HEIGHT;

        // create TileInfo for 'landscape_tiles' TileSet
        TileSet landscape_tiles = map.getTileSet(LANDSCAPE_TILES_TILESET_IDX);
        if (!landscape_tiles.name.equals("landscape_tiles"))
            throw new IllegalAccessError(
                    "Wrong tileset index: [" + LANDSCAPE_TILES_TILESET_IDX + "] is not landscape_tiles");
        else {
            int idx;
            for (idx = 0; idx < destructible_tile_indices.length; ++idx) {
                destructible_tile_indices[idx] += landscape_tiles.firstGID;
                destructible_tile_replace_indices[idx] += landscape_tiles.firstGID;
            }
            for (idx = 0; idx < indestructible_tile_indices.length; ++idx) {
                indestructible_tile_indices[idx] += landscape_tiles.firstGID;
            }
            for (idx = 0; idx < windmill_replace_indices.length; ++idx) {
                windmill_replace_indices[idx] += landscape_tiles.firstGID;
            }
        }
        GRASS_IDX = landscape_tiles.firstGID;
        DIRT_IDX = 16 + landscape_tiles.firstGID;
        CONCRETE_IDX = 80 + landscape_tiles.firstGID;


        // create TileInfo for 'enemy_tiles' TileSet
        TileSet enemy_tiles = map.getTileSet(ENEMY_TILES_TILESET_IDX);
        if (!enemy_tiles.name.equals("enemy_tiles"))
            throw new IllegalAccessError("Wrong tileset index: [" + ENEMY_TILES_TILESET_IDX + "] is not enemy_tiles");
        else {
            for (int idx = 0; idx < windmill_indices.length; ++idx) {
                windmill_indices[idx] += enemy_tiles.firstGID;
            }
        }

        staticWarAttender_indices = new int[static_plane_collision_indices.length + windmill_indices.length];
        for (int i = 0; i < staticWarAttender_indices.length; ++i) {
            if (i < static_plane_collision_indices.length)
                staticWarAttender_indices[i] = static_plane_collision_indices[i];
            else staticWarAttender_indices[i] = windmill_indices[i - static_plane_collision_indices.length];
        }

        // create TileInfo for 'plane_tiles' TileSet
        TileSet plane_tiles = map.getTileSet(PLANE_TILES_TILESET_IDX);
        if (!plane_tiles.name.equals("plane_tiles"))
            throw new IllegalAccessError("Wrong tileset index: [" + PLANE_TILES_TILESET_IDX + "] is not plane_tiles");
        else {
            for (int idx = 0; idx < static_plane_creation_indices.length; ++idx) {
                static_plane_creation_indices[idx] += plane_tiles.firstGID;
            }
            for (int idx = 0; idx < static_plane_collision_indices.length; ++idx) {
                static_plane_collision_indices[idx] += plane_tiles.firstGID;
            }
        }

        destructible_tiles_health_info = new HashMap<>();
    }

    public static void reset() {
        if (destructible_tiles_health_info == null) destructible_tiles_health_info = new HashMap<>();
        else destructible_tiles_health_info.clear();
    }

}
