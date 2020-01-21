package models.weapons;

import menus.UserSettings;
import models.animations.smoke.SmokeAnimation;
import models.weapons.projectiles.Bullet;
import models.weapons.projectiles.Projectile;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Vector2f;

public class Shell extends Weapon {
    protected SmokeAnimation smokeAnimation;

    public Shell(boolean isDrivable) {
        super();
        smokeAnimation = new SmokeAnimation(1);
        try {
            weapon_hud_image = new Image("assets/hud/weapons/shell.png");
            projectile_texture = new Image("assets/bullets/shell.png").getTexture();
            fire_sound = new Sound("audio/sounds/shell_shot.ogg");
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
            projectile_list.add(bullet);

            fire_sound.play(1.f, UserSettings.SOUND_VOLUME);
        }
    }

    protected Bullet addBullet(float spawnX, float spawnY, float rotation_angle, float x_offset) {
        float m_spawn_x = spawnX + (float) (Math.cos(((rotation_angle) * Math.PI) / 180) * x_offset
                + -Math.sin(((rotation_angle) * Math.PI) / 180) * -30.f);
        float m_spawn_y = spawnY + (float) (Math.sin(((rotation_angle) * Math.PI) / 180) * x_offset
                + Math.cos(((rotation_angle) * Math.PI) / 180) * -30.f);

        Vector2f bullet_spawn = new Vector2f(m_spawn_x, m_spawn_y);

        float dirX = (float) Math.sin(rotation_angle * Math.PI / 180);
        float dirY = (float) -Math.cos(rotation_angle * Math.PI / 180);
        Vector2f bullet_dir = new Vector2f(dirX, dirY);

        smokeAnimation.play(bullet_spawn.x, bullet_spawn.y, rotation_angle);

        return new Bullet(bullet_spawn, bullet_dir, rotation_angle, projectile_texture);
    }
}
