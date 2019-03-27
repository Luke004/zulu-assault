package models.weapons;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Napalm extends PiercingWeapon {
    private Random random;

    public Napalm() {
        super();
        random = new Random();
        try {
            bullet_texture = new Image("assets/animations/big_explosion.png").getTexture();
        } catch (SlickException e) {
            e.printStackTrace();
        }
        // individual Napalm specs
        bullet_damage = 90;
        bullet_speed = 0.5f;
        shot_reload_time = 200;
        MAX_BULLET_LIFETIME = 400;
    }


    @Override
    public void fire(float spawnX, float spawnY, float rotation_angle) {
        if (canFire()) {
            clearHitList();
            current_reload_time = 0;    // reset the reload time when a shot is fired
            spawnX += -Math.sin(((rotation_angle) * Math.PI) / 180) * -30.f;
            spawnY += Math.cos(((rotation_angle) * Math.PI) / 180) * -30.f;
            Vector2f bullet_spawn = new Vector2f(spawnX, spawnY);

            float xVal = (float) Math.sin(rotation_angle * Math.PI / 180);
            float yVal = (float) -Math.cos(rotation_angle * Math.PI / 180);
            Vector2f bullet_dir = new Vector2f(xVal, yVal);

            Bullet bullet = new Flame(bullet_spawn, bullet_dir, random.nextInt(360));
            bullet_list.add(bullet);
        }
    }


    private class Flame extends Bullet {
        List<Animation> buffered_explosions;
        List<Animation> active_explosions;
        Animation starting_flame;
        List<FlameInstance> explosion_animations;
        private final float ANIMATION_WIDTH_HALF, ANIMATION_HEIGHT_HALF;
        private final int CREATE_NEW_FLAME_TIMER = MAX_BULLET_LIFETIME / 5;
        private int time = 0;

        Flame(Vector2f startPos, Vector2f dir, float rotation) {
            super(startPos, dir, rotation);
            explosion_animations = new ArrayList<>();
            buffered_explosions = new ArrayList<>();
            active_explosions = new ArrayList<>();
            final int FLAMES_TO_CREATE = MAX_BULLET_LIFETIME / CREATE_NEW_FLAME_TIMER;
            int counter = 0;
            do {
                buffered_explosions.add(createNewExplosionInstance());
                counter++;
            } while (counter < FLAMES_TO_CREATE);

            starting_flame = createNewExplosionInstance();
            ANIMATION_HEIGHT_HALF = starting_flame.getCurrentFrame().getHeight() / 2;
            ANIMATION_WIDTH_HALF = starting_flame.getCurrentFrame().getWidth() / 2;
        }

        private Animation createNewExplosionInstance() {
            Image explosion_animation_image = null;
            try {
                explosion_animation_image = new Image("assets/animations/big_explosion.png");
            } catch (SlickException e) {
                e.printStackTrace();
            }
            int IMAGE_COUNT = 4;
            int x = 0;
            Animation explosion_animation = new Animation(false);
            for (int idx = 0; idx < IMAGE_COUNT; ++idx) {
                explosion_animation.addFrame(explosion_animation_image.getSubImage(x, 0, 40, 50), 100);
                x += 40;
            }
            explosion_animation.setLooping(false);
            return explosion_animation;
        }

        private Animation getNextFreshExplosionInstance() {
            Animation explosion = null;
            try {
                explosion = buffered_explosions.get(0);
                active_explosions.add(explosion);
                buffered_explosions.remove(explosion);
            } catch (Exception e) {
                System.out.println("Buffered rockets size too low! Increase size of buffered_rockets list!");
            }
            return explosion;
        }

        private void putExplosionInstanceBackToBuffer(Animation instance) {
            buffered_explosions.add(instance);
            active_explosions.remove(instance);
        }

        @Override
        public void update(int deltaTime) {
            super.update(deltaTime);
            starting_flame.update(deltaTime);
            time += deltaTime;

            if (time > CREATE_NEW_FLAME_TIMER) {
                time = 0;
                FlameInstance flameInstance = new FlameInstance(getNextFreshExplosionInstance(), bullet_pos.x, bullet_pos.y);
                flameInstance.setup();
                explosion_animations.add(flameInstance);
            }

            for (FlameInstance explosion_animation : explosion_animations)
                explosion_animation.update(deltaTime);
        }

        @Override
        public void draw(Graphics graphics) {
            for (FlameInstance explosion_animation : explosion_animations)
                explosion_animation.draw();

            starting_flame.draw(bullet_pos.x - ANIMATION_WIDTH_HALF, bullet_pos.y - ANIMATION_HEIGHT_HALF);
        }

        private class FlameInstance {
            Animation flame_animation;
            float xPos, yPos;

            FlameInstance(Animation flame_animation, float xPos, float yPos) {
                this.flame_animation = flame_animation;
                this.xPos = xPos;
                this.yPos = yPos;
            }

            public void update(int deltaTime) {
                flame_animation.update(deltaTime);

                if (flame_animation.isStopped()) {
                    putExplosionInstanceBackToBuffer(flame_animation);
                }
            }

            public void draw() {
                flame_animation.draw(xPos - ANIMATION_WIDTH_HALF, yPos - ANIMATION_HEIGHT_HALF);
            }

            public void setup() {
                flame_animation.setCurrentFrame(0);
                flame_animation.start();
                int rotation = random.nextInt(360);
                for (int idx = 0; idx < flame_animation.getFrameCount(); ++idx) {
                    flame_animation.getImage(idx).setRotation(rotation);
                }
            }
        }
    }
}
