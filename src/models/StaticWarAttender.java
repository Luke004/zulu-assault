package models;

import models.war_attenders.WarAttender;
import org.newdawn.slick.geom.Vector2f;

/*
StaticWarAttender are created from the tile map and each object has an individual key for identification
 */
public abstract class StaticWarAttender extends WarAttender {

    public StaticWarAttender(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);
    }

    abstract public boolean containsTilePosition(int xPos, int yPos);

    abstract public Vector2f getTilePosition();

}
