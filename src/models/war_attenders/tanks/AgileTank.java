package models.war_attenders.tanks;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class AgileTank extends Tank {


    public AgileTank(Vector2f startPos) {
        super(startPos);

        // individual AgileTank attributes
        movementSpeed = 0.3f;
        rotateSpeed = 0.15f;

        try {
            image = new Image("assets/tanks/agile_tank.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public float getRotation() {
        return image.getRotation();
    }

    @Override
    public void rotate(RotateDirection rotateDirection, int deltaTime) {
        switch (rotateDirection) {
            case ROTATE_DIRECTION_LEFT:
                image.rotate(-rotateSpeed * deltaTime);
                break;
            case ROTATE_DIRECTION_RIGHT:
                image.rotate(rotateSpeed * deltaTime);
                break;
        }
    }
}
