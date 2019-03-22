package models.war_attenders.windmills;

import models.weapons.RocketLauncher;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class WindmillYellow extends Windmill {
    public WindmillYellow(Vector2f startPos, boolean isHostile, int key) {
        super(startPos, isHostile, key);

        // individual WindmillYellow attributes
        weapons.add(new RocketLauncher(isHostile));   // WEAPON_1

        try {
            turret = new Image("assets/war_attenders/windmills/YellowWindmill_turret.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        super.init();
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        turret.draw(position.x + 14.5f, position.y + 4);
    }

    @Override
    public void rotateTowardsPlayer(float angle) {
        turret.setCenterOfRotation(8, 15);
        super.rotateTowardsPlayer(angle);
    }

}
