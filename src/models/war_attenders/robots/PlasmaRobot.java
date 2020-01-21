package models.war_attenders.robots;

import models.weapons.DoublePlasma;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class PlasmaRobot extends Robot {

    public PlasmaRobot(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        // attributes equal for human and bots
        max_health = 100;
        current_health = max_health;
        armor = 75;

        if (isDrivable) {
            // individual PlasmaRobot attributes for human player
            max_speed = 0.15f;
            current_speed = max_speed;
            rotate_speed = 0.25f;
            //turret_rotate_speed = 0.5f;
        } else {
            // individual PlasmaRobot attributes for bots
            max_speed = 0.05f;
            current_speed = max_speed;
            rotate_speed = 0.25f;
            //turret_rotate_speed = 0.5f;
        }

        weapons.add(new DoublePlasma(isDrivable));

        try {
            base_image = new Image("assets/war_attenders/robots/plasma_robot.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        super.init();
    }
}
