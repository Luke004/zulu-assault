package game.models.entities.robots;

import game.models.weapons.DoublePlasma;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class PlasmaRobot extends Robot {

    private static Texture plasma_robot_texture;

    public PlasmaRobot(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        weapons.add(new DoublePlasma(isDrivable));

        // LOAD TEXTURES
        try {
            if (plasma_robot_texture == null) {
                plasma_robot_texture = new Image("assets/entities/robots/plasma_robot.png")
                        .getTexture();
            }
            base_image = new Image(plasma_robot_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }

        super.init();
    }
}
