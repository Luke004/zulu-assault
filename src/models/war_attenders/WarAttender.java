package models.war_attenders;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

public abstract class WarAttender {
    public Vector2f map_position;
    public Vector2f coordinates;
    public Vector2f dir;
    public float movement_speed;
    public float rotate_speed;
    public Image image;

    public WarAttender(Vector2f startPos) {
        map_position = startPos;
        coordinates = new Vector2f(startPos.x, startPos.y);
        dir = new Vector2f(0, 0);
    }

    public abstract void draw();

    public abstract void update(GameContainer gc, int delta);

    public float getMovementSpeed() {
        return movement_speed;
    }

    public Vector2f getDir() {
        return dir;
    }

    public Vector2f getCoordinates() {
        return coordinates;
    }

    public Vector2f getMapPosition() {
        return map_position;
    }

    public void updateMapPosition(Vector2f map_position) {
        this.map_position.add(map_position);
    }

    public void updateCoordinates(Vector2f coordinates) {
        this.coordinates.sub(coordinates);
        //System.out.println(this.coordinates);
    }

    public abstract float getRotation();

    public abstract void rotate(RotateDirection r, int deltaTime);

    public enum RotateDirection {
        ROTATE_DIRECTION_LEFT, ROTATE_DIRECTION_RIGHT;
    }
}
