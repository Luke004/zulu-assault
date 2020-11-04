package game.models.entities.tanks;

import game.models.weapons.Cannon;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class CannonTank extends Tank {

    private static Texture cannon_tank_hostile_texture, cannon_tank_friendly_texture,
            cannon_tank_hostile_turret_texture, cannon_tank_friendly_turret_texture;

    // attributes
    private static final float ARMOR = 50.f;
    private static final float ROTATE_SPEED_PLAYER = 0.15f, ROTATE_SPEED_BOT = 0.05f;
    private static final float MAX_SPEED_PLAYER = 0.23f, MAX_SPEED_BOT = 0.09f;
    private static final float ACCELERATION_FACTOR = 0.0005f, DECELERATION_FACTOR = 0.0005f;

    public CannonTank(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        weapons.add(new Cannon(isDrivable, 4));  // WEAPON_1

        // LOAD TEXTURES
        try {
            if (isHostile) {
                if (cannon_tank_hostile_texture == null) {
                    cannon_tank_hostile_texture = new Image("assets/entities/tanks/cannon_tank_hostile.png")
                            .getTexture();
                }
                base_image = new Image(cannon_tank_hostile_texture);
                if (cannon_tank_hostile_turret_texture == null) {
                    cannon_tank_hostile_turret_texture =
                            new Image("assets/entities/tanks/cannon_tank_hostile_turret.png")
                                    .getTexture();
                }
                turret = new Image(cannon_tank_hostile_turret_texture);
            } else {    // friendly
                if (cannon_tank_friendly_texture == null) {
                    cannon_tank_friendly_texture = new Image("assets/entities/tanks/cannon_tank_friendly.png")
                            .getTexture();
                }
                base_image = new Image(cannon_tank_friendly_texture);
                if (cannon_tank_friendly_turret_texture == null) {
                    cannon_tank_friendly_turret_texture =
                            new Image("assets/entities/tanks/cannon_tank_friendly_turret.png")
                                    .getTexture();
                }
                turret = new Image(cannon_tank_friendly_turret_texture);
            }
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
    public float getMaxSpeed() {
        return isDrivable ? MAX_SPEED_PLAYER : MAX_SPEED_BOT;
    }

    @Override
    protected float getAccelerationFactor() {
        return ACCELERATION_FACTOR;
    }

    @Override
    protected float getDecelerationFactor() {
        return DECELERATION_FACTOR;
    }

    @Override
    public void changeHealth(float amount) {
        super.changeHealth(amount, ARMOR);
    }
}
