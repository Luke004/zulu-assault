package models.entities.windmills;

import logic.WayPointManager;
import graphics.animations.smoke.SmokeAnimation;
import models.CollisionModel;
import models.entities.Entity;
import models.entities.MovableEntity;
import models.weapons.Weapon;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import java.util.Random;

public abstract class Windmill extends Entity {

    protected Image turret;
    protected Vector2f turret_position;
    private SmokeAnimation smokeAnimation;
    private final static int SMOKE_ANIMATION_FREQUENCY = 300;  // two per second
    private int smoke_animation_timer;
    private Random random;

    private static final float WINDMILL_DEFAULT_ARMOR = 30.f;
    private static final int WINDMILL_DEFAULT_SCORE_VALUE = 200;
    private static final float TURRET_ROTATE_SPEED = 0.07f;


    public Windmill(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);

        random = new Random();

        smokeAnimation = new SmokeAnimation(3);

        health_bar_position.x = position.x - 27.5f;
        health_bar_position.y = position.y - 35.f;
    }

    @Override
    public void init() {
        collisionModel = new CollisionModel(position, base_image.getWidth(), base_image.getHeight());
        collisionModel.update(0);
        WIDTH_HALF = base_image.getWidth() / 2;
        HEIGHT_HALF = base_image.getHeight() / 2;
        super.init();
    }

    @Override
    public void update(GameContainer gc, int deltaTime) {
        super.update(gc, deltaTime);
        smokeAnimation.update(deltaTime);
        // draw smoke animation when below half health
        if (current_health < MAX_HEALTH / 2) {
            smoke_animation_timer += deltaTime;
            if (smoke_animation_timer > SMOKE_ANIMATION_FREQUENCY) {
                smoke_animation_timer = 0;
                int rotation = random.nextInt(360);
                float xVal = (float) (position.x + -Math.sin(((rotation) * Math.PI) / 180) * -30.f);
                float yVal = (float) (position.y + Math.cos(((rotation) * Math.PI) / 180) * -30.f);
                smokeAnimation.play(xVal, yVal, rotation);
            }
        }

        if (isDestroyed) {
            level_delete_listener.notifyForEntityDestruction(this);
        }
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        base_image.draw(position.x - WIDTH_HALF, position.y - HEIGHT_HALF);
        turret.draw(turret_position.x, turret_position.y);
        smokeAnimation.draw();
    }

    @Override
    public void drawPreview(Graphics graphics) {
        base_image.draw(position.x, position.y, 0.6f);
    }

    @Override
    public void fireWeapon(MovableEntity.WeaponType weaponType) {
        Weapon weapon = null;
        switch (weaponType) {
            case WEAPON_1:
                weapon = getWeapon(MovableEntity.WeaponType.WEAPON_1);
                break;
            case WEAPON_2:
                weapon = getWeapon(MovableEntity.WeaponType.WEAPON_2);
                break;
            case MEGA_PULSE:
                weapon = getWeapon(MovableEntity.WeaponType.MEGA_PULSE);
                break;
        }
        if (weapon == null) return;  // does not have a WEAPON_2, so return
        weapon.fire(position.x, position.y, turret.getRotation());
    }

    @Override
    public void changeAimingDirection(float angle, int deltaTime) {
        float rotation = WayPointManager.getShortestSignedAngle(turret.getRotation(), angle);
        if (rotation < 0) {
            turret.rotate(-TURRET_ROTATE_SPEED * deltaTime);
        } else {
            turret.rotate(TURRET_ROTATE_SPEED * deltaTime);
        }
    }

    @Override
    public void changeHealth(float amount) {
        super.changeHealth(amount, WINDMILL_DEFAULT_ARMOR);
    }

    @Override
    public int getScoreValue() {
        return WINDMILL_DEFAULT_SCORE_VALUE;
    }
}
