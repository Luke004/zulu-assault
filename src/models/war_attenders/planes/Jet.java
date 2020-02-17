package models.war_attenders.planes;

import menus.UserSettings;
import models.weapons.RocketLauncher;
import models.weapons.Uzi;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class Jet extends Plane {

    private static Sound jet_sound;
    private static final int JET_ENGINE_SOUND_MILLIS_TO_WAIT = 110;
    private int current_jet_engine_sound_play_time;
    boolean canPlayJetEngineSound;

    private static Texture jet_texture;

    private static final float ARMOR = 30.f;
    private static final int SCORE_VALUE = 500;
    private static final float ROTATE_SPEED_PLAYER = 0.15f, ROTATE_SPEED_BOT = 0.15f;
    private static final float MAX_SPEED_PLAYER = 0.5f, MAX_SPEED_BOT = 0.2f;
    private static final float MIN_SPEED_PLAYER = 0.1f, MIN_SPEED_BOT = 0.2f;
    private static final float SPEED_INCREASE_DECREASE_FACTOR = 0.002f;

    static {
        try {
            jet_sound = new Sound("audio/sounds/jet.ogg");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public Jet(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        current_speed = isDrivable ? MIN_SPEED_PLAYER : MIN_SPEED_BOT;

        canPlayJetEngineSound = true;

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
        weapons.add(new RocketLauncher(isDrivable));  // TODO: add 'GUIDED' rockets weapon

        super.init();
    }

    @Override
    public void fly(int deltaTime) {
        super.fly(deltaTime);
        current_jet_engine_sound_play_time += deltaTime;
        if (current_jet_engine_sound_play_time > JET_ENGINE_SOUND_MILLIS_TO_WAIT) {
            current_jet_engine_sound_play_time = 0;
            canPlayJetEngineSound = true;
        } else {
            canPlayJetEngineSound = false;
        }
        if (!jet_sound.playing()) {
            playJetEngineSound();
        }
    }

    @Override
    public void increaseSpeed() {
        if (current_speed < (isDrivable ? MAX_SPEED_PLAYER : MAX_SPEED_BOT)) {
            current_speed += SPEED_INCREASE_DECREASE_FACTOR;
            playJetEngineSound();
        }
    }

    @Override
    public void decreaseSpeed() {
        if (current_speed > (isDrivable ? MIN_SPEED_PLAYER : MIN_SPEED_BOT)) {
            playJetEngineSound();
            current_speed -= SPEED_INCREASE_DECREASE_FACTOR;
        }
    }

    public void playJetEngineSound() {
        if (!isMoving || !canPlayJetEngineSound) return;
        jet_sound.stop();
        float pitch = current_speed / MAX_SPEED_PLAYER;
        jet_sound.play(pitch, UserSettings.SOUND_VOLUME);
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
