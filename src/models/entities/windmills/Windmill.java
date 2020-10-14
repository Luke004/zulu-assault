package models.entities.windmills;

import logic.WayPointManager;
import models.StaticEntity;
import graphics.animations.smoke.SmokeAnimation;
import models.entities.MovableEntity;
import models.weapons.Weapon;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import java.util.Random;

public abstract class Windmill extends StaticEntity {
    protected Image turret;
    protected Vector2f turret_position;
    private SmokeAnimation smokeAnimation;
    private final static int SMOKE_ANIMATION_FREQUENCY = 300;  // two per second
    private int smoke_animation_timer;
    private Random random;

    private static final float ARMOR = 10.f;
    private static final int SCORE_VALUE = 200;
    private static final float TURRET_ROTATE_SPEED = 0.07f;


    public Windmill(Vector2f startPos, boolean isHostile, Vector2f[] tile_positions) {
        super(startPos, isHostile, tile_positions);
        random = new Random();

        smokeAnimation = new SmokeAnimation(3);

        health_bar_position.x = position.x - 27.5f;
        health_bar_position.y = position.y - 35.f;
    }

    public void init() {
        super.init();
    }

    @Override
    public void update(GameContainer gc, int deltaTime) {
        super.update(gc, deltaTime);
        smokeAnimation.update(deltaTime);
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
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        turret.draw(turret_position.x, turret_position.y);
        smokeAnimation.draw();
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
        super.changeHealth(amount, ARMOR);
    }

    @Override
    public int getScoreValue() {
        return SCORE_VALUE;
    }
}
