package models.war_attenders;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

public abstract class WarAttender {
    public Vector2f pos, dir;
    public float movementSpeed;
    public float rotateSpeed;
    public Image image;

    public abstract void draw();

    public abstract void update(GameContainer gc, int delta);

    public float getMovementSpeed(){
        return movementSpeed;
    }

    public float getRotateSpeed() {
        return rotateSpeed;
    }

    public Vector2f getPos() {
        return pos;
    }

    public Vector2f getDir() {
        return dir;
    }
}
