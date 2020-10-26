package game.models.entities.windmills;

import game.models.weapons.RocketLauncher;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class WindmillYellow extends Windmill {

    private static Texture windmill_yellow_texture, windmill_yellow_turret_texture;

    public WindmillYellow(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);

        // individual WindmillYellow attributes
        //turret_rotate_speed = 0.45f;
        weapons.add(new RocketLauncher(false));   // WEAPON_1

        turret_position = new Vector2f(position.x - 5.5f, position.y - 16.f);

        // LOAD TEXTURES
        try {
            if (windmill_yellow_texture == null) {
                windmill_yellow_texture = new Image("assets/entities/windmills/yellow_windmill.png")
                        .getTexture();
            }
            base_image = new Image(windmill_yellow_texture);
            if (windmill_yellow_turret_texture == null) {
                windmill_yellow_turret_texture = new Image("assets/entities/windmills/yellow_windmill_turret.png")
                        .getTexture();
            }
            turret = new Image(windmill_yellow_turret_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }

        super.init();
    }

    @Override
    public void changeAimingDirection(float angle, int deltaTime) {
        turret.setCenterOfRotation(8, 15);
        super.changeAimingDirection(angle, deltaTime);
    }
}
