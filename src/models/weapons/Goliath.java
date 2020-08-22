package models.weapons;

import logic.level_listeners.GroundTileDamageListener;
import settings.UserSettings;
import graphics.animations.explosion.BigExplosionAnimation;
import models.weapons.projectiles.AirShell;
import models.weapons.projectiles.Projectile;
import models.weapons.projectiles.iGroundTileDamageWeapon;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class Goliath extends Shell implements iGroundTileDamageWeapon {

    private static Texture goliath_hud_texture;
    private BigExplosionAnimation bigExplosionAnimation;
    public GroundTileDamageListener groundTileDamageListener;

    public Goliath(boolean isDrivable) {
        super(isDrivable);

        try {
            if (goliath_hud_texture == null)
                goliath_hud_texture = new Image("assets/hud/weapons/goliath.png").getTexture();
            if (isDrivable) weapon_hud_image = new Image(goliath_hud_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }

        bigExplosionAnimation = new BigExplosionAnimation(150);

        shot_reload_time = 3500;

        //if (!isDrivable) shot_reload_time *= 2;
    }

    @Override
    public void fire(float spawnX, float spawnY, float rotation_angle) {
        if (canFire()) {
            current_reload_time = 0;    // reset the reload time when a shot is fired

            // fire shells in 12 directions (like a clock)
            float angle = 0;

            do {
                Vector2f bullet_spawn = new Vector2f(spawnX, spawnY);

                float dirX = (float) Math.sin(angle * Math.PI / 180);
                float dirY = (float) -Math.cos(angle * Math.PI / 180);
                Vector2f bullet_dir = new Vector2f(dirX, dirY);

                Projectile bullet = new AirShell(bullet_spawn, bullet_dir, angle, projectile_texture);

                projectile_list.add(bullet);

                angle += 30;
            } while (angle < 360);

            fire_sound.play(1.f, UserSettings.SOUND_VOLUME);
            smokeAnimation.play(spawnX, spawnY, 0);
        }
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

    @Override
    protected void onProjectileRemove(Projectile projectile) {
        // check if a ground tile is damaged
        groundTileDamageListener.notifyForGroundTileDamage(projectile.projectile_pos.x,
                projectile.projectile_pos.y);
        bigExplosionAnimation.playTenTimes(projectile.projectile_pos.x, projectile.projectile_pos.y, 90);
    }

    @Override
    public void addListener(GroundTileDamageListener groundTileDamageListener) {
        this.groundTileDamageListener = groundTileDamageListener;
    }
}
