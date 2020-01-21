package models.weapons;

import menus.UserSettings;
import models.weapons.projectiles.Bullet;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

public class Uzi extends Weapon {

    protected Animation fire_animation;
    protected float xPos, yPos;

    public Uzi(boolean isDrivable) {
        super();
        Image fire_animation_image;
        try {
            fire_sound = new Sound("audio/sounds/uzi_shot.ogg");
            weapon_hud_image = new Image("assets/hud/weapons/uzi.png");
            projectile_texture = new Image("assets/bullets/bullet_small.png").getTexture();
            fire_animation_image = new Image("assets/animations/bullet_fire.png");
            fire_animation = new Animation(false);
            int IMAGE_COUNT = 5;
            int x = 0;
            int idx;
            for (idx = 0; idx < IMAGE_COUNT; ++idx) {
                fire_animation.addFrame(fire_animation_image.getSubImage(x, 0, 10, 11), 50);
                x += 10;
            }
            fire_animation.setLooping(false);
        } catch (SlickException e) {
            e.printStackTrace();
        }
        // individual Uzi specs
        bullet_damage = 50;
        shot_reload_time = 300;
        if (!isDrivable) shot_reload_time *= 5;
    }

    @Override
    public void update(int deltaTime) {
        super.update(deltaTime);
        fire_animation.update(deltaTime);
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);

        if (!fire_animation.isStopped())
            fire_animation.draw(xPos, yPos);
    }

    @Override
    public void fire(float spawnX, float spawnY, float rotation_angle) {
        if (canFire()) {
            current_reload_time = 0;    // reset the reload time when a shot is fired
            Bullet bullet = addBullet(spawnX, spawnY, rotation_angle, 0.f);
            projectile_list.add(bullet);

            for (int idx = 0; idx < fire_animation.getFrameCount(); ++idx) {
                fire_animation.getImage(idx).setRotation(rotation_angle - 180);
            }
            fire_sound.play(1.f, UserSettings.SOUND_VOLUME);
        }
    }

    protected Bullet addBullet(float spawnX, float spawnY, float rotation_angle, float x_offset) {
        float m_spawn_x = spawnX + (float) (Math.cos(((rotation_angle) * Math.PI) / 180) * x_offset
                + -Math.sin(((rotation_angle) * Math.PI) / 180) * -30.f);
        float m_spawn_y = spawnY + (float) (Math.sin(((rotation_angle) * Math.PI) / 180) * x_offset
                + Math.cos(((rotation_angle) * Math.PI) / 180) * -30.f);

        Vector2f bullet_spawn = new Vector2f(m_spawn_x, m_spawn_y);

        float dirX = (float) Math.sin(rotation_angle * Math.PI / 180);
        float dirY = (float) -Math.cos(rotation_angle * Math.PI / 180);
        Vector2f bullet_dir = new Vector2f(dirX, dirY);

        xPos = bullet_spawn.x;
        yPos = bullet_spawn.y;

        fire_animation.setCurrentFrame(0);
        fire_animation.start();

        return new Bullet(bullet_spawn, bullet_dir, rotation_angle, projectile_texture);
    }
}
