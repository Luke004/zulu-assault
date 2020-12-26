package game.models.entities.aircraft;

import game.logic.TileMapData;
import settings.UserSettings;
import game.graphics.animations.other.AnimatedCrosshair;
import game.models.weapons.AGM;
import game.models.weapons.RocketLauncher;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

import static game.logic.TileMapData.*;
import static game.logic.TileMapData.LANDSCAPE_TILES_LAYER_IDX;

public class AttackHelicopter extends Aircraft {

    private static Texture helicopter_texture;
    private static Texture helicopter_wings_moving_texture, helicopter_wings_idle_texture;

    private static Sound helicopter_sound;
    private static final int HELICOPTER_ENGINE_SOUND_MILLIS_TO_WAIT = 100;
    private int current_helicopter_engine_sound_play_time;
    boolean canPlayHelicopterEngineSound;

    private AnimatedCrosshair animatedCrosshair;

    private Image helicopter_wings_moving_image, helicopter_wings_idle_image;
    private static float WINGS_IMAGE_HALF;

    // for deceleration
    private boolean decelerate;
    Direction decelerateDirection;

    private static final float ARMOR = 25.f;
    private static final float ROTATE_SPEED_PLAYER = 0.15f, ROTATE_SPEED_BOT = 0.15f;
    private static final float MAX_SPEED_PLAYER = 0.25f, MAX_SPEED_BOT = 0.25f;
    private static final float ACCELERATION_FACTOR = 0.002f, DECELERATION_FACTOR = 0.0005f;


    public AttackHelicopter(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        if (isDrivable) animatedCrosshair = new AnimatedCrosshair();

        canPlayHelicopterEngineSound = true;

        // LOAD TEXTURES
        try {
            if (helicopter_texture == null) {
                helicopter_texture = new Image("assets/entities/aircraft/attack_helicopter.png")
                        .getTexture();
                helicopter_wings_moving_texture = new Image("assets/entities/aircraft/attack_helicopter_wings_moving.png")
                        .getTexture();
                helicopter_wings_idle_texture = new Image("assets/entities/aircraft/attack_helicopter_wings_idle.png")
                        .getTexture();
            }
            base_image = new Image(helicopter_texture);
            helicopter_wings_moving_image = new Image(helicopter_wings_moving_texture);
            helicopter_wings_idle_image = new Image(helicopter_wings_idle_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }

        WINGS_IMAGE_HALF = helicopter_wings_moving_image.getWidth() / 2.f;

        weapons.add(new RocketLauncher(isDrivable));    // WEAPON 1
        weapons.add(new AGM(true));   // WEAPON 2

        super.init();
    }

    static {
        try {
            helicopter_sound = new Sound("audio/sounds/helicopter.ogg");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fly(int deltaTime) {
        position.add(dir);
        //collisionModel.update(base_image.getRotation());
    }

    @Override
    public void increaseSpeed(int deltaTime) {
        accelerate(deltaTime, Direction.FORWARD);
    }

    @Override
    public void decreaseSpeed(int deltaTime) {
        accelerate(deltaTime, Direction.BACKWARDS);
    }

    private void accelerate(int deltaTime, Direction direction) {
        if (isDestroyed) return;
        if (current_speed < getMaxSpeed()) {
            current_speed += ACCELERATION_FACTOR * deltaTime;
            playHelicopterEngineSound();
        } else {
            current_speed = getMaxSpeed();  // cap the max speed
        }
        calculateMovementVector(deltaTime, direction);
    }

    public void decelerate(int deltaTime, Direction direction) {
        if (isDestroyed) return;
        if (current_speed > 0.f) {  // 0.01f and not 0.f because it will take longer to reach 0.f completely!
            current_speed -= DECELERATION_FACTOR * deltaTime;
            playHelicopterEngineSound();
        } else {
            current_speed = 0.f;
            decelerate = false;
            isMoving = false;
        }
        calculateMovementVector(deltaTime, direction);
    }

    public boolean isDecelerating() {
        return decelerate;
    }

    public void startDeceleration(Direction decelerateDirection) {
        decelerate = true;
        this.decelerateDirection = decelerateDirection;
    }

    public void cancelDeceleration() {
        decelerate = false;
    }


    @Override
    public void update(GameContainer gc, int deltaTime) {
        super.update(gc, deltaTime);

        if (isDrivable)
            animatedCrosshair.update(deltaTime, position, getRotation());

        if (!hasLanded)
            helicopter_wings_moving_image.rotate(1.f * deltaTime);


        if (decelerate) {
            decelerate(deltaTime, decelerateDirection);
        }

        current_helicopter_engine_sound_play_time += deltaTime;
        if (current_helicopter_engine_sound_play_time > HELICOPTER_ENGINE_SOUND_MILLIS_TO_WAIT) {
            current_helicopter_engine_sound_play_time = 0;
            canPlayHelicopterEngineSound = true;
        }

        if (!helicopter_sound.playing() && !hasLanded) {
            float pitch = 1.f + current_speed;
            helicopter_sound.play(pitch, UserSettings.soundVolume);
        }
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        // draw the helicopter wings shadow
        if (!hasLanded) {
            helicopter_wings_moving_image.drawFlash(shadow.current_shadow_pos.x - 25, shadow.current_shadow_pos.y - 7,
                    helicopter_wings_moving_image.getWidth(), helicopter_wings_moving_image.getHeight(), Color.black);
        } else {
            helicopter_wings_idle_image.draw(position.x - WINGS_IMAGE_HALF, position.y - WINGS_IMAGE_HALF);
        }
        drawBaseImage();
        if (!hasLanded)
            helicopter_wings_moving_image.draw(position.x - WINGS_IMAGE_HALF, position.y - WINGS_IMAGE_HALF);

        // draw the plane's crosshair
        if (isDrivable && !hasLanded) animatedCrosshair.draw();
    }

    @Override
    protected boolean canLand() {
        // if the helicopter is too fast, it can't land
        if (current_speed > MAX_SPEED_PLAYER - 0.15) return false;

        // check the tile below the helicopter, if it is a collision tile, the helicopter can't land
        int mapX = (int) (position.x / TILE_WIDTH);
        if (mapX < 0 || mapX >= LEVEL_WIDTH_TILES) return false;  // player wants to land out of map
        int mapY = (int) (position.y / TILE_HEIGHT);
        if (mapY < 0 || mapY >= LEVEL_HEIGHT_TILES) return false;  // player wants to land out of map
        int tileID = map.getTileId(mapX, mapY, LANDSCAPE_TILES_LAYER_IDX);
        if (TileMapData.isCollisionTile(tileID)) return false;
        helicopter_sound.stop();
        return true;
    }

    public void playHelicopterEngineSound() {
        if (!isMoving || !canPlayHelicopterEngineSound) return;
        canPlayHelicopterEngineSound = false;
        helicopter_sound.stop();
        float pitch = 1.f + current_speed;
        helicopter_sound.play(pitch, UserSettings.soundVolume);
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

}
