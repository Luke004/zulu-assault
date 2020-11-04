package game.models.weapons.projectiles;

import game.models.CollisionModel;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class GroundRocket extends Projectile {

    protected Animation rocket_animation;
    private final int ANIMATION_WIDTH_HALF, ANIMATION_HEIGHT_HALF;
    private float xVal, yVal;
    private final static float ROCKET_SPEED = 0.37f;

    public GroundRocket(Vector2f startPos, Vector2f dir, float rotation, Texture projectile_texture,  Animation rocket_animation) {
        super(startPos, dir, rotation, projectile_texture);

        this.projectile_collision_model = new CollisionModel(projectile_pos, WIDTH_HALF * 2, HEIGHT_HALF * 2);

        this.rocket_animation = rocket_animation;
        ANIMATION_WIDTH_HALF = rocket_animation.getCurrentFrame().getWidth() / 2;
        ANIMATION_HEIGHT_HALF = rocket_animation.getCurrentFrame().getHeight() / 2;
        projectile_speed = ROCKET_SPEED;   // set individual rocket speed (slower than from a normal projectile)
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
        super.update(deltaTime);
        projectile_collision_model.update(projectile_image.getRotation());
        rocket_animation.update(deltaTime);
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        rocket_animation.draw(projectile_pos.x - ANIMATION_WIDTH_HALF - xVal, projectile_pos.y - ANIMATION_HEIGHT_HALF - yVal);
    }
}
