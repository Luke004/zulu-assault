package models.war_attenders.tanks;

import models.CollisionModel;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class AgileTank_v2 extends Tank {


    public AgileTank_v2(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);

        // individual AgileTank attributes
        max_speed = 0.3f;
        acceleration_factor = 0.0005f;
        deceleration_factor = 0.995f;
        rotate_speed = 0.15f;
        turret_rotate_speed = 0.2f;

        try {
            base_image = new Image("assets/tanks/agile_tank_v2.png");
            turret = new Image("assets/tanks/agile_tank_v2_turret.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        collisionModel = new CollisionModel(position, base_image.getWidth(), base_image.getHeight());
    }
}

