package game.graphics.animations.explosion;

import game.graphics.animations.AbstractVolatileAnimation;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;

public class UziFireShotAnimation extends AbstractVolatileAnimation {

    private static Texture uzi_fire_shot_animation_texture;

    public UziFireShotAnimation(int BUFFER_SIZE) {
        super(BUFFER_SIZE);
    }

    @Override
    public void initTexture() throws SlickException {
        if (uzi_fire_shot_animation_texture == null)
            uzi_fire_shot_animation_texture = new Image("assets/animations/uzi_fire.png").getTexture();
    }

    @Override
    public void addNewInstance() {
        Image fire_shot_animation_image = new Image(uzi_fire_shot_animation_texture);
        Animation fire_animation = new Animation(false);
        final int IMAGE_COUNT = 5, IMAGE_WIDTH = 10;
        int x = 0;
        for (int idx = 0; idx < IMAGE_COUNT; ++idx) {
            fire_animation.addFrame(fire_shot_animation_image.getSubImage(x, 0, IMAGE_WIDTH, 11), 50);
            x += IMAGE_WIDTH;
        }
        fire_animation.setLooping(false);
        buffered_instances.add(new AnimationInstance(fire_animation));
    }
}
