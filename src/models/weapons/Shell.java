package models.weapons;

import models.animations.SmokeAnimation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import java.util.Iterator;

public class Shell extends Weapon {
    SmokeAnimation smokeAnimation;
    public Shell() {
        super();
        smokeAnimation = new SmokeAnimation(1);
        try {
            bullet_texture = new Image("assets/bullets/shell.png").getTexture();
        } catch (SlickException e) {
            e.printStackTrace();
        }
        // individual Shell specs
        bullet_damage = 750;
        bullet_speed = 0.8f;
        shot_reload_time = 1000;
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

            Weapon.Bullet bullet = new Weapon.Bullet(bullet_spawn, bullet_dir, rotation_angle);
            bullet_list.add(bullet);

            smokeAnimation.play(bullet_spawn, rotation_angle);
        }
    }
}
