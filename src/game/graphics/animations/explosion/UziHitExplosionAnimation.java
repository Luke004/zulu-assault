package game.graphics.animations.explosion;

import game.graphics.animations.AbstractVolatileAnimation;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;

public class UziHitExplosionAnimation extends AbstractVolatileAnimation {

    private static Texture uzi_hit_explosion_animation_texture;

    public UziHitExplosionAnimation(final int BUFFER_SIZE) {
        super(BUFFER_SIZE);
    }

    @Override
    public void initTexture() throws SlickException {
        if (uzi_hit_explosion_animation_texture == null)
            uzi_hit_explosion_animation_texture = new Image("assets/animations/bullet_explosion.png").getTexture();
    }

    @Override
    public void addNewInstance() {
        Image smoke_animation_image = new Image(uzi_hit_explosion_animation_texture);
        Animation explosion_animation = new Animation(false);
        int IMAGE_COUNT = 9;
        int x = 0;
        for (int idx = 0; idx < IMAGE_COUNT; ++idx) {
            explosion_animation.addFrame(smoke_animation_image.getSubImage(x, 0, 11, 28), 80);
            x += 11;
        }
        explosion_animation.setLooping(false);
        buffered_instances.add(new AnimationInstance(explosion_animation));
    }

}
