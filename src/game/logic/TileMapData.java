package game.logic;

import org.newdawn.slick.tiled.TileSet;
import org.newdawn.slick.tiled.TiledMap;

import java.util.HashMap;
import java.util.Map;

public class TileMapData {

    // the map itself
    public static TiledMap map;

    // tile size info
    public static int TILE_WIDTH, TILE_HEIGHT, LEVEL_WIDTH_TILES, LEVEL_HEIGHT_TILES, LEVEL_WIDTH_PIXELS,
            LEVEL_HEIGHT_PIXELS;

    // tilesets
    public static final int LANDSCAPE_TILES_TILESET_IDX = 0;
    public static final String TILESETS_LOCATION = "assets/tilesets";

    // layers
    public static final int LANDSCAPE_TILES_LAYER_IDX = 0;
    public static final int DESTRUCTION_TILES_LAYER_IDX = 1;

    public static int[] CLASS_GRASS, CLASS_CONCRETE, CLASS_DIRT, CLASS_SNOW, CLASS_SWAMP;

    public static final int[] collision_replace_indices;
    public static final int[] destructible_tile_indices;
    public static final int[] destructible_tile_replace_indices;
    public static final int[] indestructible_tile_indices;

    public static Map<Integer, Float> destructible_tiles_health_info;

    static {
        collision_replace_indices = new int[]{
                144,    // DIRT
                145,    // CONCRETE
                146,    // GRASS
                147,    // SNOW
                216,    // SWAMP
        };
        destructible_tile_indices = new int[]{1, 2, 26, 27, 37, 97, 100, 123, 132, 133, 157, 158, 159, 160, 161, 205, 206, 207};
        destructible_tile_replace_indices = new int[]{48, 49, 50, 51, 52, 53, 139, 138, 137, 135, 169, 170, 171, 172, 173, 217, 218, 219};
        indestructible_tile_indices = new int[]{60, 72, 73, 74, 75, 76, 77, 78, 79, 84, 85, 86, 87, 88, 89, 96, 98, 99,
                101, 108, 109, 110, 111, 112, 113, 180, 181, 182, 183, 184, 185, 187, 188, 189, 192, 193, 194, 195, 196, 197, 200, 201};
    }

    public static void init() {
        TILE_WIDTH = map.getTileWidth();
        TILE_HEIGHT = map.getTileHeight();

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
            for (idx = 0; idx < collision_replace_indices.length; ++idx) {
                collision_replace_indices[idx] += landscape_tiles.firstGID;
            }
        }

        // init tiles of grass for tile replacement
        int[] grass_indices = new int[]{
                0, 3, 4, 5, 6, 12, 28, 29, 30, 31
        };
        CLASS_GRASS = new int[grass_indices.length];
        for (int i = 0; i < grass_indices.length; ++i) {
            CLASS_GRASS[i] = landscape_tiles.firstGID + grass_indices[i];
        }
        // init tiles of concrete for tile replacement
        int[] concrete_indices = new int[]{
                62, 63, 64, 65, 66, 67, 90, 91, 102, 103, 114, 115, 120, 121, 122, 124, 125, 126, 127, 134, 151
        };
        CLASS_CONCRETE = new int[concrete_indices.length];
        for (int i = 0; i < concrete_indices.length; ++i) {
            CLASS_CONCRETE[i] = landscape_tiles.firstGID + concrete_indices[i];
        }
        // init tiles of dirt for tile replacement
        int[] dirt_indices = new int[]{
                13, 14, 15, 16, 17, 18, 19, 24, 25, 39, 40, 41, 42
        };
        CLASS_DIRT = new int[dirt_indices.length];
        for (int i = 0; i < dirt_indices.length; ++i) {
            CLASS_DIRT[i] = landscape_tiles.firstGID + dirt_indices[i];
        }
        // init tiles of snow for tile replacement
        int[] snow_indices = new int[]{
                156, 162, 163, 164, 165, 166, 167, 190, 191, 202, 203, 211, 212, 213, 214, 215
        };
        CLASS_SNOW = new int[snow_indices.length];
        for (int i = 0; i < snow_indices.length; ++i) {
            CLASS_SNOW[i] = landscape_tiles.firstGID + snow_indices[i];
        }
        // init tiles of snow for tile replacement
        int[] swamp_indices = new int[]{
                176, 177, 178, 179, 204, 208
        };
        CLASS_SWAMP = new int[swamp_indices.length];
        for (int i = 0; i < swamp_indices.length; ++i) {
            CLASS_SWAMP[i] = landscape_tiles.firstGID + swamp_indices[i];
        }

        destructible_tiles_health_info = new HashMap<>();
    }

    /* this is called every time a new map is loaded - maps can have different sizes! */
    public static boolean updateMapSize() {
        int prevWidth = LEVEL_WIDTH_TILES, prevHeight = LEVEL_HEIGHT_TILES;
        LEVEL_WIDTH_TILES = map.getWidth();
        LEVEL_HEIGHT_TILES = map.getHeight();
        LEVEL_WIDTH_PIXELS = LEVEL_WIDTH_TILES * TILE_WIDTH;
        LEVEL_HEIGHT_PIXELS = LEVEL_HEIGHT_TILES * TILE_HEIGHT;
        if (prevHeight == 0 && prevWidth == 0) return true;    // first call
        return prevHeight != LEVEL_HEIGHT_TILES || prevWidth != LEVEL_WIDTH_TILES;
    }

    public static void reset() {
        if (destructible_tiles_health_info == null) destructible_tiles_health_info = new HashMap<>();
        else destructible_tiles_health_info.clear();
    }

    public static void setMap(TiledMap myMap) {
        map = myMap;
    }

    public static int getReplacementTileID(int tileID) {
        for (int value : CLASS_DIRT) {
            if (tileID == value) return collision_replace_indices[0];
        }
        for (int value : CLASS_CONCRETE) {
            if (tileID == value) return collision_replace_indices[1];
        }
        for (int value : CLASS_GRASS) {
            if (tileID == value) return collision_replace_indices[2];
        }
        for (int value : CLASS_SNOW) {
            if (tileID == value) return collision_replace_indices[3];
        }
        for (int value : CLASS_SWAMP) {
            if (tileID == value) return collision_replace_indices[4];
        }
        return -1;
    }

    public static boolean isCollisionTile(int tileID) {
        for (int value : indestructible_tile_indices) {
            if (tileID == value) return true;
        }
        for (int value : destructible_tile_indices) {
            if (tileID == value) return true;
        }
        return false;
    }

}
