package game.graphics.animations;

import org.newdawn.slick.Animation;
import org.newdawn.slick.SlickException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
A class for animations that only last for a few seconds
 */
public abstract class AbstractVolatileAnimation {
    private List<AnimationInstance> active_instances;
    protected List<AnimationInstance> buffered_instances;

    public AbstractVolatileAnimation(final int BUFFER_SIZE) {
        buffered_instances = new ArrayList<>();
        active_instances = new ArrayList<>();
        try {
            initTexture();
        } catch (SlickException e) {
            e.printStackTrace();
        }
        int times = 0;
        do {
            addNewInstance();
            times++;
        } while (times < BUFFER_SIZE);
    }

    public abstract void initTexture() throws SlickException;

    public abstract void addNewInstance();


    public void play(float xPos, float yPos, float rotation) {
        AnimationInstance instance = getNextAvailableInstance();
        instance.setup(xPos, yPos, rotation);
    }

    public void draw() {
        for (int idx = 0; idx < active_instances.size(); ++idx)
            active_instances.get(idx).draw();
    }

    public void update(int deltaTime) {
        for (int idx = 0; idx < active_instances.size(); ++idx)
            active_instances.get(idx).update(deltaTime);
    }

    private AnimationInstance getNextAvailableInstance() {
        AnimationInstance instance = null;
        try {
            instance = buffered_instances.get(0);
            active_instances.add(instance);
            buffered_instances.remove(instance);
        } catch (Exception e) {
            System.out.println("Instance size too low! Increase size of buffered_instances list!");
        }
        return instance;
    }

    private void putSmokeInstanceBackToBuffer(AnimationInstance instance) {
        buffered_instances.add(instance);
        active_instances.remove(instance);
    }

    public class AnimationInstance {
        float xPos, yPos;
        private Animation animation;

        public AnimationInstance(Animation animation) {
            this.animation = animation;
        }

        void setup(float xPos, float yPos, float rotation) {
            for (int idx = 0; idx < this.animation.getFrameCount(); ++idx) {
                this.animation.getImage(idx).setRotation(rotation - 90);
            }
            this.xPos = xPos - animation.getImage(0).getWidth() / 2.f;
            this.yPos = yPos - animation.getImage(0).getHeight() / 2.f;
            this.animation.setCurrentFrame(0);
            this.animation.start();
        }

        public void draw() {
            this.animation.draw(this.xPos, this.yPos);
        }

        public void update(int deltaTime) {
            this.animation.update(deltaTime);

            if (this.animation.isStopped()) {
                putSmokeInstanceBackToBuffer(this);
            }
        }
    }

    public class MovableAnimationInstance extends AnimationInstance {
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


