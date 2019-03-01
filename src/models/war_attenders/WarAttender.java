package models.war_attenders;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

public abstract class WarAttender {
    public Vector2f pos, dir;
    public float movement_speed;
    public float rotate_speed;
    public Image image;

    public WarAttender(Vector2f startPos) {
        pos = startPos;
        dir = new Vector2f(0, 0);
    }

    public abstract void draw();

    public abstract void update(GameContainer gc, int delta);

    public float getMovementSpeed() {
        return movement_speed;
    }

    public float getRotateSpeed() {
        return rotate_speed;
    }

    public Vector2f getPos() {
        return pos;
    }

    public Vector2f getDir() {
        return dir;
    }

    public abstract float getRotation();

    public abstract void rotate(RotateDirection r, int deltaTime);

    public enum RotateDirection {
        ROTATE_DIRECTION_LEFT, ROTATE_DIRECTION_RIGHT;
    }
}
