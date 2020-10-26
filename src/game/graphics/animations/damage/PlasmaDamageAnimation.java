package game.graphics.animations.damage;

import game.graphics.animations.AbstractVolatileAnimation;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;

public class PlasmaDamageAnimation extends AbstractVolatileAnimation {

    private static Texture plasma_damage_animation_texture;

    public PlasmaDamageAnimation(final int BUFFER_SIZE) {
        super(BUFFER_SIZE);
    }

    @Override
    public void initTexture() throws SlickException {
        if (plasma_damage_animation_texture == null)
            plasma_damage_animation_texture = new Image("assets/animations/plasma_hit.png").getTexture();
    }

    @Override
    public void addNewInstance() {
        Image plasma_hit_animation_image = new Image(plasma_damage_animation_texture);
        Animation plasma_hit_animation = new Animation(false);
        int IMAGE_COUNT = 5;
        int x = 0;
        for (int idx = 0; idx < IMAGE_COUNT; ++idx) {
            plasma_hit_animation.addFrame(plasma_hit_animation_image.getSubImage(x, 0, 50, 49), 120);
            x += 50;
        }
        plasma_hit_animation.setLooping(false);
        buffered_instances.add(new MovableAnimationInstance(plasma_hit_animation));
    }
}
