package game.models.weapons;

import settings.UserSettings;
import game.models.weapons.projectiles.Projectile;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;

import java.util.Random;

public class Cannon extends Uzi {

    private boolean switch_bullet_spawn_side, isDrivable;
    private static final float MIN_BULLET_SPREAD = -3.f, MAX_BULLET_SPREAD = 3.f;
    private static Texture cannon_hud_texture;
    private static Random random;
    private final int SIDE_OFFSET;

    public Cannon(boolean isDrivable, final int SIDE_OFFSET) {
        super(isDrivable);
        this.SIDE_OFFSET = SIDE_OFFSET;
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

    static {
        random = new Random();
    }

    @Override
    public void fire(float spawnX, float spawnY, float rotation_angle) {
        if (canFire()) {
            current_reload_time = 0;    // reset the reload time when a shot is fired
            Projectile bullet;
            if (isDrivable) {
                float bullet_spread = random.nextFloat() * (MAX_BULLET_SPREAD - MIN_BULLET_SPREAD) + MIN_BULLET_SPREAD;
                if (switch_bullet_spawn_side) {
                    bullet = addBullet(spawnX, spawnY, rotation_angle + bullet_spread, -SIDE_OFFSET);
                    switch_bullet_spawn_side = false;
                } else {
                    bullet = addBullet(spawnX, spawnY, rotation_angle + bullet_spread, SIDE_OFFSET);
                    switch_bullet_spawn_side = true;
                }
            } else {    // is not drivable
                bullet = addBullet(spawnX, spawnY, rotation_angle, -SIDE_OFFSET);
                projectile_list.add(bullet);
                // right turret
                bullet = addBullet(spawnX, spawnY, rotation_angle, SIDE_OFFSET);
            }
            projectile_list.add(bullet);

            fire_sound.play(1.f, UserSettings.soundVolume);
        }
    }
}
