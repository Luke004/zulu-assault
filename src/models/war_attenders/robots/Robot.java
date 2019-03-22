package models.war_attenders.robots;

import models.CollisionModel;
import models.war_attenders.MovableWarAttender;
import models.war_attenders.tanks.Tank;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

public abstract class Robot extends MovableWarAttender {
    Animation walking_animation;
    private int ROBOT_WIDTH_HALF, ROBOT_HEIGHT_HALF;

    public Robot(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);
    }

    public void init() {
        try {
            Image walking_animation_image = new Image("assets/war_attenders/robots/robot_walking_animation.png");
            walking_animation = new Animation(false);
            int x = 0;
            do {
                walking_animation.addFrame(walking_animation_image.getSubImage(x, 0, 50, 50), 300);
                x += 50;
            } while (x <= 200);
            walking_animation.setCurrentFrame(2);
            walking_animation.setLooping(true);
            walking_animation.setPingPong(true);
            walking_animation.stop();
        } catch (SlickException e) {
            e.printStackTrace();
        }
        ROBOT_WIDTH_HALF = walking_animation.getImage(0).getWidth() / 2;
        ROBOT_HEIGHT_HALF = walking_animation.getImage(0).getHeight() / 2;
        // just use index 0, all indices are same width and height
        collisionModel = new CollisionModel(position, walking_animation.getImage(0).getWidth(), walking_animation.getImage(0).getHeight());
        super.init();
    }

    @Override
    public void update(GameContainer gameContainer, int deltaTime) {
        super.update(gameContainer, deltaTime);
        walking_animation.update(deltaTime);
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);

        if (isInvincible) {
            if (!invincibility_animation_switch) {
                walking_animation.getCurrentFrame().drawFlash(position.x - ROBOT_WIDTH_HALF, position.y - ROBOT_HEIGHT_HALF);
                base_image.drawFlash(position.x - ROBOT_WIDTH_HALF, position.y - ROBOT_HEIGHT_HALF);
            } else {
                walking_animation.draw(position.x - ROBOT_WIDTH_HALF, position.y - ROBOT_HEIGHT_HALF);
                base_image.draw(position.x - ROBOT_WIDTH_HALF, position.y - ROBOT_HEIGHT_HALF);
            }
        } else {
            walking_animation.draw(position.x - ROBOT_WIDTH_HALF, position.y - ROBOT_HEIGHT_HALF);
            base_image.draw(position.x - ROBOT_WIDTH_HALF, position.y - ROBOT_HEIGHT_HALF);
        }

        //collisionModel.draw(graphics);
    }

    public void startAnimation() {
        walking_animation.start();
    }

    public void stopAnimation() {
        walking_animation.stop();
        walking_animation.setCurrentFrame(2);
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

    @Override
    public float getRotation() {
        return walking_animation.getCurrentFrame().getRotation();
    }

    @Override
    public void rotate(RotateDirection rotateDirection, int deltaTime) {
        switch (rotateDirection) {
            case ROTATE_DIRECTION_LEFT:
                for (int idx = 0; idx < walking_animation.getFrameCount(); ++idx) {
                    walking_animation.getImage(idx).rotate(-rotate_speed * deltaTime);
                }
                base_image.rotate(-rotate_speed * deltaTime);
                break;
            case ROTATE_DIRECTION_RIGHT:
                for (int idx = 0; idx < walking_animation.getFrameCount(); ++idx) {
                    walking_animation.getImage(idx).rotate(rotate_speed * deltaTime);
                }
                base_image.rotate(rotate_speed * deltaTime);
                break;
        }
    }

    public void rotateTurret(RotateDirection r, int deltaTime) {
        switch (r) {
            case ROTATE_DIRECTION_LEFT:
                base_image.rotate(-rotate_speed * deltaTime);
                break;
            case ROTATE_DIRECTION_RIGHT:
                base_image.rotate(rotate_speed * deltaTime);
                break;
        }
    }

    @Override
    public void rotateTowardsPlayer(float angle) {
        base_image.setRotation(angle);
    }

    @Override
    public void fireWeapon(WeaponType weapon) {
        switch (weapon) {
            case WEAPON_1:
                weapons.get(0).fire(position.x, position.y, base_image.getRotation());
                break;
            case WEAPON_2:
                if (weapons.size() == 2) return;    // does not have a WEAPON_2, so return
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
}
