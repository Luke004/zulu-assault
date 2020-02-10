package models.weapons;

import menus.UserSettings;
import models.weapons.projectiles.Projectile;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;

public class Cannon extends Uzi {
    private boolean switch_bullet_spawn_side, isDrivable;

    private static Texture cannon_hud_texture;

    public Cannon(boolean isDrivable) {
        super(isDrivable);
        uziFireShotAnimation.addNewInstance();   // add another instance
        this.isDrivable = isDrivable;

        try {
            if (cannon_hud_texture == null)
                cannon_hud_texture = new Image("assets/hud/weapons/cannon.png").getTexture();
            if (isDrivable) weapon_hud_image = new Image(cannon_hud_texture);
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

            fire_sound.play(1.f, UserSettings.SOUND_VOLUME);
        }
    }
}
