package models.weapons;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RocketLauncher extends Weapon {
    private List<Animation> active_rockets;
    private List<Animation> buffered_rockets;

    public RocketLauncher(boolean isDrivable) {
        super();

        // individual RocketLauncher specs
        bullet_damage = 500;
        bullet_speed = 0.5f;
        shot_reload_time = 800;
        if (!isDrivable) shot_reload_time *= 5;

        try {
            bullet_texture = new Image("assets/bullets/shell.png").getTexture();
            final int BUFFER_SIZE;
            if (!isDrivable) {
                BUFFER_SIZE = 3;
            } else {
                BUFFER_SIZE = 5;    // player needs more buffer_size, since he can shoot more often
            }
            buffered_rockets = new ArrayList<>();
            int IMAGE_COUNT = 8;
            int x;
            int idx;
            for (idx = 0; idx < BUFFER_SIZE; ++idx) {
                x = 0;
                Image rocket_animation_image = new Image("assets/bullets/rocket_animation.png");
                Animation rocket_animation = new Animation(false);
                for (int idx2 = 0; idx2 < IMAGE_COUNT; ++idx2) {
                    rocket_animation.addFrame(rocket_animation_image.getSubImage(x, 0, 20, 123), 200);
                    x += 20;
                }
                buffered_rockets.add(rocket_animation);
            }
            active_rockets = new ArrayList<>();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public void update(int deltaTime) {
        super.update(deltaTime);
        if (active_rockets.size() == 0) return;
        putRocketBackToBuffer();
    }


    @Override
    public void fire(float spawnX, float spawnY, float rotation_angle) {
        if (canFire()) {
            current_reload_time = 0;    // reset the reload time when a shot is fired
            spawnX += -Math.sin(((rotation_angle) * Math.PI) / 180) * -30.f;
            spawnY += Math.cos(((rotation_angle) * Math.PI) / 180) * -30.f;
            Vector2f bullet_spawn = new Vector2f(spawnX, spawnY);

            float xVal = (float) Math.sin(rotation_angle * Math.PI / 180);
            float yVal = (float) -Math.cos(rotation_angle * Math.PI / 180);
            Vector2f bullet_dir = new Vector2f(xVal, yVal);

            Animation fresh_rocket = getNextFreshRocket();

            for (int idx = 0; idx < fresh_rocket.getFrameCount(); ++idx) {
                fresh_rocket.getImage(idx).setRotation(rotation_angle);
            }
            fresh_rocket.setCurrentFrame(0);
            fresh_rocket.stopAt(7);
            fresh_rocket.start();


            Bullet bullet = new Rocket(bullet_spawn, bullet_dir, rotation_angle, fresh_rocket);
            bullet_list.add(bullet);
        }
    }

    private Animation getNextFreshRocket() {
        Animation rocket = null;
        try {
            rocket = buffered_rockets.get(0);
            active_rockets.add(rocket);
            buffered_rockets.remove(rocket);
        } catch (Exception e) {
            System.out.println("Buffered rockets size too low! Increase size of buffered_rockets list!");
        }
        return rocket;
    }

    private void putRocketBackToBuffer() {
        Animation rocket = active_rockets.get(active_rockets.size() - 1);
        buffered_rockets.add(rocket);
        active_rockets.remove(rocket);
    }

    public class Rocket extends Bullet {
        private Animation rocket_animation;
        private final int ANIMATION_WIDTH_HALF, ANIMATION_HEIGHT_HALF;
        private float xVal, yVal;

        public Rocket(Vector2f startPos, Vector2f dir, float rotation, Animation rocket_animation) {
            super(startPos, dir, rotation);
            this.rocket_animation = rocket_animation;
            ANIMATION_WIDTH_HALF = rocket_animation.getCurrentFrame().getWidth() / 2;
            ANIMATION_HEIGHT_HALF = rocket_animation.getCurrentFrame().getHeight() / 2;

            // calculate x and y to set rocket behind the bullet
            final float DISTANCE = -70;
            final float SPAWN_X = -3;
            xVal = (float) (Math.cos(((rotation) * Math.PI) / 180) * SPAWN_X
                    + -Math.sin(((rotation) * Math.PI) / 180) * DISTANCE);
            yVal = (float) (Math.sin(((rotation) * Math.PI) / 180) * SPAWN_X
                    + Math.cos(((rotation) * Math.PI) / 180) * DISTANCE);
        }

        @Override
        public void update(int deltaTime) {
            super.update(deltaTime);
            rocket_animation.update(deltaTime);

        }

        @Override
        public void draw(Graphics graphics) {
            super.draw(graphics);
            rocket_animation.draw(bullet_pos.x - ANIMATION_WIDTH_HALF - xVal, bullet_pos.y - ANIMATION_HEIGHT_HALF - yVal);
        }
    }
}
