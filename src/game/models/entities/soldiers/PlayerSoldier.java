package game.models.entities.soldiers;

import game.models.weapons.MegaPulse;
import game.models.weapons.Uzi;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class PlayerSoldier extends Soldier {

    public PlayerSoldier(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);

        weapons.add(new Uzi(true));
        weapons.add(new MegaPulse(true));  // add the MEGA_PULSE (special item)

        try {
            base_image = new Image("assets/entities/soldiers/player_soldier_animation.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        super.init();
    }
}
