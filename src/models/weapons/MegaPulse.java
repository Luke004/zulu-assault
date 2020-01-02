package models.weapons;

import models.weapons.projectiles.Bullet;
import models.weapons.projectiles.Projectile;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class MegaPulse extends PiercingWeapon {

    private final static float MEGA_PULSE_SPEED = 0.5f;

    public MegaPulse() {
        super();
        try {
            projectile_texture = new Image("assets/bullets/mega_pulse.png").getTexture();
        } catch (SlickException e) {
            e.printStackTrace();
        }
        // individual MegaPulse specs
        bullet_damage = 3000;
        shot_reload_time = 500;
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

            Projectile bullet = new Bullet(bullet_spawn, bullet_dir, rotation_angle, projectile_texture);
            bullet.setProjectileSpeed(MEGA_PULSE_SPEED);    // give mega pulse its own individual speed (slower)
            projectile_list.add(bullet);
        }
    }
}
