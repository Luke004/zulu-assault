package models.war_attenders.soldiers;

import models.weapons.RocketLauncher;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class RocketSoldier extends Soldier {

    private static Texture rocket_soldier_animation_texture;

    public RocketSoldier(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);

        // individual RocketSoldier attributes
        max_speed = 0.05f;
        current_speed = max_speed;
        rotate_speed = 0.25f;

        weapons.add(new RocketLauncher(false));

        // LOAD TEXTURES
        try {
            if (rocket_soldier_animation_texture == null) {
                rocket_soldier_animation_texture = new Image("assets/war_attenders/soldiers/rocket_soldier_animation.png")
                        .getTexture();
            }
            base_image = new Image(rocket_soldier_animation_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }

        super.init();
    }
}
