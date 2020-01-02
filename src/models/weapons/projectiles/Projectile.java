package models.weapons.projectiles;

import models.CollisionModel;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public abstract class Projectile {
    public int lifetime, max_lifetime;
    public Image image;
    public CollisionModel collision_model;
    public Vector2f pos, dir;
    protected float speed;
    protected final int WIDTH_HALF, HEIGHT_HALF;
    public boolean isGroundProjectile;

    public Projectile(Vector2f startPos, Vector2f dir, float rotation, Texture projectile_texture) {
        this.image = new Image(projectile_texture);
        image.setRotation(rotation);
        this.pos = startPos;
        this.dir = dir;
        this.speed = 0.8f;   // default speed
        this.isGroundProjectile = true;    // default is a ground projectile
        this.max_lifetime = 4000;   // default maximum lifetime (4 sec)
        WIDTH_HALF = image.getWidth() / 2;
        HEIGHT_HALF = image.getHeight() / 2;
    }

    public void update(int deltaTime) {
        this.pos.x += this.dir.x * speed * deltaTime;
        this.pos.y += this.dir.y * speed * deltaTime;
        this.lifetime += deltaTime;
    }

    public void draw(Graphics graphics) {
        this.image.draw(this.pos.x - WIDTH_HALF, this.pos.y - HEIGHT_HALF);
    }

    public CollisionModel getCollisionModel() {
        return collision_model;
    }

    public void setProjectileSpeed(float speed) {
        this.speed = speed;
    }
}
