package game.graphics.animations.smoke;

import game.graphics.animations.AbstractVolatileAnimation;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;

public class SmokeAnimation extends AbstractVolatileAnimation {

    private static Texture smoke_animation_texture;

    public SmokeAnimation(final int BUFFER_SIZE) {
        super(BUFFER_SIZE);
    }

    @Override
    public void initTexture() throws SlickException {
        if (smoke_animation_texture == null)
            smoke_animation_texture = new Image("assets/animations/smoke.png").getTexture();
    }

    @Override
    public void addNewInstance() {
        Image smoke_animation_image = new Image(smoke_animation_texture);
        Animation smoke_animation = new Animation(false);
        int IMAGE_COUNT = 8;
        int x = 0;
        for (int idx = 0; idx < IMAGE_COUNT; ++idx) {
            smoke_animation.addFrame(smoke_animation_image.getSubImage(x, 0, 40, 34), 80);
            x += 40;
        }
        smoke_animation.setLooping(false);
        buffered_instances.add(new AnimationInstance(smoke_animation));
    }
}
