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
import org.newdawn.slick.opengl.Texture;

import java.util.List;

public class GreenEnemyPlane extends Plane {

    private static Texture green_enemy_plane_texture;

    private AnimatedCrosshair animatedCrosshair;

    private static final float ARMOR = 40.f;
    private static final int SCORE_VALUE = 500;
    private static final float ROTATE_SPEED_PLAYER = 0.15f, ROTATE_SPEED_BOT = 0.15f;
    private static final float MAX_SPEED_PLAYER = 0.15f, MAX_SPEED_BOT = 0.2f;

    public GreenEnemyPlane(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        setMoving(true);    // green plane is always flying

        if (isDrivable) animatedCrosshair = new AnimatedCrosshair();

        current_speed = getMaxSpeed();  // speed is always the same for this plane

        weapons.add(new Uzi(isDrivable));  // WEAPON_1
        weapons.add(new AGM(isDrivable));  // WEAPON_2

        // LOAD TEXTURES
        try {
            if (green_enemy_plane_texture == null) {
                green_enemy_plane_texture = new Image("assets/war_attenders/planes/green_enemy_plane.png")
                        .getTexture();
            }
            base_image = new Image(green_enemy_plane_texture);
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
        float rotationDegree;
        if (dist < 450) {
            isEnemyNear = true;
            rotationDegree = WayPointManager.calculateAngleToRotateTo(position, new Vector2f(xPos, yPos));
            changeAimingDirection(rotationDegree, deltaTime);
            fireWeapon(MovableWarAttender.WeaponType.WEAPON_1);
            fireWeapon(WeaponType.WEAPON_2);   // green plane can also shoot wpn2
        } else {
            isEnemyNear = false;
        }
    }

    @Override
    public void onCollision(MovableWarAttender enemy) {
        // a plane doesn't have collisions
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
            base_image.rotate(getBaseRotateSpeed() * deltaTime);
        } else {
            base_image.rotate(-getBaseRotateSpeed() * deltaTime);
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
        super.changeHealth(amount, ARMOR);
    }

    @Override
    public int getScoreValue() {
        return SCORE_VALUE;
    }
}
