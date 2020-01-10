package models.war_attenders.windmills;

import models.weapons.Uzi;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class WindmillGreen extends Windmill {

    public WindmillGreen(Vector2f startPos, boolean isHostile, Vector2f tile_position) {
        super(startPos, isHostile, tile_position);

        // individual WindmillGreen attributes
        turret_rotate_speed = 0.5f;
        weapons.add(new Uzi(false));  // WEAPON_1

        turret_position = new Vector2f(position.x - 3.f, position.y - 17.f);
        try {
            turret = new Image("assets/war_attenders/windmills/GreenWindmill_turret.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        super.init();
    }

    @Override
    public void setRotation(float angle) {
        turret.setCenterOfRotation(4, 18);
        super.setRotation(angle);
    }
}
