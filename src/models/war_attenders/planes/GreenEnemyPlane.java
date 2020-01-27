package models.war_attenders.planes;

import logic.WayPointManager;
import models.CollisionModel;
import models.animations.other.AnimatedCrosshair;
import models.war_attenders.MovableWarAttender;
import models.war_attenders.WarAttender;
import models.weapons.AGM;
import models.weapons.Uzi;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import java.util.List;

public class GreenEnemyPlane extends Plane {

    private AnimatedCrosshair animatedCrosshair;
    private boolean isEnemyNear;

    public GreenEnemyPlane(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        // attributes equal for humans and bots
        max_health = 100;
        current_health = max_health;
        armor = 45; // unknown

        scoreValue = 500;

        setMoving(true);    // green plane is always flying

        if (isDrivable) {
            animatedCrosshair = new AnimatedCrosshair();
            // individual GreenEnemyPlane attributes for human players
            max_speed = 0.15f;
            rotate_speed = 0.15f;
        } else {
            // individual GreenEnemyPlane attributes for bots
            max_speed = 0.2f;
            rotate_speed = 0.15f;
        }

        current_speed = max_speed;  // speed is always the same for this plane

        weapons.add(new Uzi(isDrivable));  // WEAPON_1
        weapons.add(new AGM(isDrivable));  // WEAPON_2

        try {
            base_image = new Image("assets/war_attenders/planes/green_enemy_plane.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        collisionModel = new CollisionModel(position, base_image.getWidth(), base_image.getHeight());
        super.init();
    }

    @Override
    public void update(GameContainer gc, int deltaTime) {
        super.update(gc, deltaTime);
        if (isMoving) {
            fly(deltaTime); // the plane is always flying forward
        }
        if (isDrivable)
            animatedCrosshair.update(deltaTime, position, getRotation());

        // WAY POINTS
        if (waypointManager != null) {
            if (!isEnemyNear) {
                // rotate the plane towards the next vector until it's pointing towards it
                if (waypointManager.wish_angle != (int) getRotation()) {
                    rotate(waypointManager.rotate_direction, deltaTime);
                    waypointManager.adjustAfterRotation(this.position, getRotation());
                }

                if (waypointManager.distToNextVector(this.position) < HEIGHT_HALF * 2) {
                    waypointManager.setupNextWayPoint(this.position, getRotation());
                }
            }
        }
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        // draw the plane's crosshair
        if (isDrivable && isMoving) animatedCrosshair.draw();
    }

    public void fly(int deltaTime) {
        calculateMovementVector(deltaTime, Direction.FORWARD);
        position.add(dir);
        collisionModel.update(base_image.getRotation());
    }

    @Override
    public void shootAtEnemies(MovableWarAttender player, List<? extends WarAttender> enemies_of_warAttender, int deltaTime) {
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
        for (WarAttender enemy_war_attender : enemies_of_warAttender) {
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
        isEnemyNear = false;
        float rotationDegree;
        if (dist < 750) {
            isEnemyNear = true;

            rotationDegree = WayPointManager.calculateAngleToRotateTo(position, new Vector2f(xPos, yPos));

            changeAimingDirection(rotationDegree, deltaTime);
        }
        if (dist < 500) {
            // fire
            fireWeapon(MovableWarAttender.WeaponType.WEAPON_1);
            fireWeapon(WeaponType.WEAPON_2);   // green plane can also shoot wpn2
        }
    }

    @Override
    public void onCollision(MovableWarAttender enemy) {
        // a plane doesn't have collisions
    }

    @Override
    public void blockMovement() {
        // a plane doesn't get its movement blocked
    }

    @Override
    public float getRotation() {
        return base_image.getRotation();
    }

    @Override
    public void setRotation(float degree) {
        base_image.setRotation(degree);
    }

    @Override
    public void fireWeapon(WeaponType weapon) {
        switch (weapon) {
            case WEAPON_1:
                weapons.get(0).fire(position.x, position.y, base_image.getRotation());
                break;
            case WEAPON_2:
                if (weapons.size() < 2) return;    // does not have a WEAPON_2, so return
                weapons.get(1).fire(position.x, position.y, base_image.getRotation());
                break;
            case MEGA_PULSE:
                if (weapons.size() == 2) {   // does not have a WEAPON_2, MEGA_PULSE it at index [1]
                    weapons.get(1).fire(position.x, position.y, base_image.getRotation());
                } else {    // does have a WEAPON_2, MEGA_PULSE it at index [2]
                    weapons.get(2).fire(position.x, position.y, base_image.getRotation());
                }
                break;
        }
    }

    @Override
    public void changeAimingDirection(float angle, int deltaTime) {
        float rotation_to_make = WayPointManager.getShortestSignedAngle(base_image.getRotation(), angle);

        if (rotation_to_make > 0) {
            base_image.rotate(rotate_speed * deltaTime);
        } else {
            base_image.rotate(-rotate_speed * deltaTime);
        }
    }
}
