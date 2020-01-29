package models.war_attenders.robots;

import models.weapons.DoubleShell;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class ShellRobot extends Robot{

    public ShellRobot(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        // individual ShellRobot attributes for human player
        armor = 75;
        max_speed = 0.15f;
        current_speed = max_speed;
        rotate_speed = 0.25f;

        init();
    }

    public void init(){
        // first weapon
        weapons.add(new DoubleShell(isDrivable));

        try {
            base_image = new Image("assets/war_attenders/robots/shell_robot.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        super.init();
    }
}
