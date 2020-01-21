package models.weapons;

import menus.UserSettings;
import models.weapons.projectiles.Bullet;
import models.weapons.projectiles.Projectile;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class DoubleShell extends Shell {

    public DoubleShell(boolean isDrivable) {
        super(isDrivable);
        smokeAnimation.addNewInstance();   // add another instance
    }

    @Override
    public void fire(float spawnX, float spawnY, float rotation_angle) {
        if (canFire()) {
            current_reload_time = 0;    // reset the reload time when a shot is fired

            // first bullet (right turret)
            Projectile bullet = addBullet(spawnX, spawnY, rotation_angle, 19.5f);
            projectile_list.add(bullet);

            // second bullet (left turret)
            bullet = addBullet(spawnX, spawnY, rotation_angle, -19.5f);
            projectile_list.add(bullet);

            fire_sound.play(1.f, UserSettings.SOUND_VOLUME);
        }
    }
}
