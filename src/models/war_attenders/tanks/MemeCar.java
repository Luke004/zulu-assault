package models.war_attenders.tanks;

import models.CollisionModel;
import models.weapons.Uzi;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class MemeCar extends Tank {
    public MemeCar(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        // attributes equal for humans and bots
        armor = 20;
        max_speed = 0.1f;
        backwards_speed = 0.07f;
        acceleration_factor = 0.0005f;
        deceleration_factor = 0.0005f;
        rotate_speed = 0.15f;

        scoreValue = 50;

        if (isDrivable) weapons.add(new Uzi(isDrivable));  // WEAPON_1

        try {
            base_image = new Image("assets/war_attenders/tanks/meme_car.png");
            turret = new Image("assets/war_attenders/tanks/meme_car_turret.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        collisionModel = new CollisionModel(position, base_image.getWidth(), base_image.getHeight());
        super.init();
    }
}
