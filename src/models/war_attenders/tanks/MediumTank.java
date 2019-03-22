package models.war_attenders.tanks;

import models.CollisionModel;
import models.weapons.RocketLauncher;
import models.weapons.Shell;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class MediumTank extends Tank {

    public MediumTank(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);

        // individual MediumTank attributes
        max_health = 100;
        current_health = max_health;
        max_speed = 0.15f;
        backwards_speed = max_speed / 2;
        acceleration_factor = 0.00005f;
        deceleration_factor = 0.0009f;
        rotate_speed = 0.15f;
        turret_rotate_speed = 0.2f;
        weapons.add(new Shell());   // WEAPON_1

        try {
            base_image = new Image("assets/tanks/medium_tank.png");
            turret = new Image("assets/tanks/medium_tank_turret.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        collisionModel = new CollisionModel(position, base_image.getWidth(), base_image.getHeight());
        super.init();
    }

}
