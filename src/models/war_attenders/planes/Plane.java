package models.war_attenders.planes;

import models.war_attenders.MovableWarAttender;
import models.war_attenders.tanks.Tank;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

public abstract class Plane extends MovableWarAttender {

    public Plane(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);
    }

    public void init() {
        WIDTH_HALF = base_image.getWidth() / 2;
        HEIGHT_HALF = base_image.getHeight() / 2;
        //destructionAnimation = new Tank.DestructionAnimation();
        super.init();
    }

    @Override
    public void rotate(RotateDirection r, int deltaTime) {
        float degree;
        switch (r) {
            case ROTATE_DIRECTION_LEFT:
                degree = -rotate_speed * deltaTime;
                base_image.rotate(degree);
                break;
            case ROTATE_DIRECTION_RIGHT:
                degree = rotate_speed * deltaTime;
                base_image.rotate(degree);
                break;
        }
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        // draw the plane's shadow
        base_image.drawFlash(position.x, position.y,
                WIDTH_HALF * 2, HEIGHT_HALF * 2, Color.black);

        if (isInvincible) {
            if (!invincibility_animation_switch) {
                base_image.drawFlash(position.x - WIDTH_HALF, position.y - HEIGHT_HALF);
            } else {
                base_image.draw(position.x - WIDTH_HALF, position.y - HEIGHT_HALF);
            }
        } else {
            base_image.draw(position.x - WIDTH_HALF, position.y - HEIGHT_HALF);
        }

        if (isDestroyed) {
            //destructionAnimation.draw(graphics);
        }
    }
}
