package game.models.weapons.projectiles;

import game.models.CollisionModel;
import game.models.weapons.AGM;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;


public class AirShell extends AirProjectile {

    public AirShell(Vector2f startPos, Vector2f dir, float rotation, Texture projectile_texture) {
        super(startPos, dir, rotation, projectile_texture);
        this.projectile_max_lifetime = 1000;   // lifetime is 1 sec
        // special air bullet collision model (collision model is an impact rectangle 60x60px)
        this.projectile_collision_model = new CollisionModel(projectile_pos, 60, 60);
        projectile_speed = 0.3f;
    }

    @Override
    public void update(int deltaTime) {
        if (projectile_lifetime <= projectile_max_lifetime - 50) {
            projectile_image.rotate(0.5f * deltaTime);
            super.update(deltaTime);
        } else {
            this.projectile_lifetime += deltaTime;
            if (!hasHitGround()) {
                setHitGround();
                AGM.playDestructionAnimation(projectile_pos.x, projectile_pos.y);
                // only update the collision model in the last 200 milliseconds
                projectile_collision_model.update(deltaTime);
            }
        }
    }
}
