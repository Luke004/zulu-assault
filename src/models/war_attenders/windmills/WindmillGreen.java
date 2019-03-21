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
        weapons.add(new Uzi());  // WEAPON_1

        try {
            turret = new Image("assets/windmills/GreenWindmill_turret.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        super.init();
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        turret.draw(position.x + 17, position.y + 3);
    }

    @Override
    public void rotateTowardsPlayer(float angle) {
        turret.setCenterOfRotation(4, 18);
        super.rotateTowardsPlayer(angle);
    }
}
