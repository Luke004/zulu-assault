package game.models.weapons.projectiles;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class AirRocket extends AirProjectile {

    protected Animation rocket_animation;
    private final int ANIMATION_WIDTH_HALF, ANIMATION_HEIGHT_HALF;
    private float xVal, yVal;

    public AirRocket(Vector2f startPos, Vector2f dir, float rotation, Texture projectile_texture, Animation rocket_animation) {
        super(startPos, dir, rotation, projectile_texture);
        this.maxLifetime = 350;   // maximum lifetime is 350 millis

        this.rocket_animation = rocket_animation;
        ANIMATION_WIDTH_HALF = rocket_animation.getCurrentFrame().getWidth() / 2;
        ANIMATION_HEIGHT_HALF = rocket_animation.getCurrentFrame().getHeight() / 2;
        speed = 0.5f;   // set individual rocket speed (slower than from a normal projectile)
        // calculate x and y to set rocket behind the bullet
        final float DISTANCE = -70;
        final float SPAWN_X = -3;
        xVal = (float) (Math.cos(((rotation) * Math.PI) / 180) * SPAWN_X
                + -Math.sin(((rotation) * Math.PI) / 180) * DISTANCE);
        yVal = (float) (Math.sin(((rotation) * Math.PI) / 180) * SPAWN_X
                + Math.cos(((rotation) * Math.PI) / 180) * DISTANCE);
    }


    @Override
    public void update(int deltaTime) {
        if (lifetime <= maxLifetime - 50) {
            super.update(deltaTime);
        } else {
            this.lifetime += deltaTime;
            if (!hasHitGround()) {
                setHitGround();
                // only update the collision model in the last 200 milliseconds
                collisionModel.update(deltaTime);
            }
        }
        rocket_animation.update(deltaTime);
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        //collision_model.draw(graphics);
        rocket_animation.draw(pos.x - ANIMATION_WIDTH_HALF - xVal, pos.y - ANIMATION_HEIGHT_HALF - yVal);
    }

}
