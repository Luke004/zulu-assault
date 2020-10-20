package models.entities.aircraft.hostile;

import graphics.animations.other.AnimatedCrosshair;
import logic.WayPointManager;
import models.entities.MovableEntity;
import models.entities.Entity;
import models.entities.aircraft.friendly.Aircraft;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import java.util.List;

public abstract class EnemyFlyingEntity extends Aircraft {

    private AnimatedCrosshair animatedCrosshair;

    public EnemyFlyingEntity(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        if (isDrivable) animatedCrosshair = new AnimatedCrosshair();

        current_speed = getMaxSpeed();  // speed is always the same
    }

    @Override
    public void update(GameContainer gc, int deltaTime) {
        super.update(gc, deltaTime);
        if (isDrivable)
            animatedCrosshair.update(deltaTime, position, getRotation());
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        // draw the plane's crosshair
        if (isDrivable && !hasLanded) animatedCrosshair.draw();
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
            // player not null means it's a hostile tank
            // calculate dist between the player and the enemy
            xPos = player.getPosition().x;
            yPos = player.getPosition().y;
            dist = (float) Math.sqrt((xPos - position.x) * (xPos - position.x)
                    + (yPos - position.y) * (yPos - position.y));
        } else {
            // player null means it's a friendly tank
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

}
