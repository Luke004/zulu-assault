package models.war_attenders.tanks;

import models.CollisionModel;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class AgileTank extends Tank {

    private boolean switch_bullet_spawn_side;   // so each turret can shoot


    public AgileTank(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);

        // individual AgileTank attributes
        max_health = 100;
        current_health = max_health;
        max_speed = 0.3f;
        backwards_speed = 0.15f;
        acceleration_factor = 0.0005f;
        deceleration_factor = 0.0005f;
        rotate_speed = 0.15f;
        turret_rotate_speed = 0.2f;
        bullet_speed = 0.8f;
        shot_reload_time = 300;
        bullet_damage = 1;

        if (isHostile) {
            shot_reload_time *= 2;
        }

        //init models
        try {
            base_image = new Image("assets/tanks/agile_tank.png");
            turret = new Image("assets/tanks/agile_tank_turret.png");
            bullet_texture = new Image("assets/bullets/bullet_small.png").getTexture();
        } catch (SlickException e) {
            e.printStackTrace();
        }
        collisionModel = new CollisionModel(position, base_image.getWidth(), base_image.getHeight());

        super.init();
    }

/*
    @Override
    public void update(GameContainer gc, int deltaTime) {
        super.update(gc, deltaTime);
        System.out.println(isMoving);
    }
*/
    @Override
    public void shoot() {
        if (canShoot()) {
            current_reload_time = 0;    // reset the reload time when a shot is fired
            float rotation_angle = turret.getRotation();

            float spawnX = position.x;
            float spawnY = position.y;
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
