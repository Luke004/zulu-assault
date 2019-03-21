package models.weapons;

import models.CollisionModel;
import org.lwjgl.Sys;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RocketLauncher extends Weapon {
    private List<Animation> active_rockets;
    private List<Animation> buffered_rockets;
    private int recollect_timer;

    public RocketLauncher() {
        super();

        // individual RocketLauncher specs
        bullet_damage = 10.f;
        bullet_speed = 0.5f;
        shot_reload_time = 800;

        Image rocket_animation_image;
        try {
            bullet_texture = new Image("assets/bullets/shell.png").getTexture();
            rocket_animation_image = new Image("assets/bullets/rocket_animation.png");
            Animation rocket_animation = new Animation(false);
            int[] y = {17, 32, 44, 57, 71, 87, 103, 123};
            int x = 0;
            int idx;
            for (idx = 0; idx < y.length; ++idx) {
                rocket_animation.addFrame(rocket_animation_image.getSubImage(x, 0, 20, y[idx]), 100);
                x += 20;
            }
            final int BUFFER_SIZE = 5;
            buffered_rockets = new ArrayList<>();
            for (idx = 0; idx < BUFFER_SIZE; ++idx) {
                buffered_rockets.add(rocket_animation);
            }
            active_rockets = new ArrayList<>();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public void update(int deltaTime) {
        super.update(deltaTime);
        if(active_rockets.size() == 0) return;
        this.recollect_timer += deltaTime;
        if (recollect_timer > MAX_BULLET_LIFETIME) {
            putRocketBackToBuffer();
        }
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
            fresh_rocket.start();
            fresh_rocket.stopAt(7);

            Bullet bullet = new Rocket(bullet_spawn, bullet_dir, rotation_angle, fresh_rocket);
            bullet_list.add(bullet);
        }
    }

    private Animation getNextFreshRocket() {
        if (buffered_rockets.get(0) != null) {
            Animation rocket = buffered_rockets.get(0);
            active_rockets.add(rocket);
            buffered_rockets.remove(rocket);
            return rocket;
        } else throw new IllegalAccessError("Not enough buffered rockets in buffered_rockets list!");
    }

    private void putRocketBackToBuffer() {
        Animation rocket = active_rockets.get(active_rockets.size() - 1);
        if (rocket != null) {
            buffered_rockets.add(rocket);
            active_rockets.remove(rocket);
        }
    }

    public class Rocket extends Bullet {
        private Animation rocket_animation;
        private float xVal, yVal, rotation;

        public Rocket(Vector2f startPos, Vector2f dir, float rotation, Animation rocket_animation) {
            super(startPos, dir, rotation);
            this.rocket_animation = rocket_animation;

            this.rotation = rotation;

            final float DISTANCE = 10;
            final float SPAWN_X = 0;
            xVal = (float) (Math.cos(((rotation) * Math.PI) / 180) * SPAWN_X
                    + -Math.sin(((rotation) * Math.PI) / 180) * DISTANCE);
            yVal = (float) (Math.sin(((rotation) * Math.PI) / 180) * SPAWN_X
                    + Math.cos(((rotation) * Math.PI) / 180) * DISTANCE);
        }

        @Override
        public void update(int deltaTime) {
            super.update(deltaTime);
            rocket_animation.update(deltaTime);
            rocket_animation.getCurrentFrame().setRotation(rotation);
        }

        @Override
        public void draw(Graphics graphics) {
            super.draw(graphics);

            rocket_animation.draw(bullet_pos.x - xVal, bullet_pos.y - yVal);
        }

    }

}
