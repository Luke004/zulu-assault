package models.war_attenders.windmills;

import models.weapons.RocketLauncher;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class WindmillYellow extends Windmill {
    public WindmillYellow(Vector2f startPos, boolean isHostile, Vector2f[] tile_positions) {
        super(startPos, isHostile, tile_positions);

        // individual WindmillYellow attributes
        //turret_rotate_speed = 0.45f;
        weapons.add(new RocketLauncher(false));   // WEAPON_1

        turret_position = new Vector2f(position.x - 5.5f, position.y - 16.f);
        try {
            turret = new Image("assets/war_attenders/windmills/YellowWindmill_turret.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        super.init();
    }

    @Override
    public void changeAimingDirection(float angle, int deltaTime) {
        turret.setCenterOfRotation(8, 15);
        super.changeAimingDirection(angle, deltaTime);
    }
}
