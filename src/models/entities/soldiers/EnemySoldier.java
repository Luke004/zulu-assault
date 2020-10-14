package models.entities.soldiers;

import models.weapons.Uzi;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class EnemySoldier extends Soldier {

    private static Texture enemy_soldier_animation_texture;

    public EnemySoldier(Vector2f startPos, boolean isHostile) {
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
