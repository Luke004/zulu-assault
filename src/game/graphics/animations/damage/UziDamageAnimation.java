package game.graphics.animations.damage;

import game.graphics.animations.AbstractVolatileAnimation;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;

public class UziDamageAnimation extends AbstractVolatileAnimation {

    private static Texture uzi_damage_animation_texture;

    public UziDamageAnimation(final int BUFFER_SIZE) {
        super(BUFFER_SIZE);
    }

    @Override
    public void initTexture() throws SlickException {
        if (uzi_damage_animation_texture == null)
            uzi_damage_animation_texture = new Image("assets/animations/bullet_damage.png").getTexture();
    }

    @Override
    public void addNewInstance() {
        Image smoke_animation_image = new Image(uzi_damage_animation_texture);
        Animation damage_animation = new Animation(false);
        int IMAGE_COUNT = 17;
        int x = 0;
        for (int idx = 0; idx < IMAGE_COUNT; ++idx) {
            damage_animation.addFrame(smoke_animation_image.getSubImage(x, 0, 13, 89), 35);
            x += 13;
        }
        damage_animation.setLooping(false);
        buffered_instances.add(new AnimationInstance(damage_animation));
    }

    @Override
    public void play(float xPos, float yPos, float rotation) {
        final float Y_OFFSET = -50; // so it looks like it continues the bullet hit
        float xVal = (float) Math.cos(((rotation) * Math.PI) / 180) * Y_OFFSET;
        float yVal = (float) Math.sin(((rotation) * Math.PI) / 180) * Y_OFFSET;

        super.play(xPos - xVal, yPos - yVal, rotation);
    }
}
