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
        base_image.draw(map_position.x, map_position.y);
        turret.draw(map_position.x + base_image.getWidth() / 4, map_position.y);
        graphics.draw(collision_model);
    }

    @Override
    public void update(GameContainer gc, int delta) {
        //System.out.println(map_position);
    }

    public abstract void rotateTurret(RotateDirection r, int deltaTime);
}
