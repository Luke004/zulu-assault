package models.war_attenders.tanks;

import models.CollisionModel;
import models.weapons.Napalm;
import models.weapons.Plasma;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class NapalmTank extends Tank {

    public NapalmTank(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);

        // individual ShellTank attributes
        max_health = 100;
        current_health = max_health;
        armor = 75;
        max_speed = 0.1f;
        backwards_speed = max_speed / 2;
        acceleration_factor = 0.00005f;
        deceleration_factor = 0.0009f;
        rotate_speed = 0.15f;
        turret_rotate_speed = 0.2f;
        weapons.add(new Plasma());  // WEAPON_1
        weapons.add(new Napalm());  // WEAPON_2

        try {
            base_image = new Image("assets/war_attenders/tanks/flamethrower_tank.png");
            turret = new Image("assets/war_attenders/tanks/flamethrower_tank_turret.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        collisionModel = new CollisionModel(position, base_image.getWidth(), base_image.getHeight());
        super.init();
    }
}
