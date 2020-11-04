package game.models.weapons;

import game.models.weapons.projectiles.GroundBullet;
import game.models.weapons.projectiles.Projectile;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;
import settings.UserSettings;

public class Laser extends PiercingWeapon {

    private static Sound laser_fire_sound;
    private static Texture laser_hud_texture;
    private static Texture laser_bullet_texture;
    protected final static float LASER_SPEED = 1.1f;
    protected final static int LASER_LIFETIME_MILLIS = 500;

    public Laser(boolean isDrivable) {
        super();
        try {
            if (laser_fire_sound == null) {
                laser_fire_sound = new Sound("audio/sounds/laser.ogg");
            }
            fire_sound = laser_fire_sound;

            if (laser_hud_texture == null)
                laser_hud_texture = new Image("assets/hud/weapons/laser.png").getTexture();
            if (isDrivable) weapon_hud_image = new Image(laser_hud_texture);

            if (laser_bullet_texture == null)
                laser_bullet_texture = new Image("assets/bullets/laser.png").getTexture();
            projectile_texture = laser_bullet_texture;
        } catch (SlickException e) {
            e.printStackTrace();
        }
        // individual Laser specs
        bullet_damage = 50;
        shot_reload_time = 300;

        if (!isDrivable) shot_reload_time *= 5;
    }

    @Override
    public void fire(float spawnX, float spawnY, float rotation_angle) {
        if (canFire()) {
            clearHitList();
            current_reload_time = 0;    // reset the reload time when a shot is fired

            Projectile bullet = addBullet(spawnX, spawnY, rotation_angle, 0.f);
            bullet.setProjectileSpeed(LASER_SPEED);
            bullet.projectile_max_lifetime = LASER_LIFETIME_MILLIS;
            projectile_list.add(bullet);

            fire_sound.play(1.f, UserSettings.soundVolume);
        }
    }

    protected GroundBullet addBullet(float spawnX, float spawnY, float rotation_angle, float x_offset) {
        float m_spawn_x = spawnX + (float) (Math.cos(((rotation_angle) * Math.PI) / 180) * x_offset
                + -Math.sin(((rotation_angle) * Math.PI) / 180) * -30.f);
        float m_spawn_y = spawnY + (float) (Math.sin(((rotation_angle) * Math.PI) / 180) * x_offset
                + Math.cos(((rotation_angle) * Math.PI) / 180) * -30.f);

        Vector2f bullet_spawn = new Vector2f(m_spawn_x, m_spawn_y);

        float dirX = (float) Math.sin(rotation_angle * Math.PI / 180);
        float dirY = (float) -Math.cos(rotation_angle * Math.PI / 180);
        Vector2f bullet_dir = new Vector2f(dirX, dirY);

        return new GroundBullet(bullet_spawn, bullet_dir, rotation_angle, projectile_texture);
    }
}
