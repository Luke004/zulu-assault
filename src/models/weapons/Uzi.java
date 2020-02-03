package models.weapons;

import menus.UserSettings;
import models.animations.explosion.UziFireShotAnimation;
import models.animations.smoke.SmokeAnimation;
import models.weapons.projectiles.Bullet;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class Uzi extends Weapon {

    private static Sound uzi_fire_sound;
    private static Texture uzi_hud_texture;

    protected UziFireShotAnimation uziFireShotAnimation;

    public Uzi(boolean isDrivable) {
        super();
        uziFireShotAnimation = new UziFireShotAnimation(10);
        try {
            if (uzi_fire_sound == null) {
                uzi_fire_sound = new Sound("audio/sounds/uzi_shot.ogg");
            }
            fire_sound = uzi_fire_sound;

            if (isDrivable && uzi_hud_texture == null) {
                uzi_hud_texture = new Image("assets/hud/weapons/uzi.png").getTexture();
                weapon_hud_image = new Image(uzi_hud_texture);
            }

            projectile_texture = new Image("assets/bullets/bullet_small.png").getTexture();
        } catch (SlickException e) {
            e.printStackTrace();
        }
        // individual Uzi specs
        bullet_damage = 50;
        shot_reload_time = 150;
        if (!isDrivable) shot_reload_time *= 16;
    }

    @Override
    public void update(int deltaTime) {
        super.update(deltaTime);
        uziFireShotAnimation.update(deltaTime);
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        uziFireShotAnimation.draw();
    }

    @Override
    public void fire(float spawnX, float spawnY, float rotation_angle) {
        if (canFire()) {
            current_reload_time = 0;    // reset the reload time when a shot is fired
            Bullet bullet = addBullet(spawnX, spawnY, rotation_angle, 0.f);
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

        uziFireShotAnimation.play(bullet_spawn.x, bullet_spawn.y, rotation_angle);

        return new Bullet(bullet_spawn, bullet_dir, rotation_angle, projectile_texture);
    }
}
