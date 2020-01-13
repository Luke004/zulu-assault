package models.war_attenders.tanks;

import models.CollisionModel;
import models.weapons.RocketLauncher;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class RocketTank extends Tank {

    public RocketTank(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        // attributes equal for humans and bots
        max_health = 100;
        current_health = max_health;
        armor = 50;

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

        try {
            base_image = new Image("assets/war_attenders/tanks/rocket_tank.png");
            turret = new Image("assets/war_attenders/tanks/rocket_tank_turret.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        collisionModel = new CollisionModel(position, base_image.getWidth(), base_image.getHeight());
        super.init();
    }
}