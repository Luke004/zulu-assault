package models.entities.static_multitile_constructions;

import models.StaticEntity;
import org.newdawn.slick.geom.Vector2f;

public class StaticEnemyPlane extends StaticEntity {

    private static final float ARMOR = 50.f;
    private static final int SCORE_VALUE = 2000;


    public StaticEnemyPlane(Vector2f startPos, boolean isHostile, Vector2f[] collision_tiles, Vector2f[] replacement_tiles) {
        super(startPos, isHostile, collision_tiles);
        this.replacement_tiles = replacement_tiles;

        health_bar_position.x = position.x - 27.5f;
        health_bar_position.y = position.y - 35.f;
    }

    @Override
    public void fireWeapon(WeaponType weapon) {

    }

    @Override
    public void changeAimingDirection(float degree, int deltaTime) {

    }

    @Override
    public void changeHealth(float amount) {
        super.changeHealth(amount, ARMOR);
    }

    @Override
    public int getScoreValue() {
        return SCORE_VALUE;
    }
}
