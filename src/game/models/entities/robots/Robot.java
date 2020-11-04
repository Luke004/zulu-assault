package game.models.entities.robots;

import game.logic.CollisionHandler;
import game.util.WayPointManager;
import game.models.CollisionModel;
import game.models.entities.MovableEntity;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

public abstract class Robot extends MovableEntity {
    private Animation walking_animation;
    private float BASE_WIDTH_HALF, BASE_HEIGHT_HALF;
    private boolean centerTurret;

    // default robot attributes
    protected static final float ROBOT_DEFAULT_ARMOR = 75.f;
    private static final int SCORE_VALUE = 3000;
    private static final float ROTATE_SPEED_PLAYER = 0.17f, ROTATE_SPEED_BOT = 0.05f;
    private static final float MAX_SPEED_PLAYER = 0.15f, MAX_SPEED_BOT = 0.06f;

    public Robot(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);
        current_speed = getMaxSpeed();
    }

    @Override
    public void init() {
        try {
            Image walking_animation_image = new Image("assets/entities/robots/robot_walking_animation.png");
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
        WIDTH_HALF = walking_animation.getImage(0).getWidth() / 2;
        HEIGHT_HALF = walking_animation.getImage(0).getHeight() / 2;
        BASE_WIDTH_HALF = base_image.getWidth() / 2.f;
        BASE_HEIGHT_HALF = base_image.getHeight() / 2.f;
        // just use index 0, all indices are same width and height
        collisionModel = new CollisionModel(position, WIDTH_HALF, HEIGHT_HALF);
        super.init();
    }

    @Override
    public void update(GameContainer gameContainer, int deltaTime) {
        super.update(gameContainer, deltaTime);
        walking_animation.update(deltaTime);

        if (isDestroyed) {
            level_delete_listener.notifyForEntityDestruction(this);
        }

        if (centerTurret) {
            centerTurret(deltaTime);
        }

        // WAY POINTS
        if (waypointManager != null) {
            // move to next vector
            moveForward(deltaTime);
            // rotate the robot towards the next vector until it's pointing towards it
            if (waypointManager.wish_angle != (int) getRotation()) {
                rotate(waypointManager.rotate_direction, deltaTime);
                waypointManager.adjustAfterRotation(this.position, getRotation());
            }

            if (waypointManager.distToNextVector(this.position) < HEIGHT_HALF * 2) {
                waypointManager.setupNextWayPoint(this.position, getRotation());
            }

            if (!isEnemyNear) {
                autoCenterTurret();
            }
        }
    }

    @Override
    public void draw(Graphics graphics) {
        if (isInvincible) {
            if (!invincibility_animation_switch) {
                walking_animation.getCurrentFrame().drawFlash(position.x - WIDTH_HALF, position.y - HEIGHT_HALF);
                base_image.drawFlash(position.x - BASE_WIDTH_HALF, position.y - BASE_HEIGHT_HALF);
            } else {
                walking_animation.draw(position.x - WIDTH_HALF, position.y - HEIGHT_HALF);
                base_image.draw(position.x - BASE_WIDTH_HALF, position.y - BASE_HEIGHT_HALF);
            }
        } else {
            walking_animation.draw(position.x - WIDTH_HALF, position.y - HEIGHT_HALF);
            base_image.draw(position.x - BASE_WIDTH_HALF, position.y - BASE_HEIGHT_HALF);
        }
        super.draw(graphics);

        //collisionModel.draw(game.graphics);
    }

    @Override
    public void drawPreview(Graphics graphics) {
        base_image.draw(position.x, position.y, 0.4f);
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

        if (!CollisionHandler.intersectsWithTileMap(this, true))
            position.x += dir.x;
        if (!CollisionHandler.intersectsWithTileMap(this, false))
            position.y += dir.y;

        collisionModel.update(base_image.getRotation());
    }

    public void moveBackwards(int deltaTime) {
        calculateMovementVector(deltaTime, Direction.BACKWARDS);

        if (!CollisionHandler.intersectsWithTileMap(this, true))
            position.x += dir.x;
        if (!CollisionHandler.intersectsWithTileMap(this, false))
            position.y += dir.y;

        collisionModel.update(base_image.getRotation());
    }

    @Override
    public void addWayPoints(WayPointManager waypointManager) {
        super.addWayPoints(waypointManager);
        // start the animation since the robot is moving because of way points
        this.setMovingForward(true);
        this.startAnimation();
    }

    @Override
    public float getRotation() {
        return walking_animation.getCurrentFrame().getRotation();
    }

    @Override
    public void setRotation(float angle) {
        base_image.setRotation(angle);
        for (int idx = 0; idx < walking_animation.getFrameCount(); ++idx) {
            walking_animation.getImage(idx).setRotation(angle);
        }
    }

    @Override
    public void rotate(RotateDirection rotateDirection, int deltaTime) {
        if (isDestroyed) return;
        switch (rotateDirection) {
            case ROTATE_DIRECTION_LEFT:
                for (int idx = 0; idx < walking_animation.getFrameCount(); ++idx) {
                    walking_animation.getImage(idx).rotate(-getBaseRotateSpeed() * deltaTime);
                }
                base_image.rotate(-getBaseRotateSpeed() * deltaTime);
                break;
            case ROTATE_DIRECTION_RIGHT:
                for (int idx = 0; idx < walking_animation.getFrameCount(); ++idx) {
                    walking_animation.getImage(idx).rotate(getBaseRotateSpeed() * deltaTime);
                }
                base_image.rotate(getBaseRotateSpeed() * deltaTime);
                break;
        }
    }

    public void rotateTurret(RotateDirection r, int deltaTime) {
        switch (r) {
            case ROTATE_DIRECTION_LEFT:
                base_image.rotate(-getTurretRotateSpeed() * deltaTime);
                break;
            case ROTATE_DIRECTION_RIGHT:
                base_image.rotate(getTurretRotateSpeed() * deltaTime);
                break;
        }
    }

    protected void centerTurret(int deltaTime) {
        float angle = WayPointManager.getShortestSignedAngle(this.getRotation(), base_image.getRotation());

        if (angle > -3 && angle < 3) {
            base_image.setRotation(this.getRotation());
            centerTurret = false;
            return;
        }

        if (angle > 0) {
            rotateTurret(RotateDirection.ROTATE_DIRECTION_LEFT, deltaTime);
        } else {
            rotateTurret(RotateDirection.ROTATE_DIRECTION_RIGHT, deltaTime);
        }
    }

    public void autoCenterTurret() {
        centerTurret = true;
    }

    @Override
    public void changeAimingDirection(float angle, int deltaTime) {
        float rotation = WayPointManager.getShortestSignedAngle(base_image.getRotation(), angle);
        if (rotation == 0) return;

        if (rotation < 0) {
            base_image.rotate(-getTurretRotateSpeed() * deltaTime);
        } else {
            base_image.rotate(getTurretRotateSpeed() * deltaTime);
        }
    }

    @Override
    public void changeHealth(float amount) {
        super.changeHealth(amount, ROBOT_DEFAULT_ARMOR);
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
    public int getScoreValue() {
        return SCORE_VALUE;
    }
}
