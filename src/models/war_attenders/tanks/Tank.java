package models.war_attenders.tanks;

import models.war_attenders.WarAttender;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

public abstract class Tank extends WarAttender {
    Image turret;
    float turret_rotate_speed;

    public Tank(Vector2f startPos) {
        super(startPos);
    }

    @Override
    public void draw() {
        image.draw(pos.x, pos.y);
        turret.draw(pos.x + image.getWidth() / 4, pos.y);
    }

    @Override
    public void update(GameContainer gc, int delta) {

    }

    public abstract void rotateTurret(RotateDirection r, int deltaTime);
}
