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
        weapons.add(new Shell());  // WEAPON_1

        try {
            turret = new Image("assets/war_attenders/windmills/GreyWindmill_turret.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        super.init();
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        turret.draw(position.x + 16, position.y + 1);
    }

    @Override
    public void rotateTowardsPlayer(float angle) {
        turret.setCenterOfRotation(6, 21);
        super.rotateTowardsPlayer(angle);
    }
}
