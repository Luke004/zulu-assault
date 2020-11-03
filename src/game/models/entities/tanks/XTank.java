package game.models.entities.tanks;

import game.models.entities.Entity;
import game.util.WayPointManager;
import game.models.weapons.Cannon;
import game.models.weapons.DoubleRocketLauncher;
import game.models.weapons.Weapon;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class XTank extends Tank {

    private static Texture x_tank_texture, x_tank_turret_texture;

    // attributes
    private static final float ARMOR = 500.f;
    private static final float ROTATE_SPEED_PLAYER = 0.15f, ROTATE_SPEED_BOT = 0.05f;
    private static final float MAX_SPEED_PLAYER = 0.03f, MAX_SPEED_BOT = 0.01f;
    private static final float ACCELERATION_FACTOR = 0.0005f, DECELERATION_FACTOR = 0.0005f;

    public XTank(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        weapons.add(new Cannon(isDrivable, 4));  // WEAPON_1
        weapons.add(new DoubleRocketLauncher(isDrivable, 3));  // WEAPON_2

        // LOAD TEXTURES
        try {
            if (x_tank_texture == null) {
                x_tank_texture = new Image("assets/entities/tanks/x_tank.png").getTexture();
            }
            base_image = new Image(x_tank_texture);
            if (x_tank_turret_texture == null) {
                x_tank_turret_texture =
                        new Image("assets/entities/tanks/meme_car_turret.png").getTexture();
            }
            turret = new Image(x_tank_turret_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }
        super.init();
    }

    /* The x-tank rotates itself, not a turret. It also follows the game.player. */
    @Override
    public void changeAimingDirection(float angle, int deltaTime) {
        float rotation = WayPointManager.getShortestSignedAngle(base_image.getRotation(), angle);

        if (rotation < 0) {
            rotate(RotateDirection.ROTATE_DIRECTION_LEFT, deltaTime);
        } else {
            rotate(RotateDirection.ROTATE_DIRECTION_RIGHT, deltaTime);
        }

        // follow the game.player
        accelerate(deltaTime, Direction.FORWARD);
    }

    @Override
    public void drawPreview(Graphics graphics) {
        this.base_image.draw(position.x, position.y, 0.3f);
    }

    @Override
    public int getScoreValue() {
        return 5000;
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
