package models.war_attenders.tanks;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class AgileTank extends Tank {


    public AgileTank(Vector2f startPos) {
        super(startPos);

        // individual AgileTank attributes
        movement_speed = 0.3f;
        rotate_speed = 0.15f;
        turret_rotate_speed = 0.2f;

        try {
            image = new Image("assets/tanks/agile_tank.png");
            turret = new Image("assets/tanks/agile_tank_turret.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
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
        return image.getRotation();
    }

    @Override
    public void rotate(RotateDirection r, int deltaTime) {
        switch (r) {
            case ROTATE_DIRECTION_LEFT:
                image.rotate(-rotate_speed * deltaTime);
                turret.rotate(-rotate_speed * deltaTime);
                break;
            case ROTATE_DIRECTION_RIGHT:
                image.rotate(rotate_speed * deltaTime);
                turret.rotate(rotate_speed * deltaTime);
                break;
        }
    }
}
