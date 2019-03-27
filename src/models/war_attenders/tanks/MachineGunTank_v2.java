package models.war_attenders.tanks;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class MachineGunTank_v2 extends MachineGunTank {

    public MachineGunTank_v2(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);

        // individual MachineGunTank attributes
        // EVERYTHING = MachineGunTank here, the only diff is the 'base_image'

        try {
            base_image = new Image("assets/war_attenders/tanks/agile_tank_v2.png");
            turret = new Image("assets/war_attenders/tanks/agile_tank_v2_turret.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }
}

