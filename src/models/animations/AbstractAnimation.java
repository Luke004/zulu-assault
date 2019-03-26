package models.animations;

import org.newdawn.slick.Animation;
import org.newdawn.slick.SlickException;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAnimation {
    private List<AnimationInstance> active_instances;
    protected List<AnimationInstance> buffered_instances;

    public AbstractAnimation(final int BUFFER_SIZE) {
        buffered_instances = new ArrayList<>();
        active_instances = new ArrayList<>();
        try {
            int times = 0;
            do {
                addNewInstance();
                times++;
            } while (times < BUFFER_SIZE);
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public abstract void addNewInstance() throws SlickException;


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
        /*
        if (buffered_instances.size() == 0) {
            // just in case the instances aren't enough during runtime, add another one ad hoc
            SmokeInstance newSmokeInstance = new SmokeInstance(smoke_animation.copy());
            active_instances.add(newSmokeInstance);
            return newSmokeInstance;
        }
        */
        AnimationInstance instance = buffered_instances.get(0);
        active_instances.add(instance);
        buffered_instances.remove(instance);
        return instance;
    }

    private void putSmokeInstanceBackToBuffer(AnimationInstance instance) {
        buffered_instances.add(instance);
        active_instances.remove(instance);
    }

    public class AnimationInstance {
        float xPos, yPos;
        public Animation animation;

        public AnimationInstance(Animation animation) {
            this.animation = animation;
        }

        void setup(float xPos, float yPos, float rotation) {
            for (int idx = 0; idx < this.animation.getFrameCount(); ++idx) {
                this.animation.getImage(idx).setRotation(rotation - 90);
            }
            this.xPos = xPos - animation.getImage(0).getWidth() / 2;
            this.yPos = yPos - animation.getImage(0).getHeight() / 2;
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


}


