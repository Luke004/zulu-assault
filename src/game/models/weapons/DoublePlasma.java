package game.models.weapons;

import settings.UserSettings;
import game.models.weapons.projectiles.GroundBullet;
import org.newdawn.slick.geom.Vector2f;

public class DoublePlasma extends Plasma {
    private boolean switch_turret, isDrivable;


    public DoublePlasma(boolean isDrivable) {
        super(isDrivable);
        this.isDrivable = isDrivable;
        shot_reload_time = shot_reload_time / 2;
    }

    @Override
    public void fire(float spawnX, float spawnY, float rotation_angle) {
        if (canFire()) {
            current_reload_time = 0;    // reset the reload time when a shot is fired
            GroundBullet groundBullet;
            if (isDrivable) {
                if (switch_turret) {
                    switch_turret = false;
                    // right turret
                    groundBullet = addBullet(spawnX, spawnY, rotation_angle, false);
                } else {
                    switch_turret = true;
                    // left turret
                    groundBullet = addBullet(spawnX, spawnY, rotation_angle, true);
                }
                projectile_list.add(groundBullet);
            } else {    // is not drivable
                // left turret
                groundBullet = addBullet(spawnX, spawnY, rotation_angle, true);
                projectile_list.add(groundBullet);
                // right turret
                groundBullet = addBullet(spawnX, spawnY, rotation_angle, false);
                projectile_list.add(groundBullet);
            }
            fire_sound.play(1.f, UserSettings.soundVolume);
        }
    }

    private GroundBullet addBullet(float spawnX, float spawnY, float rotation_angle, boolean left) {
        float m_spawn_x = spawnX + (float) (Math.cos(((rotation_angle) * Math.PI) / 180) * (left ? -19.5f : 19.5f)
                + -Math.sin(((rotation_angle) * Math.PI) / 180) * -20.f);
        float m_spawn_y = spawnY + (float) (Math.sin(((rotation_angle) * Math.PI) / 180) * (left ? -19.5f : 19.5f)
                + Math.cos(((rotation_angle) * Math.PI) / 180) * -20.f);
        Vector2f bullet_spawn = new Vector2f(m_spawn_x, m_spawn_y);

        float xVal = (float) Math.sin(rotation_angle * Math.PI / 180);
        float yVal = (float) -Math.cos(rotation_angle * Math.PI / 180);
        Vector2f bullet_dir = new Vector2f(xVal, yVal);

        return new GroundBullet(bullet_spawn, bullet_dir, rotation_angle, projectile_texture);
    }
}
