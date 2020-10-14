package models.entities.tanks;

import logic.WayPointManager;
import models.CollisionModel;
import models.entities.MovableEntity;
import models.weapons.Cannon;
import models.weapons.DoubleRocketLauncher;
import models.weapons.Weapon;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class XTank extends Tank {

    private static Texture x_tank_texture, x_tank_turret_texture;

    // attributes
    private static final float ARMOR = 360.f;
    private static final float ROTATE_SPEED_PLAYER = 0.15f, ROTATE_SPEED_BOT = 0.05f;
    private static final float MAX_SPEED_PLAYER = 0.03f, MAX_SPEED_BOT = 0.03f;
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

        collisionModel = new CollisionModel(position, base_image.getWidth(), base_image.getHeight());
        super.init();
    }

    /* the x-tank rotates itself, not a turret */
    @Override
    public void changeAimingDirection(float angle, int deltaTime) {
        float rotation = WayPointManager.getShortestSignedAngle(base_image.getRotation(), angle);

        if (rotation < 0) {
            rotate(RotateDirection.ROTATE_DIRECTION_LEFT, deltaTime);
        } else {
            rotate(RotateDirection.ROTATE_DIRECTION_RIGHT, deltaTime);
        }
    }

    @Override
    public Weapon getWeapon(MovableEntity.WeaponType weaponType) {
        switch (weaponType) {
            case WEAPON_1:
                return weapons.get(0);
            case WEAPON_2:
                return weapons.get(1);
            case MEGA_PULSE:
                return weapons.get(2);
        }
        return null;
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
