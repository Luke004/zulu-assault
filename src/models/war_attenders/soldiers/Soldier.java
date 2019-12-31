package models.war_attenders.soldiers;

import logic.WayPointManager;
import models.CollisionModel;
import models.war_attenders.MovableWarAttender;
import models.war_attenders.WarAttender;
import models.war_attenders.robots.Robot;
import models.war_attenders.tanks.Tank;
import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import java.util.List;

public abstract class Soldier extends MovableWarAttender {
    Animation animation;

    public Soldier(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);
        scoreValue = 100;
    }

    public void init() {
        animation = new Animation(false);
        int x = 0;
        do {
            animation.addFrame(base_image.getSubImage(x, 0, 12, 12), 300);
            x += 12;
        } while (x <= 24);
        animation.setCurrentFrame(1);
        animation.setLooping(true);
        animation.setPingPong(true);
        animation.stop();

        WIDTH_HALF = animation.getImage(0).getWidth() / 2;
        HEIGHT_HALF = animation.getImage(0).getHeight() / 2;

        // just use index 0, all indices are same width and height
        collisionModel = new CollisionModel(position, animation.getImage(0).getWidth(), animation.getImage(0).getHeight());
        super.init();
    }

    @Override
    public void update(GameContainer gameContainer, int deltaTime) {
        super.update(gameContainer, deltaTime);
        animation.update(deltaTime);

        if(isDestroyed){
            level_delete_listener.notifyForDeletion(this);
        }
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);

        if (isInvincible) {
            if (!invincibility_animation_switch) {
                animation.getCurrentFrame().drawFlash(position.x - WIDTH_HALF, position.y - HEIGHT_HALF);
            } else {
                animation.draw(position.x - WIDTH_HALF, position.y - HEIGHT_HALF);
            }
        } else {
            animation.draw(position.x - WIDTH_HALF, position.y - HEIGHT_HALF);
        }

        //collisionModel.draw(graphics);
    }

    public void startAnimation() {
        animation.start();
    }

    public void stopAnimation() {
        animation.stop();
    }

    public void moveForward(int deltaTime) {
        calculateMovementVector(deltaTime, Direction.FORWARD);
        position.add(dir);
        collisionModel.update(base_image.getRotation());
    }

    public void moveBackwards(int deltaTime) {
        calculateMovementVector(deltaTime, Direction.BACKWARDS);
        position.add(dir);
        collisionModel.update(base_image.getRotation());
    }

    @Override
    public void onCollision(MovableWarAttender enemy) {
        if (enemy instanceof Tank) {  // enemy is a tank
            blockMovement();
        }
        // soldier is not needed, nothing happens
        // plane instanceof is not needed, nothing happens
    }

    @Override
    public void blockMovement() {
        position.sub(dir);  // set the position on last position before the collision
        collisionModel.update(base_image.getRotation());    // update collision model
    }

    public void setPosition(Vector2f position) {
        this.position.x = position.x;
        this.position.y = position.y;
        collisionModel.update(base_image.getRotation());
    }

    @Override
    public void rotate(RotateDirection rotateDirection, int deltaTime) {
        switch (rotateDirection) {
            case ROTATE_DIRECTION_LEFT:
                for (int idx = 0; idx < animation.getFrameCount(); ++idx) {
                    animation.getImage(idx).rotate(-rotate_speed * deltaTime);
                }
                break;
            case ROTATE_DIRECTION_RIGHT:
                for (int idx = 0; idx < animation.getFrameCount(); ++idx) {
                    animation.getImage(idx).rotate(rotate_speed * deltaTime);
                }
                break;
        }
    }

    @Override
    public void setRotation(float angle) {
        float rotation = WayPointManager.getShortestAngle(animation.getImage(0).getRotation(), angle);
        if(rotation == 0) return;

        if (rotation < 0) {
            for (int idx = 0; idx < animation.getFrameCount(); ++idx) {
                animation.getImage(idx).rotate(-rotate_speed * 5);
            }
        } else {
            for (int idx = 0; idx < animation.getFrameCount(); ++idx) {
                animation.getImage(idx).rotate(rotate_speed * 5);
            }
        }
    }

    @Override
    public float getRotation() {
        return animation.getCurrentFrame().getRotation();
    }


    @Override
    public void fireWeapon(WeaponType weapon) {
        switch (weapon) {
            case WEAPON_1:
                weapons.get(0).fire(position.x, position.y, animation.getCurrentFrame().getRotation());
                break;
            case WEAPON_2:
                if (weapons.size() == 2) return;    // does not have a WEAPON_2, so return
                weapons.get(1).fire(position.x, position.y, animation.getCurrentFrame().getRotation());
                break;
            case MEGA_PULSE:
                if (weapons.size() == 2) {   // does not have a WEAPON_2, MEGA_PULSE it at index [1]
                    weapons.get(1).fire(position.x, position.y, animation.getCurrentFrame().getRotation());
                } else {    // does have a WEAPON_2, MEGA_PULSE it at index [2]
                    weapons.get(2).fire(position.x, position.y, animation.getCurrentFrame().getRotation());
                }
                break;
        }
    }

    @Override
    public void shootAtEnemies(MovableWarAttender player, List<? extends WarAttender> friendly_war_attenders, int deltaTime) {
        MovableWarAttender closest_warAttender = player;
        // calculate dist between the player and the enemy
        float xPos = player.position.x;
        float yPos = player.position.y;
        float dist = (float) Math.sqrt((xPos - position.x) * (xPos - position.x)
                + (yPos - position.y) * (yPos - position.y));

        // calculate dist between each friend and the enemy
        for (WarAttender friendly_war_attender : friendly_war_attenders) {
            float next_xPos = friendly_war_attender.position.x;
            float next_yPos = friendly_war_attender.position.y;
            float next_dist = (float) Math.sqrt((next_xPos - position.x) * (next_xPos - position.x)
                    + (next_yPos - position.y) * (next_yPos - position.y));
            if (next_dist < dist) {
                dist = next_dist;
                xPos = next_xPos;
                yPos = next_yPos;
                closest_warAttender = (MovableWarAttender) friendly_war_attender;
            }
        }

        // flee when the closest warAttender gets too close and is a tank or a robot
        if (dist < 100 && (closest_warAttender instanceof Tank || closest_warAttender instanceof Robot)) {
            setRotation(closest_warAttender.getRotation() + 105.f);
            moveForward(deltaTime);
        } else if (dist < 500) {
            // aim at the closest enemy and fire
            float rotationDegree = WayPointManager.calculateAngle(position, new Vector2f(xPos, yPos));

            setRotation(rotationDegree);

            fireWeapon(MovableWarAttender.WeaponType.WEAPON_1);
        }
    }
}
