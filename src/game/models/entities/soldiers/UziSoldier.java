package game.models.entities.soldiers;

import game.models.weapons.Uzi;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class UziSoldier extends Soldier {

    private static Texture enemy_soldier_animation_texture;

    public UziSoldier(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);

        weapons.add(new Uzi(false));

        // LOAD TEXTURES
        try {
            if (enemy_soldier_animation_texture == null) {
                enemy_soldier_animation_texture = new Image("assets/entities/soldiers/enemy_soldier_animation.png")
                        .getTexture();
            }
            base_image = new Image(enemy_soldier_animation_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }

        super.init();
    }
}
