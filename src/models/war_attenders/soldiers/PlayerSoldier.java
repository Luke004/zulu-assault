package models.war_attenders.soldiers;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.geom.Vector2f;

public class PlayerSoldier extends Soldier {

    public PlayerSoldier(float start_xPos, float start_yPos) {
        super(new Vector2f(start_xPos, start_yPos));

        // individual PlayerSoldier attributes
        max_speed = 0.1f;
        acceleration_factor = 0.05f;
        deceleration_factor = 0.1f; // not needed
        rotate_speed = 0.25f;

        try {
            base_image = new Image("assets/soldiers/player_soldier_animation.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        collision_model = new Ellipse(start_xPos + base_image.getHeight() / 2,
                start_yPos + base_image.getHeight() / 2,
                base_image.getHeight() / 2,
                base_image.getHeight() / 2);
        animation = new Animation(false);
        int x = 0;
        do {
            animation.addFrame(base_image.getSubImage(x, 0, 12, 12), 300);
            x += 12;
        } while (x <= 24);
        animation.setCurrentFrame(1);
        animation.setLooping(true);
        animation.setPingPong(true);
        animation.stop();
    }

    @Override
    public float getRotation() {
        return animation.getCurrentFrame().getRotation();
    }

    @Override
    public void rotate(RotateDirection rotateDirection, int deltaTime) {
        switch (rotateDirection) {
            case ROTATE_DIRECTION_LEFT:
                for (int idx = 0; idx < animation.getFrameCount(); ++idx) {
                    animation.getImage(idx).rotate(-rotate_speed * deltaTime);
                }
                break;
            case ROTATE_DIRECTION_RIGHT:
                for (int idx = 0; idx < animation.getFrameCount(); ++idx) {
                    animation.getImage(idx).rotate(rotate_speed * deltaTime);
                }
                break;
        }
    }

}
