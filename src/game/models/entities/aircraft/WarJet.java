package game.models.entities.aircraft;

import game.models.weapons.RocketLauncher;
import game.models.weapons.Uzi;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class WarJet extends Jet {

    private static Texture war_jet_texture;

    // STATS
    private static final float ARMOR = 30.f;
    private static final float ROTATE_SPEED_PLAYER = 0.15f, ROTATE_SPEED_BOT = 0.15f;
    private static final float MIN_SPEED_PLAYER = 0.2f, MIN_SPEED_BOT = 0.1f;
    private static final float MAX_SPEED_PLAYER = 0.35f, MAX_SPEED_BOT = 0.15f;
    private static final float SPEED_CHANGE_FACTOR = 0.00013f;

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
        if (isDrivable) weapons.add(new RocketLauncher(isDrivable));  // TODO: add 'GUIDED' rockets weapon

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
