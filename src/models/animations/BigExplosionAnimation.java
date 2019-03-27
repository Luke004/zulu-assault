package models.animations;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import java.util.Random;

public class BigExplosionAnimation extends AbstractAnimation {
    public BigExplosionAnimation(final int BUFFER_SIZE) {
        super(BUFFER_SIZE);
    }

    @Override
    public void addNewInstance() throws SlickException {
        Image smoke_animation_image = new Image("assets/animations/big_explosion.png");
        Animation explosion_animation = new Animation(false);
        int IMAGE_COUNT = 4;
        int x = 0;

        for (int idx = 0; idx < IMAGE_COUNT; ++idx) {
            explosion_animation.addFrame(smoke_animation_image.getSubImage(x, 0, 40, 50), 160);
            x += 40;
        }
        explosion_animation.setLooping(false);
        buffered_instances.add(new MovableAnimationInstance(explosion_animation));
    }

    public void playTenTimes(float xPos, float yPos, float rotation) {
        final int PLAY_TIMES = 10;
        int counter = 0;
        do {
            super.play(xPos, yPos, rotation);
            counter ++;
        } while (counter < PLAY_TIMES);
    }

    private class MovableAnimationInstance extends AnimationInstance {
        private float xDir, yDir;
        private final float SPEED;
        private final Random random;

        public MovableAnimationInstance(Animation animation) {
            super(animation);
            random = new Random();
            SPEED = 0.05f;
        }

        @Override
        void setup(float xPos, float yPos, float rotation) {
            super.setup(xPos, yPos, rotation);
            float rotate_direction = random.nextInt(360);
            xDir = (float) Math.sin(rotate_direction * Math.PI / 180);
            yDir = (float) -Math.cos(rotate_direction * Math.PI / 180);
        }

        @Override
        public void update(int deltaTime) {
            super.update(deltaTime);
            xPos += xDir * SPEED * deltaTime;
            yPos += yDir * SPEED * deltaTime;
        }
    }
}
