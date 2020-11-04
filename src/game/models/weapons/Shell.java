package game.models.weapons;

import settings.UserSettings;
import game.graphics.animations.smoke.SmokeAnimation;
import game.models.weapons.projectiles.GroundBullet;
import game.models.weapons.projectiles.Projectile;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class Shell extends Weapon {

    private static Sound shell_fire_sound;
    private static Texture shell_hud_texture;
    private static Texture shell_bullet_texture;
    private final static float SHELL_SPEED = 0.42f;

    protected SmokeAnimation smokeAnimation;

    public Shell(boolean isDrivable) {
        super();
        smokeAnimation = new SmokeAnimation(1);
        try {
            if (shell_fire_sound == null) {
                shell_fire_sound = new Sound("audio/sounds/shell_shot.ogg");
            }
            fire_sound = shell_fire_sound;

            if (shell_hud_texture == null)
                shell_hud_texture = new Image("assets/hud/weapons/shell.png").getTexture();
            if (isDrivable) weapon_hud_image = new Image(shell_hud_texture);

            if (shell_bullet_texture == null)
                shell_bullet_texture = new Image("assets/bullets/shell.png").getTexture();
            projectile_texture = shell_bullet_texture;
        } catch (SlickException e) {
            e.printStackTrace();
        }
        // individual Shell specs
        bullet_damage = 750;
        shot_reload_time = 1000;

        if (!isDrivable) shot_reload_time *= 5;
    }

    @Override
    public void update(int deltaTime) {
        super.update(deltaTime);
        smokeAnimation.update(deltaTime);
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        smokeAnimation.draw();
    }

    @Override
    public void fire(float spawnX, float spawnY, float rotation_angle) {
        if (canFire()) {
            current_reload_time = 0;    // reset the reload time when a shot is fired

            Projectile bullet = addBullet(spawnX, spawnY, rotation_angle, 0.f);
            bullet.setProjectileSpeed(SHELL_SPEED);
            projectile_list.add(bullet);

            fire_sound.play(1.f, UserSettings.soundVolume);
        }
    }

    protected GroundBullet addBullet(float spawnX, float spawnY, float rotation_angle, float x_offset) {
        float m_spawn_x = spawnX + (float) (Math.cos(((rotation_angle) * Math.PI) / 180) * x_offset
                + -Math.sin(((rotation_angle) * Math.PI) / 180) * -30.f);
        float m_spawn_y = spawnY + (float) (Math.sin(((rotation_angle) * Math.PI) / 180) * x_offset
                + Math.cos(((rotation_angle) * Math.PI) / 180) * -30.f);

        Vector2f bullet_spawn = new Vector2f(m_spawn_x, m_spawn_y);

        float dirX = (float) Math.sin(rotation_angle * Math.PI / 180);
        float dirY = (float) -Math.cos(rotation_angle * Math.PI / 180);
        Vector2f bullet_dir = new Vector2f(dirX, dirY);

        smokeAnimation.play(bullet_spawn.x, bullet_spawn.y, rotation_angle);

        return new GroundBullet(bullet_spawn, bullet_dir, rotation_angle, projectile_texture);
    }
}
