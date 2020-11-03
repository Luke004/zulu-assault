package game.models.entities.tanks;

import game.models.weapons.MegaPulse;
import game.models.weapons.Plasma;
import game.models.weapons.Weapon;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

/* IMPORTANT: This tank should only be used by bots. It was not developed for players! */

public class MegaPulseTank extends Tank {

    private static Texture mega_pulse_tank_hostile_texture, mega_pulse_tank_hostile_turret_texture;

    // attributes
    private static final float ARMOR = 40.f;
    private static final float ROTATE_SPEED_PLAYER = 0.2f, ROTATE_SPEED_BOT = 0.05f;
    private static final float MAX_SPEED_PLAYER = 0.18f, MAX_SPEED_BOT = 0.05f;
    private static final float ACCELERATION_FACTOR = 0.0003f, DECELERATION_FACTOR = 0.0009f;

    public MegaPulseTank(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        weapons.add(new Plasma(isDrivable));        // WEAPON_1
        weapons.add(new MegaPulse(isDrivable));     // WEAPON_2

        // LOAD TEXTURES
        try {
            if (mega_pulse_tank_hostile_texture == null) {
                mega_pulse_tank_hostile_texture = new Image("assets/entities/tanks/mega_pulse_tank.png")
                        .getTexture();
            }
            base_image = new Image(mega_pulse_tank_hostile_texture);
            if (mega_pulse_tank_hostile_turret_texture == null) {
                mega_pulse_tank_hostile_turret_texture =
                        new Image("assets/entities/tanks/mega_pulse_tank_turret.png")
                                .getTexture();
            }
            turret = new Image(mega_pulse_tank_hostile_turret_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }
        super.init();
    }

    /* special fireWeapon case which should only be used by bots */
    @Override
    public void fireWeapon(WeaponType weaponType) {
        Weapon weapon;
        if (weaponType == WeaponType.WEAPON_2) {
            weapon = weapons.get(1);    // mega pulse
        } else {
            weapon = weapons.get(0);    // plasma
        }
        weapon.fire(position.x, position.y, turret.getRotation());
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
