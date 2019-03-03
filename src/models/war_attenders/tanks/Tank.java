package models.war_attenders.tanks;

import models.war_attenders.WarAttender;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

public abstract class Tank extends WarAttender {
    Image turret;
    float turret_rotate_speed;

    public Tank(Vector2f startPos) {
        super(startPos);
    }

    @Override
    public void draw(Graphics graphics) {
        base_image.draw(position.x, position.y);
        turret.draw(position.x + base_image.getWidth() / 4, position.y);
        graphics.draw(collision_model);
        if (accessible_animation != null) {
            accessible_animation.draw(position.x + base_image.getWidth()/2 - 9, position.y);
        }
    }

    @Override
    public void update(GameContainer gc, int deltaTime) {
        if (accessible_animation != null) {
            accessible_animation.update(deltaTime);
        }
    }

    public abstract void rotateTurret(RotateDirection r, int deltaTime);
}
