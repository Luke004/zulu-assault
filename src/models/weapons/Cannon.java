package models.weapons;

import menus.UserSettings;
import models.weapons.projectiles.Projectile;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Cannon extends Uzi {
    private boolean switch_bullet_spawn_side, isDrivable;

    public Cannon(boolean isDrivable) {
        super(isDrivable);
        this.isDrivable = isDrivable;

        try {
            weapon_hud_image = new Image("assets/hud/weapons/cannon.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void fire(float spawnX, float spawnY, float rotation_angle) {
        if (canFire()) {
            current_reload_time = 0;    // reset the reload time when a shot is fired
            Projectile bullet;
            if (isDrivable) {
                if (switch_bullet_spawn_side) {
                    bullet = addBullet(spawnX, spawnY, rotation_angle, -4.f);
                    switch_bullet_spawn_side = false;
                } else {
                    bullet = addBullet(spawnX, spawnY, rotation_angle, 4.f);
                    switch_bullet_spawn_side = true;
                }
                projectile_list.add(bullet);
            } else {    // is not drivable
                bullet = addBullet(spawnX, spawnY, rotation_angle, -4.f);
                projectile_list.add(bullet);
                // right turret
                bullet = addBullet(spawnX, spawnY, rotation_angle, 4.f);
                projectile_list.add(bullet);
            }

            for (int idx = 0; idx < fire_animation.getFrameCount(); ++idx) {
                fire_animation.getImage(idx).setRotation(rotation_angle - 180);
            }

            fire_sound.play(1.f, UserSettings.SOUND_VOLUME);
        }
    }
}
