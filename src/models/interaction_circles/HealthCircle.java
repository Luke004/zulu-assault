package models.interaction_circles;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class HealthCircle extends InteractionCircle {

    public static final float HEAL_SPEED = 0.03f;

    public HealthCircle(Vector2f map_position) {
        super(map_position);
        try {
            outerCircle = new Image("assets/interaction_circles/health_circle.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(int deltaTime) {
        super.update(deltaTime);
        outerCircle.rotate(0.2f * deltaTime);
    }

    @Override
    public void draw() {
        super.draw();
        outerCircle.drawCentered(position.x, position.y);
    }

}
