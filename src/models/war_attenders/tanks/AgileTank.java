package models.war_attenders.tanks;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

public class AgileTank extends Tank {


    public AgileTank(Vector2f startPos) {
        super(startPos);

        // individual AgileTank attributes
        movement_speed = 0.3f;
        rotate_speed = 0.15f;
        turret_rotate_speed = 0.2f;
        deceleration_factor = 0.85f;

        try {
            base_image = new Image("assets/tanks/agile_tank.png");
            turret = new Image("assets/tanks/agile_tank_turret.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        collision_model = new Rectangle(startPos.x, startPos.y, base_image.getWidth(), base_image.getHeight());
    }

    @Override
    public void rotateTurret(RotateDirection r, int deltaTime) {
        switch (r) {
            case ROTATE_DIRECTION_LEFT:
                turret.rotate(-turret_rotate_speed * deltaTime);
                break;
            case ROTATE_DIRECTION_RIGHT:
                turret.rotate(turret_rotate_speed * deltaTime);
                break;
        }
    }

    @Override
    public float getRotation() {
        return base_image.getRotation();
    }

    @Override
    public void rotate(RotateDirection r, int deltaTime) {
        float degree, radian;
        switch (r) {
            case ROTATE_DIRECTION_LEFT:
                degree = -rotate_speed * deltaTime;
                base_image.rotate(degree);
                turret.rotate(degree);
                radian = (float) (degree * Math.PI / 180);
                collision_model = collision_model.transform(Transform.createRotateTransform(
                        radian,
                        collision_model.getCenterX(),
                        collision_model.getCenterY()));
                break;
            case ROTATE_DIRECTION_RIGHT:
                degree = rotate_speed * deltaTime;
                base_image.rotate(degree);
                turret.rotate(degree);
                radian = (float) (degree * Math.PI / 180);
                collision_model = collision_model.transform(Transform.createRotateTransform(
                        radian,
                        collision_model.getCenterX(),
                        collision_model.getCenterY()));
                break;
        }
    }
}
