package models.war_attenders.tanks;

import models.war_attenders.WarAttender;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

public abstract class Tank extends WarAttender {
    Image turret;

    public Tank(Vector2f startPos) {
        super(startPos);
    }

    @Override
    public void draw() {
        image.draw(pos.x, pos.y);
    }

    @Override
    public void update(GameContainer gc, int delta) {

    }
}
