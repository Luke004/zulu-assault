package models;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

/*
A class for entities that are static on map and have collision
 */
public abstract class StaticCollisionEntity {
    final protected Vector2f position;
    protected CollisionModel collisionModel;
    protected Image base_image;

    public StaticCollisionEntity(Vector2f position) {
        this.position = position;
    }

    abstract public void draw();

    abstract public void update(int deltaTime);

    public void initCollisionModel(int width, int height) {
        // center the position of the collision model
        Vector2f centeredPosition = new Vector2f(position.x + width / 2.f, position.y + height / 2.f);
        collisionModel = new CollisionModel(centeredPosition, width, height);
        collisionModel.update(0);
    }

    public CollisionModel getCollisionModel() {
        return collisionModel;
    }
}
