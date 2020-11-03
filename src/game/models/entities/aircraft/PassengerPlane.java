package game.models.entities.aircraft;

import game.models.CollisionModel;
import game.models.weapons.Uzi;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class PassengerPlane extends Plane {

    private static Sound passenger_plane_engine_sound;
    private static Texture passenger_plane_texture;

    // STATS
    private static final float ARMOR = 63.f;
    private static final float ROTATE_SPEED_PLAYER = 0.02f, ROTATE_SPEED_BOT = 0.02f;
    private static final float MIN_SPEED_PLAYER = 0.05f, MIN_SPEED_BOT = 0.05f;
    private static final float MAX_SPEED_PLAYER = 0.1f, MAX_SPEED_BOT = 0.05f;
    private static final float SPEED_CHANGE_FACTOR = 0.00005f;

    static {
        try {
            passenger_plane_engine_sound = new Sound("audio/sounds/passenger_plane.ogg");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public PassengerPlane(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        // LOAD TEXTURES
        try {
            if (passenger_plane_texture == null) {
                passenger_plane_texture = new Image("assets/entities/aircraft/passenger_plane.png")
                        .getTexture();
            }
            base_image = new Image(passenger_plane_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }
        engine_sound = passenger_plane_engine_sound;

        super.init();
        collisionModel = new CollisionModel(position, base_image.getWidth() / 8, base_image.getHeight());
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
