package models.entities.aircraft.hostile;

import models.weapons.AGM;
import models.weapons.Uzi;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class WhiteEnemyJet extends EnemyFlyingEntity {

    private static Texture white_enemy_jet_texture;

    private static final float ARMOR = 50.f;
    private static final float ROTATE_SPEED_PLAYER = 0.15f, ROTATE_SPEED_BOT = 0.15f;
    private static final float MAX_SPEED_PLAYER = 0.15f, MAX_SPEED_BOT = 0.15f;

    public WhiteEnemyJet(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        current_speed = getMaxSpeed();  // speed is always the same for this plane

        weapons.add(new Uzi(isDrivable));  // WEAPON_1
        weapons.add(new AGM(isDrivable));  // WEAPON_2

        // LOAD TEXTURES
        try {
            if (white_enemy_jet_texture == null) {
                white_enemy_jet_texture = new Image("assets/entities/planes/jet_enemy.png")
                        .getTexture();
            }
            base_image = new Image(white_enemy_jet_texture);
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

}
