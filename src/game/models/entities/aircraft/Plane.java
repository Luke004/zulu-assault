package game.models.entities.aircraft;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.openal.SoundStore;
import settings.UserSettings;

public abstract class Plane extends Aircraft {

    protected static Sound plane_engine_sound;

    private static final int ENGINE_SOUND_MILLIS_TO_WAIT = 130;
    private int current_engine_sound_play_time;
    boolean canPlayEngineSound;
    protected Sound engine_sound;

    static {
        try {
            plane_engine_sound = new Sound("audio/sounds/war_plane.ogg");     // default plane sound is war plane
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public Plane(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);
        current_speed = getMinSpeed();
        canPlayEngineSound = true;
        engine_sound = plane_engine_sound;
    }

    @Override
    public void fly(int deltaTime) {
        super.fly(deltaTime);
        current_engine_sound_play_time += deltaTime;
        if (current_engine_sound_play_time > ENGINE_SOUND_MILLIS_TO_WAIT) {
            current_engine_sound_play_time = 0;
            canPlayEngineSound = true;
        }
        if (isDrivable) {

            if (!engine_sound.playing()) {
                playEngineSound();
            }
        }
    }

    @Override
    public void increaseSpeed(int deltaTime) {
        if (current_speed < getMaxSpeed()) {
            current_speed += getSpeedChangeFactor() * deltaTime;
            playEngineSound();
        }
    }

    @Override
    public void decreaseSpeed(int deltaTime) {
        if (current_speed > getMinSpeed()) {
            playEngineSound();
            current_speed -= getSpeedChangeFactor() * deltaTime;
        }
    }

    public void playEngineSound() {
        if (!isMoving || !canPlayEngineSound) return;
        canPlayEngineSound = false;
        engine_sound.stop();
        engine_sound.play(current_speed / getMaxSpeed(), UserSettings.soundVolume);
    }

    /* only allow the plane to land if its not too fast */
    @Override
    protected boolean canLand() {
        if (current_speed > getMinSpeed() + 0.15) return false;
        else return super.canLand();
    }

    protected abstract float getMinSpeed();

    protected abstract float getMaxSpeed();

    protected abstract float getSpeedChangeFactor();

}
