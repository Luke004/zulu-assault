package models.war_attenders.robots;

import models.CollisionModel;
import models.weapons.DoublePlasma;
import models.weapons.Plasma;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class PlasmaRobot extends Robot {

    public PlasmaRobot(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);

        // individual PlasmaRobot attributes
        max_health = 100;
        current_health = max_health;
        max_speed = 0.2f;
        current_speed = max_speed;
        rotate_speed = 0.25f;
        weapons.add(new DoublePlasma());

        try {
            base_image = new Image("assets/war_attenders/robots/plasma_robot.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        super.init();
    }
}
