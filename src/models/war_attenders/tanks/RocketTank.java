package models.war_attenders.tanks;

import models.CollisionModel;
import models.weapons.RocketLauncher;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class RocketTank extends Tank {

    private static Texture rocket_tank_hostile_texture, rocket_tank_friendly_texture,
            rocket_tank_hostile_turret_texture, rocket_tank_friendly_turret_texture;

    private static final float ARMOR = 50.f;

    public RocketTank(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        if (isDrivable) {
            // individual RocketTank attributes for human players
            max_speed = 0.2f;
            backwards_speed = max_speed / 2;
            acceleration_factor = 0.0001f;
            deceleration_factor = 0.0009f;
            rotate_speed = 0.15f;
        } else {
            // individual RocketTank attributes for bots
            max_speed = 0.07f;
            backwards_speed = max_speed / 2;
            acceleration_factor = 0.0001f;
            deceleration_factor = 0.0009f;
            rotate_speed = 0.15f;
        }

        weapons.add(new RocketLauncher(isDrivable));   // WEAPON_1

        // LOAD TEXTURES
        try {
            if (isHostile) {
                if (rocket_tank_hostile_texture == null) {
                    rocket_tank_hostile_texture = new Image("assets/war_attenders/tanks/rocket_tank.png")
                            .getTexture();
                }
                base_image = new Image(rocket_tank_hostile_texture);
                if (rocket_tank_hostile_turret_texture == null) {
                    rocket_tank_hostile_turret_texture =
                            new Image("assets/war_attenders/tanks/rocket_tank_turret.png")
                                    .getTexture();
                }
                turret = new Image(rocket_tank_hostile_turret_texture);
            } else {    // friendly
                if (rocket_tank_friendly_texture == null) {
                    rocket_tank_friendly_texture = new Image("assets/war_attenders/tanks/rocket_tank.png")
                            .getTexture();
                }
                base_image = new Image(rocket_tank_friendly_texture);
                if (rocket_tank_friendly_turret_texture == null) {
                    rocket_tank_friendly_turret_texture =
                            new Image("assets/war_attenders/tanks/rocket_tank_turret.png")
                                    .getTexture();
                }
                turret = new Image(rocket_tank_friendly_turret_texture);
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