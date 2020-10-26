package game.models.weapons.projectiles;

import game.models.CollisionModel;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public abstract class Projectile {
    public int projectile_lifetime, projectile_max_lifetime;
    public Image projectile_image;
    public CollisionModel projectile_collision_model;
    public Vector2f projectile_pos, projectile_dir;
    protected float projectile_speed;
    protected final int WIDTH_HALF, HEIGHT_HALF;

    public Projectile(Vector2f startPos, Vector2f dir, float rotation, Texture projectile_texture) {
        this.projectile_image = new Image(projectile_texture);
        projectile_image.setRotation(rotation);
        this.projectile_pos = startPos;
        this.projectile_dir = dir;
        this.projectile_speed = 0.6f;   // default projectile speed
        this.projectile_max_lifetime = 4000;   // default maximum lifetime (4 sec)
        WIDTH_HALF = projectile_image.getWidth() / 2;
        HEIGHT_HALF = projectile_image.getHeight() / 2;
    }

    public void update(int deltaTime) {
        this.projectile_pos.x += this.projectile_dir.x * projectile_speed * deltaTime;
        this.projectile_pos.y += this.projectile_dir.y * projectile_speed * deltaTime;
        this.projectile_lifetime += deltaTime;
    }

    public void draw(Graphics graphics) {
        this.projectile_image.draw(this.projectile_pos.x - WIDTH_HALF, this.projectile_pos.y - HEIGHT_HALF);
    }

    public CollisionModel getCollisionModel() {
        return projectile_collision_model;
    }

    public void setProjectileSpeed(float speed) {
        this.projectile_speed = speed;
    }
}
