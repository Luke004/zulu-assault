package models.weapons;

import models.animations.SmokeAnimation;
import models.weapons.projectiles.Bullet;
import models.weapons.projectiles.Projectile;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class Shell extends Weapon {
    SmokeAnimation smokeAnimation;

    public Shell(boolean isDrivable) {
        super();
        smokeAnimation = new SmokeAnimation(1);
        try {
            projectile_texture = new Image("assets/bullets/shell.png").getTexture();
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
            spawnX += -Math.sin(((rotation_angle) * Math.PI) / 180) * -30.f;
            spawnY += Math.cos(((rotation_angle) * Math.PI) / 180) * -30.f;
            Vector2f bullet_spawn = new Vector2f(spawnX, spawnY);

            float xVal = (float) Math.sin(rotation_angle * Math.PI / 180);
            float yVal = (float) -Math.cos(rotation_angle * Math.PI / 180);
            Vector2f bullet_dir = new Vector2f(xVal, yVal);

            Projectile bullet = new Bullet(bullet_spawn, bullet_dir, rotation_angle, projectile_texture);
            projectile_list.add(bullet);

            smokeAnimation.play(bullet_spawn.x, bullet_spawn.y, rotation_angle);
        }
    }
}
