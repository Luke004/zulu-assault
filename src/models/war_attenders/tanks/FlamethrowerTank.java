package models.war_attenders.tanks;

import models.CollisionModel;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class FlamethrowerTank extends Tank {

    public FlamethrowerTank(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);

        // individual MediumTank attributes
        max_health = 100;
        current_health = max_health;
        max_speed = 0.1f;
        backwards_speed = max_speed / 2;
        acceleration_factor = 0.00005f;
        deceleration_factor = 0.0009f;
        rotate_speed = 0.15f;
        turret_rotate_speed = 0.2f;
        bullet_speed = 0.8f;
        shot_reload_time = 330;    // can shoot three times every sec
        bullet_damage = 4;

        if (isHostile) {    // double the reload time if its an enemy
            shot_reload_time *= 2;
        }

        try {
            base_image = new Image("assets/tanks/flamethrower_tank.png");
            turret = new Image("assets/tanks/flamethrower_tank_turret.png");
            bullet_texture = new Image("assets/bullets/plasma.png").getTexture();
        } catch (SlickException e) {
            e.printStackTrace();
        }
        collisionModel = new CollisionModel(position, base_image.getWidth(), base_image.getHeight());

        super.init();
    }
}
