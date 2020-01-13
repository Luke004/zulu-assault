package models.war_attenders.tanks;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class CannonTank_v2 extends CannonTank {

    public CannonTank_v2(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);
        try {
            base_image = new Image("assets/war_attenders/tanks/agile_tank_v2.png");
            turret = new Image("assets/war_attenders/tanks/agile_tank_v2_turret.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

}

