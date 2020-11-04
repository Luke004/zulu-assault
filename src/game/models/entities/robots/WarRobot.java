package game.models.entities.robots;

import game.models.weapons.Cannon;
import game.models.weapons.DoubleRocketLauncher;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class WarRobot extends Robot {

    private static Texture rocket_robot_texture;

    public WarRobot(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        // LOAD TEXTURES
        try {
            if (rocket_robot_texture == null) {
                rocket_robot_texture = new Image("assets/entities/robots/war_robot.png")
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