package models.war_attenders.windmills;

import models.weapons.Shell;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class WindmillGrey extends Windmill {

    private static Texture windmill_grey_turret_texture;

    public WindmillGrey(Vector2f startPos, boolean isHostile, Vector2f[] tile_positions) {
        super(startPos, isHostile, tile_positions);

        // individual WindmillGrey attributes
        //turret_rotate_speed = 0.4f;
        weapons.add(new Shell(false));  // WEAPON_1

        turret_position = new Vector2f(position.x - 4.f, position.y - 19.f);

        // LOAD TEXTURES
        try {
            if (windmill_grey_turret_texture == null) {
                windmill_grey_turret_texture = new Image("assets/war_attenders/windmills/GreyWindmill_turret.png")
                        .getTexture();
            }
            turret = new Image(windmill_grey_turret_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }

        super.init();
    }

    @Override
    public void changeAimingDirection(float angle, int deltaTime) {
        turret.setCenterOfRotation(6, 21);
        super.changeAimingDirection(angle, deltaTime);
    }
}
