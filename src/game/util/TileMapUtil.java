package game.util;


import game.logic.TileMapData;

import java.util.ArrayList;
import java.util.List;

import static game.logic.TileMapData.*;

public class TileMapUtil {

    // various tile specs
    public static final float TILE_HEALTH = 100.f;
    public static final float DESTRUCTIBLE_TILE_NORMAL_ARMOR = 5.f;
    public static final float DESTRUCTIBLE_TILE_LOW_ARMOR = 1.f;

    private static final float CHANCE_FOR_TILE_DESTRUCTION = 0.1f;  // 10% chance for another tile to be destroyed

    public static void doCollateralTileDamage(int x, int y) {
        // maybe destroy nearby tiles
        List<Tile> tiles = new ArrayList<>();
        if (y > 0) {
            // top tile
            tiles.add(new Tile(x, y - 1, map.getTileId(x, y - 1, LANDSCAPE_TILES_LAYER_IDX)));
            if (x > 0) {
                // top left tile
                tiles.add(new Tile(x - 1, y - 1, map.getTileId(x - 1, y - 1, LANDSCAPE_TILES_LAYER_IDX)));
            }
            if (x < map.getWidth() - 1) {
                // top right tile
                tiles.add(new Tile(x + 1, y - 1, map.getTileId(x + 1, y - 1, LANDSCAPE_TILES_LAYER_IDX)));
            }
        }

        if (y < map.getHeight() - 1) {
            // bottom tile
            tiles.add(new Tile(x, y + 1, map.getTileId(x, y + 1, LANDSCAPE_TILES_LAYER_IDX)));
            if (x > 0) {
                // bottom left tile
                tiles.add(new Tile(x - 1, y + 1, map.getTileId(x - 1, y + 1, LANDSCAPE_TILES_LAYER_IDX)));
            }
            if (x < map.getWidth() - 1) {
                // bottom right tile
                tiles.add(new Tile(x + 1, y + 1, map.getTileId(x + 1, y + 1, LANDSCAPE_TILES_LAYER_IDX)));
            }
        }

        if (x > 0) {
            // left tile
            tiles.add(new Tile(x - 1, y, map.getTileId(x - 1, y, LANDSCAPE_TILES_LAYER_IDX)));
        }

        if (x < map.getWidth() - 1) {
            // right tile
            tiles.add(new Tile(x + 1, y, map.getTileId(x + 1, y, LANDSCAPE_TILES_LAYER_IDX)));
        }

        for (Tile tile : tiles) {
            double d = Math.random();
            boolean isDestructibleTile = false;
            for (int idx2 = 0; idx2 < destructible_tile_indices.length; ++idx2) {
                if (tile.tileID == destructible_tile_indices[idx2]) {
                    isDestructibleTile = true;
                    if (d < CHANCE_FOR_TILE_DESTRUCTION) {  // look for chance for it to be destroyed
                        map.setTileId(tile.xVal, tile.yVal, LANDSCAPE_TILES_LAYER_IDX, destructible_tile_replace_indices[idx2]);
                        destructible_tiles_health_info.remove(tile.key);
                    }
                    break;
                }
            }
            if (!isDestructibleTile) {
                if (d < CHANCE_FOR_TILE_DESTRUCTION) {
                    int replacement_tile_id = TileMapData.getReplacementTileID(tile.tileID);
                    if (replacement_tile_id != -1)
                        map.setTileId(tile.xVal, tile.yVal, DESTRUCTION_TILES_LAYER_IDX, replacement_tile_id);
                }
            }
        }
    }

    private static class Tile {
        int tileID, xVal, yVal, key;

        Tile(int xVal, int yVal, int tileID) {
            this.tileID = tileID;
            this.xVal = xVal;
            this.yVal = yVal;
            this.key = generateKey(xVal, yVal);
        }
    }

    public static int generateKey(int xPos, int yPos) {
        return xPos > yPos ? -xPos * yPos : xPos * yPos;
    }

}
