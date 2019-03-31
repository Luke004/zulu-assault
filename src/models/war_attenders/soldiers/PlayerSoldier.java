package models.war_attenders.soldiers;

import models.weapons.Uzi;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class PlayerSoldier extends Soldier {

    public PlayerSoldier(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);

        // individual PlayerSoldier attributes
        max_health = 100;
        current_health = max_health;
        armor = 2.5f;
        max_speed = 0.1f;
        current_speed = max_speed;
        rotate_speed = 0.25f;
        weapons.add(new Uzi(true));

        try {
            base_image = new Image("assets/war_attenders/soldiers/player_soldier_animation.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        super.init();
    }
}
