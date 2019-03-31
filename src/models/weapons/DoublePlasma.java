package models.weapons;

import org.newdawn.slick.geom.Vector2f;

public class DoublePlasma extends Plasma {
    private boolean switch_turret;

    public DoublePlasma(boolean isDrivable) {
        super(isDrivable);
        shot_reload_time = shot_reload_time / 2;
    }

    @Override
    public void fire(float spawnX, float spawnY, float rotation_angle) {
        if (canFire()) {
            current_reload_time = 0;    // reset the reload time when a shot is fired

            float xVal = (float) Math.sin(rotation_angle * Math.PI / 180);
            float yVal = (float) -Math.cos(rotation_angle * Math.PI / 180);
            Vector2f bullet_dir = new Vector2f(xVal, yVal);

            Vector2f bullet_spawn;
            Weapon.Bullet bullet;
            if (switch_turret) {
                switch_turret = false;
                // right turret
                spawnX += (Math.cos(((rotation_angle) * Math.PI) / 180) * 19.5f
                        + -Math.sin(((rotation_angle) * Math.PI) / 180) * -30.f);
                spawnY += (Math.sin(((rotation_angle) * Math.PI) / 180) * 19.5f
                        + Math.cos(((rotation_angle) * Math.PI) / 180) * -30.f);
                bullet_spawn = new Vector2f(spawnX, spawnY);
                bullet = new Weapon.Bullet(bullet_spawn, bullet_dir, rotation_angle);
            } else {
                switch_turret = true;
                // left turret
                spawnX += (Math.cos(((rotation_angle) * Math.PI) / 180) * -19.5f
                        + -Math.sin(((rotation_angle) * Math.PI) / 180) * -30.f);
                spawnY += (Math.sin(((rotation_angle) * Math.PI) / 180) * -19.5f
                        + Math.cos(((rotation_angle) * Math.PI) / 180) * -30.f);

                bullet_spawn = new Vector2f(spawnX, spawnY);
                bullet = new Weapon.Bullet(bullet_spawn, bullet_dir, rotation_angle);

            }
            bullet_list.add(bullet);
        }
    }
}
