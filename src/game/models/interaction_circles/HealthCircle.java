package game.models.interaction_circles;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class HealthCircle extends InteractionCircle {

    public static final float HEAL_SPEED = 0.05f;

    public HealthCircle(Vector2f map_position) {
        super(map_position);
        try {
            outerCircle = new Image("assets/interaction_circles/health_circle.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(GameContainer gc, int deltaTime) {
        super.update(gc, deltaTime);
        outerCircle.rotate(0.2f * deltaTime);
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        outerCircle.drawCentered(position.x, position.y);
    }

    @Override
    public void drawPreview(Graphics graphics) {
        super.drawPreview(graphics);
        outerCircle.draw(position.x, position.y, 0.2f);
    }

}
