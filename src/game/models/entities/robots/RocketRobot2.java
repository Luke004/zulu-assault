package game.models.entities.robots;

import game.models.weapons.DoubleRocketLauncher;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

/* This robot also has a double rocket launcher, but has no cannon.
    It also has a different texture than the other RocketRobot */

public class RocketRobot2 extends Robot {

    private static Texture rocket_robot_texture;

    public RocketRobot2(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        // LOAD TEXTURES
        try {
            if (rocket_robot_texture == null) {
                rocket_robot_texture = new Image("assets/entities/robots/rocket_robot_2.png")
                        .getTexture();
            }
            base_image = new Image(rocket_robot_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }

        weapons.add(new DoubleRocketLauncher(isDrivable, 13));

        super.init();
    }

}