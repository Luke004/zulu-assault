package game.models.weapons.projectiles;

import game.models.CollisionModel;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;


public class AirShell extends AirProjectile {

    public AirShell(Vector2f startPos, Vector2f dir, float rotation, Texture projectile_texture) {
        super(startPos, dir, rotation, projectile_texture);
        this.maxLifetime = 1000;   // lifetime is 1 sec
        // special air bullet collision model (collision model is an impact rectangle 60x60px)
        this.collisionModel = new CollisionModel(pos, 60, 60);
        speed = 0.3f;
    }

    @Override
    public void update(int deltaTime) {
        if (lifetime <= maxLifetime - 50) {
            image.rotate(0.5f * deltaTime);
            super.update(deltaTime);
        } else {
            this.lifetime += deltaTime;
            if (!hasHitGround()) {
                setHitGround();
                // only update the collision model in the last 200 milliseconds
                collisionModel.update(deltaTime);
            }
        }
    }
}
