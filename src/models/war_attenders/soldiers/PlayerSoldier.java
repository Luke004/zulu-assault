package models.war_attenders.soldiers;

import models.CollisionModel;
import models.weapons.MachineGun;
import models.weapons.Uzi;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class PlayerSoldier extends Soldier {

    public PlayerSoldier(float start_xPos, float start_yPos) {
        super(new Vector2f(start_xPos, start_yPos), false);

        // individual PlayerSoldier attributes
        max_health = 100;
        current_health = max_health;
        max_speed = 0.1f;
        current_speed = max_speed;
        rotate_speed = 0.25f;
        weapons.add(new Uzi());

        try {
            base_image = new Image("assets/soldiers/player_soldier_animation.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        animation = new Animation(false);
        int x = 0;
        do {
            animation.addFrame(base_image.getSubImage(x, 0, 12, 12), 300);
            x += 12;
        } while (x <= 24);
        animation.setCurrentFrame(1);
        animation.setLooping(true);
        animation.setPingPong(true);
        animation.stop();
        // just use index 0, all indices are same width and height
        collisionModel = new CollisionModel(position, animation.getImage(0).getWidth(), animation.getImage(0).getHeight());
        super.init();
    }

    @Override
    public float getRotation() {
        return animation.getCurrentFrame().getRotation();
    }

    @Override
    public void rotate(RotateDirection rotateDirection, int deltaTime) {
        switch (rotateDirection) {
            case ROTATE_DIRECTION_LEFT:
                for (int idx = 0; idx < animation.getFrameCount(); ++idx) {
                    animation.getImage(idx).rotate(-rotate_speed * deltaTime);
                }
                break;
            case ROTATE_DIRECTION_RIGHT:
                for (int idx = 0; idx < animation.getFrameCount(); ++idx) {
                    animation.getImage(idx).rotate(rotate_speed * deltaTime);
                }
                break;
        }
    }


    /*
    @Override
    public void fireWeapon() {
        if (canShoot()) {
            current_reload_time = 0;    // reset the reload time when a shot is fired
            float rotation_angle = animation.getCurrentFrame().getRotation();
            float spawnX = position.x;
            float spawnY = position.y;
            spawnX += -Math.sin(((rotation_angle) * Math.PI) / 180) * -30.f;
            spawnY += Math.cos(((rotation_angle) * Math.PI) / 180) * -30.f;
            Vector2f bullet_spawn = new Vector2f(spawnX, spawnY);

            float xVal = (float) Math.sin(rotation_angle * Math.PI / 180);
            float yVal = (float) -Math.cos(rotation_angle * Math.PI / 180);
            Vector2f bullet_dir = new Vector2f(xVal, yVal);

            Bullet bullet = new Bullet(bullet_spawn, bullet_dir, rotation_angle);
            bullet_list.add(bullet);
        }
    }
    */

}
