package models.war_attenders.tanks;

import logic.WayPointManager;
import models.war_attenders.MovableWarAttender;
import models.war_attenders.soldiers.Soldier;
import models.weapons.Weapon;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import java.util.Random;

public abstract class Tank extends MovableWarAttender {
    public Image turret;
    protected float backwards_speed;
    private boolean decelerate, centerTurret;
    private int TURRET_WIDTH_HALF, TURRET_HEIGHT_HALF;

    // each tank has an acceleration and a deceleration
    protected float acceleration_factor;   // number between [0 and 1] -> the smaller the faster the acceleration
    protected float deceleration_factor;   // number between [0 and 1] -> the smaller the faster the deceleration

    private DestructionAnimation destructionAnimation;

    public Tank(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);
        scoreValue = 1000;
    }

    public void init() {
        WIDTH_HALF = base_image.getWidth() / 2;
        HEIGHT_HALF = base_image.getHeight() / 2;
        TURRET_WIDTH_HALF = turret.getWidth() / 2;
        TURRET_HEIGHT_HALF = turret.getHeight() / 2;
        destructionAnimation = new DestructionAnimation();
        super.init();
    }

    @Override
    public void update(GameContainer gc, int deltaTime) {
        super.update(gc, deltaTime);

        if (decelerate) {
            decelerate(deltaTime);
        }

        if (isDestroyed) {
            if (waypointManager != null) current_speed = 0.f; // stop the tank if it was moving
            destructionAnimation.update(deltaTime);
            if (destructionAnimation.hasFinished()) {
                level_delete_listener.notifyForWarAttenderDeletion(this);
            }
        }

        if (centerTurret) {
            centerTurret(deltaTime);
        }

        //WAYPOINTS
        if (waypointManager != null) {
            // move to next vector
            accelerate(deltaTime);
            // rotate the tank towards the next vector until it's pointing towards it
            if (waypointManager.wish_angle != (int) getRotation()) {
                rotate(waypointManager.rotate_direction, deltaTime);
                waypointManager.adjustAfterRotation(this.position, getRotation());
            }

            if (waypointManager.distToNextVector(this.position) < HEIGHT_HALF * 2) {
                waypointManager.setupNextWayPoint(this.position, getRotation());
            }
        }
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
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
    }

    public void accelerate(int deltaTime) {
        if (current_speed < max_speed) {
            current_speed += acceleration_factor * deltaTime;
        } else {
            current_speed = max_speed;  // cap the max speed
        }
        calculateMovementVector(deltaTime, Direction.FORWARD);
        position.add(dir);
    }

    public void decelerate(int deltaTime) {
        if (current_speed > 0.f) {  // 0.01f and not 0.f because it will take longer to reach 0.f completely!
            current_speed -= deceleration_factor * deltaTime;
        } else {
            current_speed = 0.f;
            decelerate = false;
            isMoving = false;
        }
        calculateMovementVector(deltaTime, Direction.FORWARD);
        position.add(dir);
    }

    public void startDeceleration() {
        decelerate = true;
    }

    public void cancelDeceleration() {
        decelerate = false;
    }

    public void moveBackwards(int deltaTime) {
        if (decelerate) { // if tank is still decelerating, but player wants to move backwards, decelerate harder
            current_speed -= acceleration_factor * deltaTime;
        }
        calculateMovementVector(deltaTime, Direction.BACKWARDS);
        position.add(dir);
    }

    public void rotateTurret(RotateDirection r, int deltaTime) {
        switch (r) {
            case ROTATE_DIRECTION_LEFT:
                turret.rotate(-turret_rotate_speed * deltaTime);
                break;
            case ROTATE_DIRECTION_RIGHT:
                turret.rotate(turret_rotate_speed * deltaTime);
                break;
        }
    }

    public void autoCenterTurret() {
        centerTurret = true;
    }

    protected void centerTurret(int deltaTime) {
        float relative_turretRotation = this.getRotation() - turret.getRotation();
        if (relative_turretRotation > 0) {
            if (relative_turretRotation < 180) {
                rotateTurret(RotateDirection.ROTATE_DIRECTION_RIGHT, deltaTime);
                if (relative_turretRotation <= 3) {
                    turret.setRotation(this.getRotation());
                    centerTurret = false;
                }
            } else {
                rotateTurret(RotateDirection.ROTATE_DIRECTION_LEFT, deltaTime);
                if (relative_turretRotation >= 357) {
                    turret.setRotation(this.getRotation());
                    centerTurret = false;
                }
            }
        } else {
            if (relative_turretRotation > -180) {
                rotateTurret(RotateDirection.ROTATE_DIRECTION_LEFT, deltaTime);
                if (relative_turretRotation >= -3) {
                    turret.setRotation(this.getRotation());
                    centerTurret = false;
                }
            } else {
                rotateTurret(RotateDirection.ROTATE_DIRECTION_RIGHT, deltaTime);
                if (relative_turretRotation <= -357) {
                    turret.setRotation(this.getRotation());
                    centerTurret = false;
                }
            }
        }
    }

    @Override
    public float getRotation() {
        return base_image.getRotation();
    }

    public void setCurrentSpeed(Direction direction) {
        if (direction == Direction.FORWARD) {
            this.current_speed = 0.f;
        } else {
            this.current_speed = backwards_speed;
        }
    }

    @Override
    public void rotate(RotateDirection r, int deltaTime) {
        float degree;
        switch (r) {
            case ROTATE_DIRECTION_LEFT:
                degree = -rotate_speed * deltaTime;
                base_image.rotate(degree);
                turret.rotate(degree);
                break;
            case ROTATE_DIRECTION_RIGHT:
                degree = rotate_speed * deltaTime;
                base_image.rotate(degree);
                turret.rotate(degree);
                break;
        }
    }

    @Override
    public void onCollision(MovableWarAttender enemy) {
        if (enemy instanceof Tank) {  // enemy is a tank
            // block movement as long as there's collision
            blockMovement();
        } else if (enemy instanceof Soldier) {   // enemy is a soldier (bad for him)
            // kill soldier
        }
        // plane instanceof is not needed, nothing happens there
    }

    @Override
    public void blockMovement() {
        position.sub(dir);  // set the position on last position before the collision
        collisionModel.update(base_image.getRotation());    // update collision model
        current_speed = 0.f;    // set the speed to zero (stop moving on collision)
    }

    /*
    let the tank bounce a few meters back from its current position
     */
    private void bounceBack() {
        // TODO
    }

    @Override
    public void setRotation(float angle) {
        float rotation = WayPointManager.getShortestAngle(turret.getRotation(), angle);
        if (rotation == 0) return;

        if (rotation < 0) {
            turret.rotate(-turret_rotate_speed);
        } else {
            turret.rotate(turret_rotate_speed);
        }
    }

    @Override
    public void fireWeapon(WeaponType weaponType) {
        Weapon weapon = null;
        switch (weaponType) {
            case WEAPON_1:
                weapon = getWeapon(WeaponType.WEAPON_1);
                break;
            case WEAPON_2:
                weapon = getWeapon(WeaponType.WEAPON_2);
                break;
            case MEGA_PULSE:
                weapon = getWeapon(WeaponType.MEGA_PULSE);
                break;
        }
        if (weapon == null) return;  // does not have a WEAPON_2, so return
        weapon.fire(position.x, position.y, turret.getRotation());
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

        DestructionAnimation() {
            random = new Random();
            my_first_line = new MyLine();
            my_second_line = new MyLine();
        }

        public void draw(Graphics graphics) {
            graphics.setColor(new Color(196, 200, 249));
            graphics.setLineWidth(3.f);
            my_first_line.draw(graphics);
            my_second_line.draw(graphics);
        }

        public void update(int deltaTime) {
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
                collisionModel.update(getRotation());
                defineNewLine();
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
                yDir *= WIDTH_HALF;
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
