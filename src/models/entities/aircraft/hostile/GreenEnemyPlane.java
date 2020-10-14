package models.entities.aircraft.hostile;

import models.weapons.AGM;
import models.weapons.Uzi;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class GreenEnemyPlane extends EnemyFlyingEntity {

    private static Texture green_enemy_plane_texture;

    private static final float ARMOR = 40.f;
    private static final int SCORE_VALUE = 500;
    private static final float ROTATE_SPEED_PLAYER = 0.15f, ROTATE_SPEED_BOT = 0.15f;
    private static final float MAX_SPEED_PLAYER = 0.25f, MAX_SPEED_BOT = 0.2f;

    public GreenEnemyPlane(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        weapons.add(new Uzi(isDrivable));  // WEAPON_1
        weapons.add(new AGM(isDrivable));  // WEAPON_2

        // LOAD TEXTURES
        try {
            if (green_enemy_plane_texture == null) {
                green_enemy_plane_texture = new Image("assets/entities/planes/green_enemy_plane.png")
                        .getTexture();
            }
            base_image = new Image(green_enemy_plane_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }

        super.init();
    }

    @Override
    protected float getBaseRotateSpeed() {
        return isDrivable ? ROTATE_SPEED_PLAYER : ROTATE_SPEED_BOT;
    }

    @Override
    protected float getMaxSpeed() {
        return isDrivable ? MAX_SPEED_PLAYER : MAX_SPEED_BOT;
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
