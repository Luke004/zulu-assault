package game.models.weapons;

import settings.UserSettings;
import game.models.weapons.projectiles.Projectile;

import java.util.Random;

public class DoubleRocketLauncher extends RocketLauncher {

    private static final float MIN_BULLET_SPREAD = -3.f, MAX_BULLET_SPREAD = 3.f;
    private static Random random;
    private float bullet_spread;
    private final int SIDE_OFFSET;

    public DoubleRocketLauncher(boolean isDrivable, final int SIDE_OFFSET) {
        super(isDrivable);
        this.SIDE_OFFSET = SIDE_OFFSET;
        addInstances(BUFFER_SIZE);  // add double the instances of a normal rocket launcher
    }

    static {
        random = new Random();
    }

    @Override
    public void fire(float spawnX, float spawnY, float rotation_angle) {
        if (canFire()) {
            current_reload_time = 0;    // reset the reload time when a shot is fired

            bullet_spread = random.nextFloat() * (MAX_BULLET_SPREAD - MIN_BULLET_SPREAD) + MIN_BULLET_SPREAD;

            // first bullet (right turret)
            Projectile bullet = addRocket(spawnX, spawnY, rotation_angle + bullet_spread, SIDE_OFFSET);
            projectile_list.add(bullet);

            // second bullet (left turret)
            bullet_spread = random.nextFloat() * (MAX_BULLET_SPREAD - MIN_BULLET_SPREAD) + MIN_BULLET_SPREAD;
            bullet = addRocket(spawnX, spawnY, rotation_angle + bullet_spread, -SIDE_OFFSET);
            projectile_list.add(bullet);

            fire_sound.play(1.f, UserSettings.soundVolume);
        }
    }
}
