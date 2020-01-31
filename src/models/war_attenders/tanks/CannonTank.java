package models.war_attenders.tanks;

import models.CollisionModel;
import models.weapons.Cannon;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class CannonTank extends Tank {

    public CannonTank(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        // attributes equal for humans and bots
        armor = 50;

        if (isDrivable) {
            // individual MachineGunTank attributes for human players
            max_speed = 0.27f;
            backwards_speed = 0.15f;
            acceleration_factor = 0.0005f;
            deceleration_factor = 0.0005f;
            rotate_speed = 0.15f;
        } else {
            // individual MachineGunTank attributes for bots
            max_speed = 0.3f;
            backwards_speed = 0.15f;
            acceleration_factor = 0.0005f;
            deceleration_factor = 0.0005f;
            rotate_speed = 0.15f;
        }

        weapons.add(new Cannon(isDrivable));  // WEAPON_1

        try {
            if (isHostile) {
                base_image = new Image("assets/war_attenders/tanks/cannon_tank_hostile.png");
                turret = new Image("assets/war_attenders/tanks/cannon_tank_hostile_turret.png");
            } else {
                base_image = new Image("assets/war_attenders/tanks/cannon_tank_friendly.png");
                turret = new Image("assets/war_attenders/tanks/cannon_tank_friendly_turret.png");
            }
        } catch (SlickException e) {
            e.printStackTrace();
        }
        collisionModel = new CollisionModel(position, base_image.getWidth(), base_image.getHeight());
        super.init();
    }
}
