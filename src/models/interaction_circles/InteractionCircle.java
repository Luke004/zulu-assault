package models.interaction_circles;

import models.StaticCollisionEntity;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public abstract class InteractionCircle extends StaticCollisionEntity {

    protected Image outerCircle;

    public InteractionCircle(Vector2f position) {
        super(position);
        try {
            this.base_image = new Image("assets/interaction_circles/inner_circle.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        initCollisionModel(base_image.getWidth() / 3, base_image.getHeight() / 3);
    }

    @Override
    public void update(int deltaTime) {
        base_image.rotate(-0.1f * deltaTime);
    }

    @Override
    public void draw() {
        base_image.drawCentered(position.x, position.y);
    }
}
