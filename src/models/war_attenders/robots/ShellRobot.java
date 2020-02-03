package models.war_attenders.robots;

import models.weapons.DoubleShell;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class ShellRobot extends Robot {

    private static Texture shell_robot_texture;

    public ShellRobot(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        weapons.add(new DoubleShell(isDrivable));

        // LOAD TEXTURES
        try {
            if (shell_robot_texture == null) {
                shell_robot_texture = new Image("assets/war_attenders/robots/shell_robot.png")
                        .getTexture();
            }
            base_image = new Image(shell_robot_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }

        super.init();
    }


}
