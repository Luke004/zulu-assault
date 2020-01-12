package models;

import models.war_attenders.WarAttender;
import org.newdawn.slick.geom.Vector2f;

/*
StaticWarAttender are created from the tile map and each object has an individual key for identification
 */
public abstract class StaticWarAttender extends WarAttender {

    protected Vector2f[] collision_tiles;
    protected Vector2f[] replacement_tiles;

    public StaticWarAttender(Vector2f startPos, boolean isHostile, Vector2f[] collision_tiles) {
        super(startPos, isHostile);
        this.collision_tiles = collision_tiles;
    }

    public Vector2f[] getCollisionTiles() {
        return collision_tiles;
    }

    public Vector2f[] getReplacementTiles() {
        return replacement_tiles;
    }

    public boolean containsTilePosition(int xPos, int yPos) {
        for (Vector2f collision_tile : collision_tiles) {
            if (collision_tile.x == xPos && collision_tile.y == yPos) return true;
        }
        return false;
    }
}
