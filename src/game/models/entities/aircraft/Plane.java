package game.models.entities.aircraft;

import game.util.WayPointManager;
import game.models.entities.Entity;
import game.models.entities.MovableEntity;
import org.newdawn.slick.geom.Vector2f;

import java.util.List;

public abstract class Plane extends Aircraft {

    private static final float PLANE_DEFAULT_ARMOR = 40.f;
    private static final float ROTATE_SPEED_PLAYER = 0.15f, ROTATE_SPEED_BOT = 0.15f;
    private static final float MAX_SPEED_PLAYER = 0.25f, MAX_SPEED_BOT = 0.2f;

    public Plane(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);
        current_speed = getMaxSpeed();  // the speed of planes is always the same
    }

    /* don't use following 2 methods, enemy planes always fly at the same speed */
    @Override
    public void increaseSpeed(int deltaTime) {
    }

    @Override
    public void decreaseSpeed(int deltaTime) {
    }

    @Override
    public void shootAtEnemies(MovableEntity player, List<? extends Entity> enemies_of_entity, int deltaTime) {
        if (isDestroyed) return;
        float xPos, yPos, dist;
        if (player != null) {
            // game.player not null means it's a hostile tank
            // calculate dist between the game.player and the enemy
            xPos = player.getPosition().x;
            yPos = player.getPosition().y;
            dist = (float) Math.sqrt((xPos - position.x) * (xPos - position.x)
                    + (yPos - position.y) * (yPos - position.y));
        } else {
            // game.player null means it's a friendly tank
            xPos = 0;
            yPos = 0;
            dist = Float.MAX_VALUE;
        }
        // calculate dist between each tank and all its enemies
        for (Entity enemy_war_attender : enemies_of_entity) {
            float next_xPos = enemy_war_attender.getPosition().x;
            float next_yPos = enemy_war_attender.getPosition().y;
            float next_dist = WayPointManager.dist(position, new Vector2f(next_xPos, next_yPos));

            if (next_dist < dist) {
                dist = next_dist;
                xPos = next_xPos;
                yPos = next_yPos;
            }
        }

        // follow the closest enemy
        float rotationDegree;
        if (dist < 380) {
            isEnemyNear = true;
            rotationDegree = WayPointManager.calculateAngleToRotateTo(position, new Vector2f(xPos, yPos));
            changeAimingDirection(rotationDegree, deltaTime);
            fireWeapon(MovableEntity.WeaponType.WEAPON_1);
            fireWeapon(WeaponType.WEAPON_2);   // green plane can also shoot wpn2
        } else {
            isEnemyNear = false;
        }
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
        super.changeHealth(amount, PLANE_DEFAULT_ARMOR);
    }

}
