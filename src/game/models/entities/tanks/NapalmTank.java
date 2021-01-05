package game.models.entities.tanks;

import game.models.weapons.Napalm;
import game.models.weapons.Plasma;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class NapalmTank extends Tank {

    private static Texture napalm_tank_hostile_texture, napalm_tank_friendly_texture,
            napalm_tank_hostile_turret_texture, napalm_tank_friendly_turret_texture;

    // attributes
    private static final float ARMOR = 75.f;
    private static final float ROTATE_SPEED_PLAYER = 0.15f, ROTATE_SPEED_BOT = 0.05f;
    private static final float MAX_SPEED_PLAYER = 0.14f, MAX_SPEED_BOT = 0.05f;
    private static final float ACCELERATION_FACTOR = 0.00005f, DECELERATION_FACTOR = 0.0009f;

    public NapalmTank(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        weapons.add(new Plasma(isDrivable));  // WEAPON_1
        if (isDrivable) weapons.add(new Napalm(true));  // WEAPON_2

        // LOAD TEXTURES
        try {
            if (isHostile) {
                if (napalm_tank_hostile_texture == null) {
                    napalm_tank_hostile_texture = new Image("assets/entities/tanks/napalm_tank_hostile.png")
                            .getTexture();
                }
                base_image = new Image(napalm_tank_hostile_texture);
                if (napalm_tank_hostile_turret_texture == null) {
                    napalm_tank_hostile_turret_texture =
                            new Image("assets/entities/tanks/napalm_tank_hostile_turret.png")
                                    .getTexture();
                }
                turret = new Image(napalm_tank_hostile_turret_texture);
            } else {    // friendly
                if (napalm_tank_friendly_texture == null) {
                    napalm_tank_friendly_texture = new Image("assets/entities/tanks/napalm_tank_friendly.png")
                            .getTexture();
                }
                base_image = new Image(napalm_tank_friendly_texture);
                if (napalm_tank_friendly_turret_texture == null) {
                    napalm_tank_friendly_turret_texture =
                            new Image("assets/entities/tanks/napalm_tank_friendly_turret.png")
                                    .getTexture();
                }
                turret = new Image(napalm_tank_friendly_turret_texture);
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
