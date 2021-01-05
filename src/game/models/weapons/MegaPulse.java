package game.models.weapons;

import settings.UserSettings;
import game.models.weapons.projectiles.GroundBullet;
import game.models.weapons.projectiles.Projectile;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Vector2f;

public class MegaPulse extends PiercingWeapon {

    private boolean isDrivable;
    private static Sound mega_pulse_fire_sound;
    private final static float MEGA_PULSE_SPEED = 0.3f;

    public MegaPulse(boolean isDrivable) {
        super();
        this.isDrivable = isDrivable;
        try {
            projectile_texture = new Image("assets/bullets/mega_pulse.png").getTexture();

            if (mega_pulse_fire_sound == null) {
                mega_pulse_fire_sound = new Sound("audio/sounds/mega_pulse_shot.ogg");
            }
            fire_sound = mega_pulse_fire_sound;
        } catch (SlickException e) {
            e.printStackTrace();
        }
        // individual MegaPulse specs
        bullet_damage = 3000;
        shot_reload_time = 500;
        if (!isDrivable) shot_reload_time *= 11;
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

            Projectile bullet = new GroundBullet(bullet_spawn, bullet_dir, rotation_angle, projectile_texture);
            bullet.setProjectileSpeed(MEGA_PULSE_SPEED);    // give mega pulse its own individual speed (slower)
            projectile_list.add(bullet);
            // make a sound only if the enemy shoots it
            if (!isDrivable) mega_pulse_fire_sound.play(1.f, UserSettings.soundVolume);
        }
    }
}
