package game.models.entities.tanks;

import game.logic.CollisionHandler;
import game.util.WayPointManager;
import game.models.CollisionModel;
import game.models.entities.MovableEntity;
import game.models.weapons.Weapon;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import java.util.Random;

public abstract class Tank extends MovableEntity {
    public Image turret;
    private boolean centerTurret, isTurretCentered;
    private int TURRET_WIDTH_HALF, TURRET_HEIGHT_HALF;

    // for deceleration
    private boolean decelerate;
    Direction decelerateDirection;

    // default tank attributes
    private static final float TANK_DEFAULT_ARMOR = 50.f;
    private static final int TANK_DEFAULT_SCORE_VALUE = 1000;

    private DestructionAnimation destructionAnimation;

    public Tank(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);
        isTurretCentered = true;
    }

    public void init() {
        WIDTH_HALF = base_image.getWidth() / 2;
        HEIGHT_HALF = base_image.getHeight() / 2;
        TURRET_WIDTH_HALF = turret.getWidth() / 2;
        TURRET_HEIGHT_HALF = turret.getHeight() / 2;
        destructionAnimation = new DestructionAnimation();
        collisionModel = new CollisionModel(position, base_image.getWidth(), base_image.getHeight());
        super.init();
    }

    @Override
    public void update(GameContainer gc, int deltaTime) {
        super.update(gc, deltaTime);

        if (decelerate) {
            decelerate(deltaTime, decelerateDirection);
        }

        if (isDestroyed) {
            destructionAnimation.play(deltaTime);
            if (destructionAnimation.hasFinished()) {
                level_delete_listener.notifyForEntityDestruction(this);
            }
        }

        if (centerTurret) {
            centerTurret(deltaTime);
        }

        // WAY POINTS
        if (waypointManager != null) {
            // move to next vector
            accelerate(deltaTime, Direction.FORWARD);
            // rotate the tank towards the next vector until it's pointing towards it
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
                base_image.drawFlash(position.x - WIDTH_HALF, position.y - HEIGHT_HALF);
                turret.drawFlash(position.x - TURRET_WIDTH_HALF, position.y - TURRET_HEIGHT_HALF);
            } else {
                base_image.draw(position.x - WIDTH_HALF, position.y - HEIGHT_HALF);
                turret.draw(position.x - TURRET_WIDTH_HALF, position.y - TURRET_HEIGHT_HALF);
            }
        } else {
            base_image.draw(position.x - WIDTH_HALF, position.y - HEIGHT_HALF);
            turret.draw(position.x - TURRET_WIDTH_HALF, position.y - TURRET_HEIGHT_HALF);
        }

        if (isDestroyed) {
            destructionAnimation.draw(graphics);
        }

        super.draw(graphics);
    }

    @Override
    public void drawPreview(Graphics graphics) {
        base_image.draw(position.x, position.y, 0.4f);
        turret.draw(position.x, position.y, 0.4f);
    }

    public void accelerate(int deltaTime, Direction direction) {
        if (isDestroyed) return;
        if (current_speed < getMaxSpeed()) {
            current_speed += getAccelerationFactor() * deltaTime;
        } else {
            current_speed = getMaxSpeed();  // cap the max speed
        }
        calculateMovementVector(deltaTime, direction);
        if (!CollisionHandler.intersectsWithTileMap(this, true))   // check x pos collision
            position.x += dir.x;
        if (!CollisionHandler.intersectsWithTileMap(this, false))    // check y pos collision
            position.y += dir.y;
    }

    /* get the individual max speed of a tank */
    public abstract float getMaxSpeed();

    public void decelerate(int deltaTime, Direction direction) {
        if (isDestroyed) return;
        if (current_speed > 0.f) {  // 0.01f and not 0.f because it will take longer to reach 0.f completely!
            current_speed -= getDecelerationFactor() * deltaTime;
        } else {
            current_speed = 0.f;
            decelerate = false;
            isMoving = false;
        }
        calculateMovementVector(deltaTime, direction);
        if (!CollisionHandler.intersectsWithTileMap(this, true))
            position.x += dir.x;
        if (!CollisionHandler.intersectsWithTileMap(this, false))
            position.y += dir.y;
    }

    @Override
    public void blockMovement() {
        if (isDestroyed) return;
        position.sub(dir);  // set the position on last position before the collision
        collisionModel.update(base_image.getRotation());    // update collision model
        current_speed = 0.f;    // set the speed to zero -> reset velocity
    }

    public boolean isDecelerating() {
        return decelerate;
    }

    public void startDeceleration(Direction decelerateDirection) {
        decelerate = true;
        this.decelerateDirection = decelerateDirection;
    }

    public void cancelDeceleration() {
        decelerate = false;
    }

    public void autoCenterTurret() {
        if (!isTurretCentered) centerTurret = true;
    }

    protected void centerTurret(int deltaTime) {
        if (isTurretCentered) return;
        float angle = WayPointManager.getShortestSignedAngle(this.getRotation(), turret.getRotation());

        if (angle > -3 && angle < 3) {
            turret.setRotation(this.getRotation());
            isTurretCentered = true;
            centerTurret = false;
            return;
        }

        if (angle > 0) {
            rotateTurret(RotateDirection.ROTATE_DIRECTION_LEFT, deltaTime);
        } else {
            rotateTurret(RotateDirection.ROTATE_DIRECTION_RIGHT, deltaTime);
        }
    }

    @Override
    public float getRotation() {
        return base_image.getRotation();
    }

    @Override
    public int getScoreValue() {
        return TANK_DEFAULT_SCORE_VALUE;
    }

    @Override
    public void rotate(RotateDirection r, int deltaTime) {
        if (isDestroyed) return;
        float degree;
        switch (r) {
            case ROTATE_DIRECTION_LEFT:
                degree = -getBaseRotateSpeed() * deltaTime;
                base_image.rotate(degree);
                turret.rotate(degree);
                break;
            case ROTATE_DIRECTION_RIGHT:
                degree = getBaseRotateSpeed() * deltaTime;
                base_image.rotate(degree);
                turret.rotate(degree);
                break;
        }
    }

    @Override
    public void setRotation(float angle) {
        this.base_image.setRotation(angle);
        this.turret.setRotation(angle);
    }

    @Override
    public void changeAimingDirection(float angle, int deltaTime) {
        float rotation = WayPointManager.getShortestSignedAngle(turret.getRotation(), angle);

        if (rotation < 0) {
            rotateTurret(RotateDirection.ROTATE_DIRECTION_LEFT, deltaTime);
        } else {
            rotateTurret(RotateDirection.ROTATE_DIRECTION_RIGHT, deltaTime);
        }
    }

    @Override
    public float getAimingDirection() {
        return turret.getRotation();
    }

    public void rotateTurret(RotateDirection r, int deltaTime) {
        if (isDestroyed) return;
        isTurretCentered = false;
        switch (r) {
            case ROTATE_DIRECTION_LEFT:
                turret.rotate(-getTurretRotateSpeed() * deltaTime);
                break;
            case ROTATE_DIRECTION_RIGHT:
                turret.rotate(getTurretRotateSpeed() * deltaTime);
                break;
        }
    }

    protected abstract float getAccelerationFactor();

    protected abstract float getDecelerationFactor();

    @Override
    public void changeHealth(float amount) {
        super.changeHealth(amount, TANK_DEFAULT_ARMOR);
    }

    private class DestructionAnimation {
        private long oscillation_timer, line_change_timer;
        private final static int OSCILLATION_CHANGE = 100;
        private final static int LINE_CHANGE = 600;
        private final static int MAXIMUM_LINE_CHANGES = 10;
        private Random random;
        private MyLine my_first_line;
        private MyLine my_second_line;
        private int finish_destruction_counter;
        private boolean firstCall;

        DestructionAnimation() {
            random = new Random();
            my_first_line = new MyLine();
            my_second_line = new MyLine();
            firstCall = true;
        }

        public void draw(Graphics graphics) {
            graphics.setColor(new Color(196, 200, 249));
            graphics.setLineWidth(3.f);
            my_first_line.draw(graphics);
            my_second_line.draw(graphics);
        }

        public void play(int deltaTime) {
            if (firstCall) {
                firstCall = false;
                my_first_line.defineNewLine();
                my_second_line.defineNewLine();
            }

            oscillation_timer += deltaTime;
            line_change_timer += deltaTime;
            if (oscillation_timer > OSCILLATION_CHANGE) {
                oscillation_timer = 0;
                my_first_line.oscillateMidPoints();
                my_second_line.oscillateMidPoints();
            }
            if (line_change_timer > LINE_CHANGE) {
                finish_destruction_counter++;
                line_change_timer = 0;
                my_first_line.defineNewLine();
                my_second_line.defineNewLine();
            }
        }

        boolean hasFinished() {
            return finish_destruction_counter == MAXIMUM_LINE_CHANGES;
        }


        private class MyLine {
            Vector2f line_start, mid_point_1, mid_point_1_oscillation, mid_point_2, mid_point_2_oscillation, line_end;

            MyLine() {
                line_start = new Vector2f();
                mid_point_1 = new Vector2f();
                mid_point_1_oscillation = new Vector2f();
                mid_point_2 = new Vector2f();
                mid_point_2_oscillation = new Vector2f();
                line_end = new Vector2f();
                //collisionModel.update(getRotation());
                //defineNewLine();
            }

            void draw(Graphics graphics) {
                graphics.drawLine(line_start.x, line_start.y, mid_point_1.x, mid_point_1.y);
                graphics.drawLine(mid_point_1.x, mid_point_1.y, mid_point_2.x, mid_point_2.y);
                graphics.drawLine(mid_point_2.x, mid_point_2.y, line_end.x, line_end.y);
            }

            private void defineNewLine() {
                // go from tank middle in a random direction (360°) for 'TANK_WIDTH_HALF' units and pick this point
                // as 'line_start'
                int angle = random.nextInt(360);
                float xDir = (float) Math.sin(angle * Math.PI / 180);
                float yDir = (float) -Math.cos(angle * Math.PI / 180);
                xDir *= WIDTH_HALF;
                yDir *= HEIGHT_HALF;
                line_start.x = position.x + xDir;
                line_start.y = position.y + yDir;
                angle -= getRotation();
                if (angle < 0) angle *= -1;
                // connect line_start to one of the opposite collision corners
                if (angle >= 360) angle -= 360;
                int pointIdx;
                if (angle > 270 || angle < 90) {
                    pointIdx = random.nextInt(3 + 1 - 2) + 2;
                } else {
                    pointIdx = random.nextInt(1);
                }
                line_end.x = collisionModel.collision_points[pointIdx].x;
                line_end.y = collisionModel.collision_points[pointIdx].y;

                // define the two mid points between 'line_start' and 'line_end' which will be oscillating
                float diff_X = line_end.x - line_start.x;
                float diff_Y = line_end.y - line_start.y;
                float interval_X = diff_X / 3;
                float interval_Y = diff_Y / 3;
                mid_point_1.x = line_start.x + interval_X * 1;
                mid_point_1.y = line_start.y + interval_Y * 1;
                mid_point_2.x = line_start.x + interval_X * 2;
                mid_point_2.y = line_start.y + interval_Y * 2;
            }

            private void oscillateMidPoints() {
                // oscillate the first mid point
                mid_point_1.x -= mid_point_1_oscillation.x;
                mid_point_1.y -= mid_point_1_oscillation.y;
                mid_point_1_oscillation.x = random.nextInt(WIDTH_HALF + WIDTH_HALF / 2);
                mid_point_1_oscillation.y = calcOrthogonalPoint(mid_point_1.y, mid_point_1.x, mid_point_1_oscillation.x);
                mid_point_1.x += mid_point_1_oscillation.x;
                mid_point_1.y += mid_point_1_oscillation.y;

                // oscillate the second mid point
                mid_point_2.x -= mid_point_2_oscillation.x;
                mid_point_2.y -= mid_point_2_oscillation.y;
                mid_point_2_oscillation.x = -random.nextInt(WIDTH_HALF + HEIGHT_HALF / 2);
                mid_point_2_oscillation.y = calcOrthogonalPoint(mid_point_2.y, mid_point_2.x, mid_point_2_oscillation.x);
                mid_point_2.x += mid_point_2_oscillation.x;
                mid_point_2.y += mid_point_2_oscillation.y;
            }

            private float calcOrthogonalPoint(float p1, float p2, float op1) {
                return -(p1 * op1) / p2;
            }
        }
    }
}
