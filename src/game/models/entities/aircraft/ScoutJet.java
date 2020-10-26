package game.models.entities.aircraft;

import game.models.weapons.AGM;
import game.models.weapons.Uzi;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class ScoutJet extends Jet {

    private static Texture scout_jet_texture;

    public ScoutJet(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        // LOAD TEXTURES
        try {
            if (scout_jet_texture == null) {
                scout_jet_texture = new Image("assets/entities/aircraft/scout_jet.png")
                        .getTexture();
            }
            base_image = new Image(scout_jet_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }

        weapons.add(new Uzi(isDrivable));  // WEAPON_1
        weapons.add(new AGM(isDrivable));  // WEAPON_2

        super.init();
    }

}
