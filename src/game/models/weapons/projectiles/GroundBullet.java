package game.models.weapons.projectiles;

import game.models.CollisionModel;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class GroundBullet extends Projectile {
    public GroundBullet(Vector2f startPos, Vector2f dir, float rotation, Texture bullet_texture) {
        super(startPos, dir, rotation, bullet_texture);
        this.projectile_collision_model = new CollisionModel(projectile_pos, WIDTH_HALF * 2, HEIGHT_HALF * 2);
    }

    public void update(int deltaTime) {
        super.update(deltaTime);
        projectile_collision_model.update(projectile_image.getRotation());
    }

}
