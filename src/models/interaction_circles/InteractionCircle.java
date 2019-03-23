package models.interaction_circles;

import models.CollisionModel;
import models.war_attenders.MovableWarAttender;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public abstract class InteractionCircle {
    private Image innerCircle;
    Image outerCircle;
    private final CollisionModel collisionModel;
    final Vector2f position;

    public InteractionCircle(Vector2f map_position) {
        this.position = map_position;
        try {
            this.innerCircle = new Image("assets/interaction_circles/inner_circle.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        collisionModel = new CollisionModel(map_position, innerCircle.getWidth()/3, innerCircle.getHeight()/3);
        collisionModel.update(0);
    }

    public void update(int deltaTime) {
        innerCircle.rotate(-0.1f * deltaTime);
    }

    public void draw(){
        innerCircle.drawCentered(position.x, position.y);
    }

    public CollisionModel getCollisionModel(){
        return collisionModel;
    }


}
