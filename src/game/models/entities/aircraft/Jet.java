package game.models.entities.aircraft;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Vector2f;

public abstract class Jet extends Plane {

    protected static Sound jet_engine_sound;

    static {
        try {
            jet_engine_sound = new Sound("audio/sounds/jet.ogg");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public Jet(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);
        engine_sound = jet_engine_sound;
    }

}
