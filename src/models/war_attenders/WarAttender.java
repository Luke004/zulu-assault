package models.war_attenders;

import models.CollisionModel;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

import java.util.ArrayList;
import java.util.List;

public abstract class WarAttender {
    // model related
    public Image base_image;
    public CollisionModel collisionModel;
    public Texture bullet_texture;
    public List<Bullet> bullet_list;
    public Animation accessible_animation;
    public Image accessible_animation_image;

    // math related
    public Vector2f position;
    public Vector2f dir;

    // specs related
    public float max_speed, current_speed;
    public float acceleration_factor;   // number between [0 and 1] -> the smaller the faster the acceleration
    public float deceleration_factor;   // number between [0 and 1] -> the smaller the faster the deceleration
    public float rotate_speed;
    public float bullet_speed;
    public int shot_reload_time, current_reload_time;    // time(ms) it takes for tank to reload
    public final int MAX_BULLET_LIFETIME = 2000;

    // booleans
    public boolean isMoving, isHostile, show_accessible_animation;


    public WarAttender(Vector2f startPos, boolean isHostile) {
        this.isHostile = isHostile;
        position = startPos;
        dir = new Vector2f(0, 0);
        bullet_list = new ArrayList<>();
        if (!isHostile) {
            initAccessibleAnimation();
        }
    }

    public abstract void draw(Graphics graphics);

    public abstract void update(GameContainer gc, int delta);

    public void accelerate(Move direction, int deltaTime) {
        isMoving = true;
        if (current_speed < max_speed) {
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
            current_speed = 0.f;
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

    public void showAccessibleAnimation(boolean activate) {
        show_accessible_animation = activate;
    }

    private void initAccessibleAnimation() {
        show_accessible_animation = true;
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
    }

    public CollisionModel getCollisionModel() {
        return collisionModel;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public boolean isHostile() {
        return isHostile;
    }

    public Image getBase_image() {
        return base_image;
    }

    public Vector2f getDir() {
        return dir;
    }

    public abstract float getRotation();

    public abstract void rotate(RotateDirection r, int deltaTime);

    public abstract void shoot();

    public enum RotateDirection {
        ROTATE_DIRECTION_LEFT, ROTATE_DIRECTION_RIGHT;
    }

    public enum Move {
        MOVE_UP, MOVE_DOWN
    }

    public class Bullet {
        public int bullet_lifetime;
        public Image bullet_image;
        public CollisionModel bullet_collision_model;
        public Vector2f bullet_pos, bullet_dir;

        public Bullet(Vector2f startPos, Vector2f dir, float rotation){
            this.bullet_image = new Image(bullet_texture);
            bullet_image.setRotation(rotation);
            this.bullet_pos = startPos;
            this.bullet_dir = dir;
            this.bullet_collision_model = bullet_collision_model;
        }

        public void update(int deltaTime){
            this.bullet_pos.x += this.bullet_dir.x * bullet_speed * deltaTime;
            this.bullet_pos.y += this.bullet_dir.y * bullet_speed * deltaTime;

            this.bullet_lifetime += deltaTime;
        }

        public void draw(){
            this.bullet_image.draw(this.bullet_pos.x, this.bullet_pos.y);
        }

        public CollisionModel getCollisionModel(){
            return bullet_collision_model;
        }
    }
}
