package models.war_attenders.soldiers;

import models.weapons.RocketLauncher;
import models.weapons.Uzi;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class RocketSoldier extends Soldier {

    public RocketSoldier(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);

        // individual RocketSoldier attributes
        max_health = 100;
        current_health = max_health;
        armor = 2.5f;
        max_speed = 0.05f;
        current_speed = max_speed;
        rotate_speed = 0.25f;
        weapons.add(new RocketLauncher(false));

        try {
            base_image = new Image("assets/war_attenders/soldiers/rocket_soldier_animation.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        super.init();
    }
}
