package models.weapons;

import models.CollisionModel;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Weapon {
    Texture bullet_texture;
    protected List<Bullet> bullet_list;
    public static final int MAX_BULLET_LIFETIME = 4000;
    // specs related
    float bullet_damage, shot_reload_time, current_reload_time;
    float bullet_speed;

    public Weapon() {
        this.bullet_list = new ArrayList<>();
    }

    public void update(int deltaTime) {
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
    }

    public void draw(Graphics graphics) {
        for (Bullet b : bullet_list) {
            b.draw(graphics);
        }
    }

    public void fire(float spawnX, float spawnY, float rotation_angle) {
        if (canFire()) {
            current_reload_time = 0;    // reset the reload time when a shot is fired
            spawnX += -Math.sin(((rotation_angle) * Math.PI) / 180) * -30.f;
            spawnY += Math.cos(((rotation_angle) * Math.PI) / 180) * -30.f;
            Vector2f bullet_spawn = new Vector2f(spawnX, spawnY);

            float xVal = (float) Math.sin(rotation_angle * Math.PI / 180);
            float yVal = (float) -Math.cos(rotation_angle * Math.PI / 180);
            Vector2f bullet_dir = new Vector2f(xVal, yVal);

            Weapon.Bullet bullet = new Weapon.Bullet(bullet_spawn, bullet_dir, rotation_angle);
            bullet_list.add(bullet);
        }
    }

    public boolean canFire() {
        return current_reload_time >= shot_reload_time;
    }

    public Iterator<Bullet> getBullets() {
        return bullet_list.iterator();
    }

    /*
    for KI to shoot slower than player (less difficult)
     */
    public void multiplyShotReloadTime(int factor) {
        shot_reload_time *= factor;
    }

    public float getBulletDamage() {
        return bullet_damage;
    }

    public class Bullet {
        public int bullet_lifetime;
        public Image bullet_image;
        public CollisionModel bullet_collision_model;
        public Vector2f bullet_pos, bullet_dir;
        private final int BULLET_WIDTH_HALF, BULLET_HEIGHT_HALF;

        public Bullet(Vector2f startPos, Vector2f dir, float rotation) {
            this.bullet_image = new Image(bullet_texture);
            bullet_image.setRotation(rotation);
            this.bullet_pos = startPos;
            this.bullet_dir = dir;
            BULLET_WIDTH_HALF = bullet_image.getWidth() / 2;
            BULLET_HEIGHT_HALF = bullet_image.getHeight() / 2;
            this.bullet_collision_model = new CollisionModel(bullet_pos, BULLET_WIDTH_HALF * 2, BULLET_HEIGHT_HALF * 2);
        }

        public void update(int deltaTime) {
            this.bullet_pos.x += this.bullet_dir.x * bullet_speed * deltaTime;
            this.bullet_pos.y += this.bullet_dir.y * bullet_speed * deltaTime;

            this.bullet_lifetime += deltaTime;

            bullet_collision_model.update(bullet_image.getRotation());
        }

        public void draw(Graphics graphics) {
            this.bullet_image.draw(this.bullet_pos.x - BULLET_WIDTH_HALF, this.bullet_pos.y - BULLET_HEIGHT_HALF);

            //bullet_collision_model.draw(graphics);
        }

        public CollisionModel getCollisionModel() {
            return bullet_collision_model;
        }
    }
}
