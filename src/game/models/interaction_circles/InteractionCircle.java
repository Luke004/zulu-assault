package game.models.interaction_circles;

import game.models.CollisionModel;
import game.models.Element;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public abstract class InteractionCircle extends Element {

    protected Image outerCircle;

    public InteractionCircle(Vector2f position) {
        this.position = position;
        try {
            this.base_image = new Image("assets/interaction_circles/inner_circle.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        // init the collision model and center its position
        int width = base_image.getWidth() / 3;
        int height = base_image.getHeight() / 3;
        Vector2f centeredPosition = new Vector2f(position.x + width / 2.f, position.y + height / 2.f);
        collisionModel = new CollisionModel(centeredPosition, width, height);
    }

    @Override
    public void update(GameContainer gc, int deltaTime) {
        base_image.rotate(-0.1f * deltaTime);
    }

    @Override
    public void draw(Graphics graphics) {
        base_image.drawCentered(position.x, position.y);
    }

    @Override
    public void drawPreview(Graphics graphics) {
        base_image.draw(position.x, position.y, 0.2f);
    }

}
