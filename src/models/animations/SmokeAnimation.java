package models.animations;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class SmokeAnimation {
    private List<SmokeInstance> active_smoke_instances;
    private List<SmokeInstance> buffered_smoke_instances;
    private Animation smoke_animation;
    private final int SMOKE_WIDTH_HALF, SMOKE_HEIGHT_HALF;

    public SmokeAnimation(final int BUFFER_SIZE) {
        buffered_smoke_instances = new ArrayList<>();
        active_smoke_instances = new ArrayList<>();
        smoke_animation = null;
        try {
            Image smoke_animation_image = new Image("assets/animations/smoke.png");
            smoke_animation = new Animation(false);
            int IMAGE_COUNT = 8;
            int x = 0;
            for (int idx = 0; idx < IMAGE_COUNT; ++idx) {
                smoke_animation.addFrame(smoke_animation_image.getSubImage(x, 0, 40, 34), 80);
                x += 40;
            }
            smoke_animation.setLooping(false);

            // create instances of 'SmokeAnimation'
            buffered_smoke_instances.add(new SmokeInstance(smoke_animation));
            for (int idx = 0; idx < BUFFER_SIZE - 1; ++idx) {
                buffered_smoke_instances.add(new SmokeInstance(smoke_animation.copy()));
            }
        } catch (SlickException e) {
            e.printStackTrace();
        }
        SMOKE_WIDTH_HALF = smoke_animation.getImage(0).getWidth() / 2;
        SMOKE_HEIGHT_HALF = smoke_animation.getImage(0).getHeight() / 2;
    }

    public void play(Vector2f position, float rotation) {
        SmokeInstance smokeInstance = getNextFreshSmokeInstance();
        smokeInstance.setup(position, rotation);
    }

    public void draw() {
        for (int idx = 0; idx < active_smoke_instances.size(); ++idx)
            active_smoke_instances.get(idx).draw();
    }

    public void update(int deltaTime) {
        for (int idx = 0; idx < active_smoke_instances.size(); ++idx)
            active_smoke_instances.get(idx).update(deltaTime);
    }

    private SmokeInstance getNextFreshSmokeInstance() {
        if (buffered_smoke_instances.size() == 0) {
            // just in case the instances aren't enough during runtime, add another one ad hoc
            SmokeInstance newSmokeInstance = new SmokeInstance(smoke_animation.copy());
            active_smoke_instances.add(newSmokeInstance);
            return newSmokeInstance;
        }
        SmokeInstance smokeInstance = buffered_smoke_instances.get(0);
        active_smoke_instances.add(smokeInstance);
        buffered_smoke_instances.remove(smokeInstance);
        return smokeInstance;
    }

    private void putSmokeInstanceBackToBuffer(SmokeInstance instance) {
        buffered_smoke_instances.add(instance);
        active_smoke_instances.remove(instance);
    }

    private class SmokeInstance {
        float xPos, yPos;
        private Animation animation;

        SmokeInstance(Animation animation) {
            this.animation = animation;
        }

        void setup(Vector2f position, float rotation) {
            for (int idx = 0; idx < animation.getFrameCount(); ++idx) {
                this.animation.getImage(idx).setRotation(rotation - 90);
            }
            this.xPos = position.x - SMOKE_WIDTH_HALF;
            this.yPos = position.y - SMOKE_HEIGHT_HALF;
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
