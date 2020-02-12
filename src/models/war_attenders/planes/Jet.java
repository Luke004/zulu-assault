package models.war_attenders.planes;

import models.CollisionModel;
import models.weapons.Uzi;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class Jet extends Plane {

    private static Texture jet_texture;

    private static final float ARMOR = 30.f;
    private static final int SCORE_VALUE = 500;
    private static final float ROTATE_SPEED_PLAYER = 0.15f, ROTATE_SPEED_BOT = 0.15f;
    private static final float MAX_SPEED_PLAYER = 0.5f, MAX_SPEED_BOT = 0.2f;
    private static final float MIN_SPEED_PLAYER = 0.1f, MIN_SPEED_BOT = 0.2f;
    private static final float SPEED_INCREASE_DECREASE_FACTOR = 0.002f;

    public Jet(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        current_speed = isDrivable ? MIN_SPEED_PLAYER : MIN_SPEED_BOT;

        // LOAD TEXTURES
        try {
            if (jet_texture == null) {
                jet_texture = new Image("assets/war_attenders/planes/jet.png")
                        .getTexture();
            }
            base_image = new Image(jet_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }

        weapons.add(new Uzi(isDrivable));  // WEAPON_1

        super.init();
    }

    @Override
    public void increaseSpeed() {
        if (current_speed < (isDrivable ? MAX_SPEED_PLAYER : MAX_SPEED_BOT))
            current_speed += SPEED_INCREASE_DECREASE_FACTOR;
    }

    @Override
    public void decreaseSpeed() {
        if (current_speed > (isDrivable ? MIN_SPEED_PLAYER : MIN_SPEED_BOT))
            current_speed -= SPEED_INCREASE_DECREASE_FACTOR;
    }

    /* only allow the jet to land if its not too fast */
    @Override
    protected boolean canLand() {
        if (current_speed > MIN_SPEED_PLAYER + 0.15) return false;
        else return super.canLand();
    }

    @Override
    protected float getBaseRotateSpeed() {
        return isDrivable ? ROTATE_SPEED_PLAYER : ROTATE_SPEED_BOT;
    }

    @Override
    protected float getMaxSpeed() {
        return isDrivable ? MAX_SPEED_PLAYER : MAX_SPEED_BOT;
    }

    @Override
    public void changeHealth(float amount) {
        super.changeHealth(amount, ARMOR);
    }

    @Override
    public int getScoreValue() {
        return SCORE_VALUE;
    }
}
