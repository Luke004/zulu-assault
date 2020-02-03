package models.war_attenders.tanks;

import models.CollisionModel;
import models.weapons.Napalm;
import models.weapons.Plasma;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class NapalmTank extends Tank {

    private static Texture napalm_tank_hostile_texture, napalm_tank_friendly_texture,
            napalm_tank_hostile_turret_texture, napalm_tank_friendly_turret_texture;

    private static final float ARMOR = 75.f;


    public NapalmTank(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        // attributes equal for humans and bots


        if (isDrivable) {
            // individual NapalmTank attributes for human players
            max_speed = 0.1f;
            backwards_speed = max_speed / 2;
            acceleration_factor = 0.00005f;
            deceleration_factor = 0.0009f;
            rotate_speed = 0.15f;
        } else {
            // individual NapalmTank attributes for bots
            max_speed = 0.065f;
            backwards_speed = max_speed / 2;
            acceleration_factor = 0.00005f;
            deceleration_factor = 0.0009f;
            rotate_speed = 0.05f;
        }

        weapons.add(new Plasma(isDrivable));  // WEAPON_1
        weapons.add(new Napalm(isDrivable));  // WEAPON_2

        // LOAD TEXTURES
        try {
            if (isHostile) {
                if (napalm_tank_hostile_texture == null) {
                    napalm_tank_hostile_texture = new Image("assets/war_attenders/tanks/napalm_tank.png")
                            .getTexture();
                }
                base_image = new Image(napalm_tank_hostile_texture);
                if (napalm_tank_hostile_turret_texture == null) {
                    napalm_tank_hostile_turret_texture =
                            new Image("assets/war_attenders/tanks/napalm_tank_turret.png")
                                    .getTexture();
                }
                turret = new Image(napalm_tank_hostile_turret_texture);
            } else {    // friendly
                if (napalm_tank_friendly_texture == null) {
                    napalm_tank_friendly_texture = new Image("assets/war_attenders/tanks/napalm_tank.png")
                            .getTexture();
                }
                base_image = new Image(napalm_tank_friendly_texture);
                if (napalm_tank_friendly_turret_texture == null) {
                    napalm_tank_friendly_turret_texture =
                            new Image("assets/war_attenders/tanks/napalm_tank_turret.png")
                                    .getTexture();
                }
                turret = new Image(napalm_tank_friendly_turret_texture);
            }
        } catch (SlickException e) {
            e.printStackTrace();
        }

        collisionModel = new CollisionModel(position, base_image.getWidth(), base_image.getHeight());
        super.init();
    }

    @Override
    public void changeHealth(float amount) {
        super.changeHealth(amount, ARMOR);
    }
}
