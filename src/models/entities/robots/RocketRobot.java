package models.war_attenders.robots;

import models.weapons.Cannon;
import models.weapons.DoubleRocketLauncher;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class RocketRobot extends Robot {

    private static Texture rocket_robot_texture;

    public RocketRobot(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        // LOAD TEXTURES
        try {
            if (rocket_robot_texture == null) {
                rocket_robot_texture = new Image("assets/war_attenders/robots/rocket_robot.png")
                        .getTexture();
            }
            base_image = new Image(rocket_robot_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }

        weapons.add(new Cannon(isDrivable, 26));
        weapons.add(new DoubleRocketLauncher(isDrivable, 13));

        super.init();
    }


}