package game.models.entities.robots;

import game.models.weapons.DoubleLaser;
import game.models.weapons.DoubleRocketLauncher;
import game.models.weapons.Laser;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class LaserRobot extends Robot {

    private static Texture laser_robot_texture;

    public LaserRobot(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        // LOAD TEXTURES
        try {
            if (laser_robot_texture == null) {
                laser_robot_texture = new Image("assets/entities/robots/laser_robot.png")
                        .getTexture();
            }
            base_image = new Image(laser_robot_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }

        weapons.add(new DoubleRocketLauncher(isDrivable, 13));
        weapons.add(new DoubleLaser(isDrivable));

        super.init();
    }

}