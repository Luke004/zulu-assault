package models.war_attenders.tanks;

import models.CollisionModel;
import models.weapons.RocketLauncher;
import models.weapons.Shell;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class ShellTank extends Tank {

    public ShellTank(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        // attributes equal for humans and bots
        max_health = 100;
        current_health = max_health;
        armor = 60;

        scoreValue = 1000;

        if (isDrivable) {
            // individual ShellTank attributes for human players
            max_speed = 0.15f;
            backwards_speed = max_speed / 2;
            acceleration_factor = 0.00005f;
            deceleration_factor = 0.0009f;
            rotate_speed = 0.2f;
        } else {
            // individual ShellTank attributes for bots
            max_speed = 0.05f;
            backwards_speed = max_speed / 2;
            acceleration_factor = 0.00005f;
            deceleration_factor = 0.0009f;
            rotate_speed = 0.05f;
        }

        weapons.add(new Shell(isDrivable));   // WEAPON_1

        try {
            base_image = new Image("assets/war_attenders/tanks/medium_tank.png");
            turret = new Image("assets/war_attenders/tanks/medium_tank_turret.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        collisionModel = new CollisionModel(position, base_image.getWidth(), base_image.getHeight());
        super.init();
    }
}
