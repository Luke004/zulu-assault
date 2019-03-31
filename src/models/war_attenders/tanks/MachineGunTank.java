package models.war_attenders.tanks;

import models.CollisionModel;
import models.weapons.MachineGun;
import models.weapons.RocketLauncher;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class MachineGunTank extends Tank {

    public MachineGunTank(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        // attributes equal for humans and bots
        max_health = 100;
        current_health = max_health;
        armor = 50;

        if (isDrivable) {
            // individual MachineGunTank attributes for human players
            max_speed = 0.3f;
            backwards_speed = 0.15f;
            acceleration_factor = 0.0005f;
            deceleration_factor = 0.0005f;
            rotate_speed = 0.15f;
            turret_rotate_speed = 0.15f;
        } else {
            // individual MachineGunTank attributes for bots
            max_speed = 0.3f;
            backwards_speed = 0.15f;
            acceleration_factor = 0.0005f;
            deceleration_factor = 0.0005f;
            rotate_speed = 0.15f;
            turret_rotate_speed = 0.5f;
        }
        weapons.add(new MachineGun(isDrivable));  // WEAPON_1

        try {
            base_image = new Image("assets/war_attenders/tanks/agile_tank.png");
            turret = new Image("assets/war_attenders/tanks/agile_tank_turret.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        collisionModel = new CollisionModel(position, base_image.getWidth(), base_image.getHeight());
        super.init();
    }
}
