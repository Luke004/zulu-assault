package game.models.entities.windmills;

import game.models.weapons.Uzi;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class WindmillGreen extends Windmill {

    private static Texture windmill_green_texture, windmill_green_turret_texture;

    public WindmillGreen(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);

        // individual WindmillGreen attributes
        //turret_rotate_speed = 0.5f;
        weapons.add(new Uzi(false));  // WEAPON_1

        turret_position = new Vector2f(position.x - 3.f, position.y - 17.f);

        // LOAD TEXTURES
        try {
            if (windmill_green_texture == null) {
                windmill_green_texture = new Image("assets/entities/windmills/green_windmill.png")
                        .getTexture();
            }
            base_image = new Image(windmill_green_texture);
            if (windmill_green_turret_texture == null) {
                windmill_green_turret_texture = new Image("assets/entities/windmills/green_windmill_turret.png")
                        .getTexture();
            }
            turret = new Image(windmill_green_turret_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }

        super.init();
    }

    @Override
    public void changeAimingDirection(float angle, int deltaTime) {
        turret.setCenterOfRotation(4, 18);
        super.changeAimingDirection(angle, deltaTime);
    }
}
