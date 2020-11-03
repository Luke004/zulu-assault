package game.models.entities.aircraft;

import game.models.weapons.AGM;
import game.models.weapons.Uzi;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class ScoutJet extends Jet {

    private static Texture scout_jet_texture;

    // STATS
    private static final float ARMOR = 20.f;
    private static final float ROTATE_SPEED_PLAYER = 0.15f, ROTATE_SPEED_BOT = 0.15f;
    private static final float MIN_SPEED_PLAYER = 0.25f, MIN_SPEED_BOT = 0.2f;
    private static final float MAX_SPEED_PLAYER = 0.5f, MAX_SPEED_BOT = 0.25f;
    private static final float SPEED_CHANGE_FACTOR = 0.0002f;


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

        weapons.add(new Uzi(isDrivable));       // WEAPON_1
        weapons.add(new AGM(isDrivable));     // WEAPON_2

        super.init();
    }

    @Override
    protected float getBaseRotateSpeed() {
        return isDrivable ? ROTATE_SPEED_PLAYER : ROTATE_SPEED_BOT;
    }

    @Override
    protected float getMinSpeed() {
        return (isDrivable ? MIN_SPEED_PLAYER : MIN_SPEED_BOT);
    }

    @Override
    protected float getMaxSpeed() {
        return (isDrivable ? MAX_SPEED_PLAYER : MAX_SPEED_BOT);
    }

    @Override
    protected float getSpeedChangeFactor() {
        return SPEED_CHANGE_FACTOR;
    }

    @Override
    public void changeHealth(float amount) {
        super.changeHealth(amount, ARMOR);
    }

}
