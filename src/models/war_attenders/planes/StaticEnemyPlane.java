package models.war_attenders.planes;

import models.StaticWarAttender;
import org.newdawn.slick.geom.Vector2f;

public class StaticEnemyPlane extends StaticWarAttender {

    private Vector2f[] tile_positions;

    public StaticEnemyPlane(Vector2f startPos, boolean isHostile, Vector2f[] tile_positions) {
        super(startPos, isHostile);
        this.tile_positions = tile_positions;

        max_health = 100.f;
        armor = 10.f;
        current_health = max_health;
        scoreValue = 200;

        health_bar_position.x = position.x - 27.5f;
        health_bar_position.y = position.y - 35.f;
    }

    @Override
    public void fireWeapon(WeaponType weapon) {

    }

    @Override
    public void setRotation(float degree) {

    }

    @Override
    public boolean containsTilePosition(int xPos, int yPos) {
        for (int i = 0; i < tile_positions.length; ++i) {
            if (tile_positions[i].x == xPos && tile_positions[i].y == yPos) return true;
        }
        return false;
    }

    @Override
    public Vector2f getTilePosition() {
        return tile_positions[0];
    }
}
