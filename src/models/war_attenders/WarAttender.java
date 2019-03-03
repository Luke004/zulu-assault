package models.war_attenders;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

public abstract class WarAttender {
    public Vector2f position;
    public Vector2f dir;
    public float max_speed, current_speed;
    public float acceleration_factor;   // number between [0 and 1] -> the smaller the faster the acceleration
    public float deceleration_factor;   // number between [0 and 1] -> the smaller the faster the deceleration
    public float rotate_speed;
    public Image base_image;
    public Shape collision_model;
    public Animation accessible_animation;
    public Image accessible_animation_image;
    public boolean isMoving;

    public WarAttender(Vector2f startPos) {
        position = startPos;
        dir = new Vector2f(0, 0);
    }

    public abstract void draw(Graphics graphics);

    public abstract void update(GameContainer gc, int delta);

    public void accelerate(Move direction, int deltaTime) {
        isMoving = true;
        if(current_speed < max_speed){
            current_speed += acceleration_factor;
        }
        calculateVector(direction, deltaTime);
        position.add(dir);
    }

    public void decelerate(Move direction, int deltaTime) {
        if (current_speed > 0.1f) {
            current_speed *= deceleration_factor;
        } else {
            isMoving = false;
            current_speed = 0;
        }
        calculateVector(direction, deltaTime);
        position.add(dir);
    }

    private void calculateVector(Move direction, int deltaTime) {
        switch (direction) {
            case MOVE_UP:
                dir.x = (float) Math.sin(getRotation() * Math.PI / 180);
                dir.y = (float) -Math.cos(getRotation() * Math.PI / 180);
                dir.x *= deltaTime * current_speed;
                dir.y *= deltaTime * current_speed;
                break;
            case MOVE_DOWN:
                // TODO
                break;
        }
    }

    public void setAccessibleAnimation(boolean activate) {
        if (activate) {
            try {
                accessible_animation_image = new Image("assets/healthbars/accessible_arrow_animation.png");
            } catch (SlickException e) {
                e.printStackTrace();
            }
            accessible_animation = new Animation(false);

            int x = 0;
            do {
                accessible_animation.addFrame(accessible_animation_image.getSubImage(x, 0, 17, 28), 1000);
                x += 17;
            } while (x <= 34);
            accessible_animation.setCurrentFrame(1);
        } else {
            accessible_animation_image = null;
            accessible_animation = null;
        }

    }

    public boolean isMoving(){
        return isMoving;
    }

    public Image getBase_image(){
        return base_image;
    }

    public Vector2f getDir() {
        return dir;
    }


    public Shape getCollisionModel() {
        return collision_model;
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
