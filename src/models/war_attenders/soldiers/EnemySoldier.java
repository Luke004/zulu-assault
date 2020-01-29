package models.war_attenders.soldiers;

import models.weapons.Uzi;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class EnemySoldier extends Soldier {

    public EnemySoldier(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);

        // individual EnemySoldier attributes
        armor = 2.5f;
        max_speed = 0.1f;
        current_speed = max_speed;
        rotate_speed = 0.25f;
        weapons.add(new Uzi(false));

        try {
            base_image = new Image("assets/war_attenders/soldiers/enemy_soldier_animation.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        super.init();
    }
}
