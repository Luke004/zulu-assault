package game.models.weapons;

import game.logic.level_listeners.GroundTileDamager;
import game.models.weapons.projectiles.GroundRocket;
import settings.UserSettings;
import game.graphics.animations.explosion.BigExplosionAnimation;
import game.models.weapons.projectiles.AirRocket;
import game.models.weapons.projectiles.Projectile;
import game.models.weapons.projectiles.iGroundTileDamageWeapon;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class AGM extends RocketLauncher implements iGroundTileDamageWeapon {

    private static Texture agm_hud_texture;

    private BigExplosionAnimation bigExplosionAnimation;
    public GroundTileDamager groundTileDamager;

    private final int SIDE_OFFSET;

    public AGM(boolean isDrivable, final int SIDE_OFFSET) {
        super(isDrivable);

        try {
            if (agm_hud_texture == null)
                agm_hud_texture = new Image("assets/hud/weapons/agm.png").getTexture();
            if (isDrivable) weapon_hud_image = new Image(agm_hud_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }

        BUFFER_SIZE *= 2;   // double the buffer size, since this is a double rocket launcher
        bigExplosionAnimation = new BigExplosionAnimation(50);
        this.SIDE_OFFSET = SIDE_OFFSET;

        //if (!isDrivable) shot_reload_time *= 2;
    }

    @Override
    public void fire(float spawnX, float spawnY, float rotation_angle) {
        if (canFire()) {
            current_reload_time = 0;    // reset the reload time when a shot is fired

            // first bullet (right turret)
            Projectile bullet = addRocket(spawnX, spawnY, rotation_angle, SIDE_OFFSET);
            projectile_list.add(bullet);

            // second bullet (left turret)
            bullet = addRocket(spawnX, spawnY, rotation_angle, -SIDE_OFFSET);
            projectile_list.add(bullet);

            fire_sound.play(1.f, UserSettings.soundVolume);
        }
    }

    @Override
    protected Projectile addRocket(float spawnX, float spawnY, float rotation_angle, float side_offset) {
        Vector2f bullet_spawn = calculateBulletSpawn(spawnX, spawnY, rotation_angle, side_offset);
        Vector2f bullet_dir = calculateBulletDir(rotation_angle);
        Animation preparedRocket = prepareNextRocket(rotation_angle);
        return new AirRocket(bullet_spawn, bullet_dir, rotation_angle, projectile_texture, preparedRocket);
    }

    @Override
    public void onProjectileRemove(Projectile projectile) {
        // check if a ground tile is damaged
        groundTileDamager.damageGroundTile(projectile.projectile_pos.x,
                projectile.projectile_pos.y);
        bigExplosionAnimation.playTenTimes(projectile.projectile_pos.x, projectile.projectile_pos.y, 90);
        putRocketBackToBuffer();
    }

    @Override
    public void update(int deltaTime) {
        super.update(deltaTime);
        bigExplosionAnimation.update(deltaTime);
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        bigExplosionAnimation.draw();
    }

    public static void playDestructionAnimation(float xPos, float yPos) {
    }

    @Override
    public void addListener(GroundTileDamager groundTileDamager) {
        this.groundTileDamager = groundTileDamager;
    }
}
