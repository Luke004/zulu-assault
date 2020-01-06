package models.war_attenders.windmills;

import models.weapons.Shell;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class WindmillGrey extends Windmill {
    public WindmillGrey(Vector2f startPos, boolean isHostile, int key) {
        super(startPos, isHostile, key);

        // individual WindmillGrey attributes
        turret_rotate_speed = 0.4f;
        weapons.add(new Shell(false));  // WEAPON_1

        turret_position = new Vector2f(position.x - 4.f, position.y - 19.f);
        try {
            turret = new Image("assets/war_attenders/windmills/GreyWindmill_turret.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        super.init();
    }

    @Override
    public void setRotation(float angle) {
        turret.setCenterOfRotation(6, 21);
        super.setRotation(angle);
    }
}
