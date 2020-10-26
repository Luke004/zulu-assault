package game.models.entities.soldiers;

import game.models.weapons.RocketLauncher;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class RocketSoldier extends Soldier {

    private static Texture rocket_soldier_animation_texture;

    public RocketSoldier(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);

        weapons.add(new RocketLauncher(false));

        // LOAD TEXTURES
        try {
            if (rocket_soldier_animation_texture == null) {
                rocket_soldier_animation_texture = new Image("assets/entities/soldiers/rocket_soldier_animation.png")
                        .getTexture();
            }
            base_image = new Image(rocket_soldier_animation_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }

        super.init();
    }
}
