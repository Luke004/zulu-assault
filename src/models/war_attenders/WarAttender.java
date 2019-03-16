package models.war_attenders;

import models.CollisionModel;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class WarAttender {
    // model related
    public Image base_image, health_bar_image;
    public CollisionModel collisionModel;
    public Texture bullet_texture;
    public List<Bullet> bullet_list;
    public Animation accessible_animation;
    public Image accessible_animation_image;

    // math related
    public Vector2f position;
    public Vector2f dir;

    // specs related
    public int current_health, max_health;
    public float max_speed, current_speed;
    public float rotate_speed;
    public float bullet_speed;
    public int shot_reload_time, current_reload_time;    // time(ms) it takes for tank to reload
    public final int MAX_BULLET_LIFETIME = 2000;
    public int bullet_damage;

    // booleans
    public boolean isMoving, isHostile, show_accessible_animation;


    public WarAttender(Vector2f startPos, boolean isHostile) {
        this.isHostile = isHostile;
        position = startPos;
        dir = new Vector2f(0, 0);
        bullet_list = new ArrayList<>();
        if (isHostile) {
            try {
                health_bar_image = new Image("assets/healthbars/healthbar_enemy.png");
            } catch (SlickException e) {
                e.printStackTrace();
            }
        } else {
            try {
                health_bar_image = new Image("assets/healthbars/healthbar_friendly.png");
            } catch (SlickException e) {
                e.printStackTrace();
            }
            initAccessibleAnimation();
        }
    }

    public void draw(Graphics graphics) {
        health_bar_image.draw(position.x - health_bar_image.getWidth() / 2 - 7.5f, position.y - base_image.getHeight() / 2 - 15);

        // draw health bar damage using a black rectangle
        graphics.setColor(Color.black);
        if (current_health > 0) {
            graphics.fillRect(position.x - 15, position.y - base_image.getHeight() / 2 - 14, (29 - (((float) current_health) / (((float) max_health) / 29))), 5);
        } else {    // destroyed (rectangle is full black size)
            graphics.fillRect(position.x - 15, position.y - base_image.getHeight() / 2 - 14, 29, 5);
        }

        // BULLET RELATED STUFF
        for (Bullet b : bullet_list) {
            b.draw(graphics);
        }

        // COLLISION RELATED STUFF
        collisionModel.draw(graphics);
    }

    public void update(GameContainer gc, int deltaTime) {
        // BULLET RELATED STUFF
        if (current_reload_time < shot_reload_time) {
            current_reload_time += deltaTime;
        }
        Iterator<Bullet> iter = bullet_list.iterator();
        while (iter.hasNext()) {
            Bullet b = iter.next();
            b.update(deltaTime);

            // remove bullet if if max lifetime was reached
            if (b.bullet_lifetime > MAX_BULLET_LIFETIME) {
                iter.remove();
            }
        }

        // COLLISION RELATED STUFF
        collisionModel.update(base_image.getRotation());
    }

    public void calculateMovementVector(int deltaTime, Direction direction) {
        dir.x = (float) Math.sin(getRotation() * Math.PI / 180);
        dir.y = (float) -Math.cos(getRotation() * Math.PI / 180);
        if(direction == Direction.BACKWARDS){
            dir.x *= -1;
            dir.y *= -1;
        }
        dir.x *= deltaTime * current_speed;
        dir.y *= deltaTime * current_speed;
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

    public boolean isHostile() {
        return isHostile;
    }

    public void setMoving(boolean b) {
        isMoving = b;
    }

    public boolean isMoving(){
        return isMoving;
    }

    public Iterator<Bullet> getBullets() {
        return bullet_list.iterator();
    }

    public int getBulletDamage() {
        return bullet_damage;
    }

    public abstract void onCollision(WarAttender enemy);

    public void changeHealth(int amount) {
        current_health += amount;
    }

    public abstract float getRotation();

    public abstract void rotate(RotateDirection r, int deltaTime);

    public abstract void shoot();

    public boolean canShoot() {
        return current_reload_time >= shot_reload_time;
    }

    public enum RotateDirection {
        ROTATE_DIRECTION_LEFT, ROTATE_DIRECTION_RIGHT;
    }

    public enum Direction {
        FORWARD, BACKWARDS
    }

    public class Bullet {
        public int bullet_lifetime;
        public Image bullet_image;
        public CollisionModel bullet_collision_model;
        public Vector2f bullet_pos, bullet_dir;

        public Bullet(Vector2f startPos, Vector2f dir, float rotation) {
            this.bullet_image = new Image(bullet_texture);
            bullet_image.setRotation(rotation);
            this.bullet_pos = startPos;
            this.bullet_dir = dir;
            this.bullet_collision_model = new CollisionModel(bullet_pos, bullet_image.getWidth(), bullet_image.getHeight());
        }

        public void update(int deltaTime) {
            this.bullet_pos.x += this.bullet_dir.x * bullet_speed * deltaTime;
            this.bullet_pos.y += this.bullet_dir.y * bullet_speed * deltaTime;

            this.bullet_lifetime += deltaTime;

            bullet_collision_model.update(bullet_image.getRotation());
        }

        public void draw(Graphics graphics) {
            this.bullet_image.draw(this.bullet_pos.x, this.bullet_pos.y);

            //bullet_collision_model.draw(graphics);
        }

        public CollisionModel getCollisionModel() {
            return bullet_collision_model;
        }
    }
}
