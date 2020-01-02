package models.weapons.projectiles;

import models.CollisionModel;
import org.newdawn.slick.Animation;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class GroundRocket extends Rocket {
    public GroundRocket(Vector2f startPos, Vector2f dir, float rotation, Texture bullet_texture, Animation rocket_animation) {
        super(startPos, dir, rotation, bullet_texture, rocket_animation);
        this.collision_model = new CollisionModel(pos, WIDTH_HALF * 2, HEIGHT_HALF * 2);
    }

    @Override
    public void update(int deltaTime) {
        super.update(deltaTime);
        collision_model.update(image.getRotation());
        rocket_animation.update(deltaTime);
    }
}
