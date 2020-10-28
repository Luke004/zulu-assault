package game.models;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

public abstract class Element {

    protected Vector2f position;
    protected Image base_image;
    protected CollisionModel collisionModel;

    abstract public void draw(Graphics graphics);

    abstract public void drawPreview(Graphics graphics);

    abstract public void update(GameContainer gc, int deltaTime);

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public void editorUpdate(GameContainer gc, int deltaTime) {
        this.update(gc, deltaTime);
    }

    public Vector2f getPosition() {
        return this.position;
    }

    public CollisionModel getCollisionModel() {
        return collisionModel;
    }

    public Image getBaseImage() {
        return this.base_image;
    }

}
