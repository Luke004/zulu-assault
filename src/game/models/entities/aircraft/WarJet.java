package game.models.entities.aircraft;

import game.models.weapons.RocketLauncher;
import game.models.weapons.Uzi;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class WarJet extends Jet {

    private static Texture war_jet_texture;

    public WarJet(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        // LOAD TEXTURES
        try {
            if (war_jet_texture == null) {
                war_jet_texture = new Image("assets/entities/aircraft/war_jet.png")
                        .getTexture();
            }
            base_image = new Image(war_jet_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }

        weapons.add(new Uzi(isDrivable));  // WEAPON_1
        weapons.add(new RocketLauncher(isDrivable));  // TODO: add 'GUIDED' rockets weapon

        super.init();
    }

}
