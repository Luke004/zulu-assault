package models.war_attenders.tanks;

import models.CollisionModel;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class MediumTank extends Tank {

    public MediumTank(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);

        // individual MediumTank attributes
        max_speed = 0.15f;
        acceleration_factor = 0.00005f;
        deceleration_factor = 0.995f;
        rotate_speed = 0.15f;
        turret_rotate_speed = 0.2f;
        bullet_speed = 0.8f;

        try {
            base_image = new Image("assets/tanks/medium_tank.png");
            turret = new Image("assets/tanks/medium_tank_turret.png");
            bullet_texture = new Image("assets/bullets/bullet_medium.png").getTexture();
        } catch (SlickException e) {
            e.printStackTrace();
        }
        collisionModel = new CollisionModel(position, base_image.getWidth(), base_image.getHeight());
    }

    @Override
    public void shoot(){
        float rotation_angle = turret.getRotation();
        float xVal = (float) Math.sin(rotation_angle * Math.PI / 180);
        float yVal = (float) -Math.cos(rotation_angle * Math.PI / 180);
        Vector2f bullet_dir = new Vector2f(xVal, yVal);
        Bullet bullet = new Bullet(new Vector2f(position.x, position.y), bullet_dir, rotation_angle);
        bullet_list.add(bullet);
    }
}
