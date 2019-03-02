package models.war_attenders;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

public abstract class WarAttender {
    public Vector2f map_position;
    public Vector2f coordinates;
    public Vector2f dir;
    public float movement_speed;
    public float rotate_speed;
    public Image base_image;
    public Shape collision_model;

    public WarAttender(Vector2f startPos) {
        map_position = startPos;
        coordinates = new Vector2f(startPos.x, startPos.y);
        dir = new Vector2f(0, 0);
    }

    public abstract void draw(Graphics graphics);

    public abstract void update(GameContainer gc, int delta);

    public void move(Move move, int deltaTime) {
        switch (move) {
            case MOVE_UP:
                dir.x = (float) Math.sin(getRotation() * Math.PI / 180);
                dir.y = (float) -Math.cos(getRotation() * Math.PI / 180);
                dir.x *= deltaTime * movement_speed * -1;
                dir.y *= deltaTime * movement_speed * -1;
                break;
            case MOVE_DOWN:
                // TODO
                break;
        }
    }

    /*
    used when a soldier gets out of a tank or plane to freeze its position at the place the soldier got out at
     */
    public void freezePosition(Vector2f direction) {
        map_position.add(direction);    // WarAttender stays in the same place
        collision_model.setX(collision_model.getX() + direction.x);
        collision_model.setY(collision_model.getY() + direction.y);
    }

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

    public Shape getCollisionModel() {
        return collision_model;
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

    public enum Move {
        MOVE_UP, MOVE_DOWN
    }
}
