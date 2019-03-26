package models.animations;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class UziHitExplosionAnimation extends AbstractAnimation {

    public UziHitExplosionAnimation(final int BUFFER_SIZE) {
        super(BUFFER_SIZE);
    }

    @Override
    public void addNewInstance() throws SlickException {
        Image smoke_animation_image = new Image("assets/animations/bullet_explosion.png");
        Animation explosion_animation = new Animation(false);
        int IMAGE_COUNT = 9;
        int x = 0;
        for (int idx = 0; idx < IMAGE_COUNT; ++idx) {
            explosion_animation.addFrame(smoke_animation_image.getSubImage(x, 0, 15, 19), 80);
            x += 15;
        }
        explosion_animation.setLooping(false);
        buffered_instances.add(new AnimationInstance(explosion_animation));
    }

}
