package models.war_attenders.windmills;

import models.weapons.Uzi;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class WindmillGreen extends Windmill {

    public WindmillGreen(Vector2f startPos, boolean isHostile, int key) {
        super(startPos, isHostile, key);

        // individual WindmillGreen attributes
        turret_rotate_speed = 0.5f;
        weapons.add(new Uzi(false));  // WEAPON_1

        try {
            turret = new Image("assets/war_attenders/windmills/GreenWindmill_turret.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        super.init();
    }

    @Override
    public void draw(Graphics graphics) {
        turret.draw(position.x + 17, position.y + 3);
        super.draw(graphics);
    }

    @Override
    public void setRotation(float angle) {
        turret.setCenterOfRotation(4, 18);
        super.setRotation(angle);
    }
}
