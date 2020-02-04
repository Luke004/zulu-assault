package models.war_attenders.soldiers;

import models.weapons.Uzi;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class PlayerSoldier extends Soldier {

    public PlayerSoldier(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);

        weapons.add(new Uzi(true));

        try {
            base_image = new Image("assets/war_attenders/soldiers/player_soldier_animation.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        super.init();
    }
}
