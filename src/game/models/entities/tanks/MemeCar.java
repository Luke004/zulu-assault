package game.models.entities.tanks;

import game.models.weapons.Uzi;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class MemeCar extends Tank {

    private static Texture meme_car_texture, meme_car_turret_texture;

    // attributes
    private static final float ARMOR = 10.f;
    private static final int SCORE_VALUE = 50;
    private static final float ROTATE_SPEED_PLAYER = 0.15f, ROTATE_SPEED_BOT = 0.15f;
    private static final float MAX_SPEED_PLAYER = 0.3f, MAX_SPEED_BOT = 0.2f;
    private static final float ACCELERATION_FACTOR = 0.0005f, DECELERATION_FACTOR = 0.0003f;

    public MemeCar(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        weapons.add(new Uzi(isDrivable));  // WEAPON_1

        // LOAD TEXTURES
        try {
            if (meme_car_texture == null) {
                meme_car_texture = new Image("assets/entities/tanks/meme_car.png")
                        .getTexture();
            }
            base_image = new Image(meme_car_texture);
            if (meme_car_turret_texture == null) {
                meme_car_turret_texture =
                        new Image("assets/entities/tanks/meme_car_turret.png")
                                .getTexture();
            }
            turret = new Image(meme_car_turret_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }
        super.init();
    }

    @Override
    protected float getBaseRotateSpeed() {
        return isDrivable ? ROTATE_SPEED_PLAYER : ROTATE_SPEED_BOT;
    }

    @Override
    public float getMaxSpeed() {
        return isDrivable ? MAX_SPEED_PLAYER : MAX_SPEED_BOT;
    }

    @Override
    public int getScoreValue() {
        return SCORE_VALUE;
    }

    @Override
    protected float getAccelerationFactor() {
        return ACCELERATION_FACTOR;
    }

    @Override
    protected float getDecelerationFactor() {
        return DECELERATION_FACTOR;
    }

    @Override
    public void changeHealth(float amount) {
        super.changeHealth(amount, ARMOR);
    }
}
