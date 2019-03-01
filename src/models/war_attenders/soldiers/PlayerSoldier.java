package models.war_attenders.soldiers;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class PlayerSoldier extends Soldier {

    public PlayerSoldier(float start_xPos, float start_yPos) {
        this(new Vector2f(start_xPos, start_yPos));
    }

    public PlayerSoldier(Vector2f startPos) {
        super(startPos);

        // individual PlayerSoldier attributes
        movementSpeed = 0.1f;
        rotateSpeed = 0.25f;

        try {
            image = new Image("assets/soldiers/player_soldier_animation.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        animation = new Animation(false);
        int x = 0;
        do {
            animation.addFrame(image.getSubImage(x, 0, 12, 12), 300);
            x += 12;
        } while (x <= 24);
        animation.setCurrentFrame(1);
        animation.setLooping(true);
        animation.setPingPong(true);
        animation.stop();
    }

}
