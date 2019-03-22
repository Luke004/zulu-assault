package models.weapons;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class MachineGun extends Weapon {
    private boolean switch_bullet_spawn_side;

    public MachineGun() {
        super();
        try {
            bullet_texture = new Image("assets/bullets/bullet_small.png").getTexture();
        } catch (SlickException e) {
            e.printStackTrace();
        }
        // individual MachineGun specs
        bullet_damage = 50;
        bullet_speed = 0.8f;
        shot_reload_time = 300;
    }

    @Override
    public void fire(float spawnX, float spawnY, float rotation_angle) {
        if (canFire()) {
            current_reload_time = 0;    // reset the reload time when a shot is fired
            if (switch_bullet_spawn_side) {
                spawnX += (float) (Math.cos(((rotation_angle) * Math.PI) / 180) * 4.f
                        + -Math.sin(((rotation_angle) * Math.PI) / 180) * -30.f);
                spawnY += (float) (Math.sin(((rotation_angle) * Math.PI) / 180) * 4.f
                        + Math.cos(((rotation_angle) * Math.PI) / 180) * -30.f);
                switch_bullet_spawn_side = false;
            } else {
                spawnX += (float) (Math.cos(((rotation_angle) * Math.PI) / 180) * -4.f
                        + -Math.sin(((rotation_angle) * Math.PI) / 180) * -30.f);
                spawnY += (float) (Math.sin(((rotation_angle) * Math.PI) / 180) * -4.f
                        + Math.cos(((rotation_angle) * Math.PI) / 180) * -30.f);
                switch_bullet_spawn_side = true;
            }
            Vector2f bullet_spawn = new Vector2f(spawnX, spawnY);

            float dirX = (float) Math.sin(rotation_angle * Math.PI / 180);
            float dirY = (float) -Math.cos(rotation_angle * Math.PI / 180);
            Vector2f bullet_dir = new Vector2f(dirX, dirY);

            Bullet bullet = new Bullet(bullet_spawn, bullet_dir, rotation_angle);
            bullet_list.add(bullet);
        }
    }
}
