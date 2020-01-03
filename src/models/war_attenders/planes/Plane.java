package models.war_attenders.planes;

import logic.WayPointManager;
import models.war_attenders.MovableWarAttender;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

public abstract class Plane extends MovableWarAttender {

    protected boolean landing, starting, hasLanded, hasStarted;
    private PlaneShadow planeShadow;

    public Plane(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);
    }

    public void init() {
        WIDTH_HALF = base_image.getWidth() / 2;
        HEIGHT_HALF = base_image.getHeight() / 2;
        //destructionAnimation = new Tank.DestructionAnimation();

        planeShadow = new PlaneShadow(new Vector2f(position));

        if (isHostile) hasStarted = true;    // hostile planes are already flying from the start
        hasStarted = true;      // TODO: REMOVE THIS! (FOR TESTING PURPOSES)
        super.init();
    }

    @Override
    public void rotate(RotateDirection r, int deltaTime) {
        float degree;
        switch (r) {
            case ROTATE_DIRECTION_LEFT:
                degree = -rotate_speed * deltaTime;
                base_image.rotate(degree);
                break;
            case ROTATE_DIRECTION_RIGHT:
                degree = rotate_speed * deltaTime;
                base_image.rotate(degree);
                break;
        }
    }

    @Override
    public void update(GameContainer gc, int deltaTime) {
        super.update(gc, deltaTime);
        if (landing) {
            if (hasLanded) return;
            // calc landing shadow positions
            Vector2f plane_pos = new Vector2f(position.x - WIDTH_HALF, position.y - HEIGHT_HALF);
            movePlaneShadow(deltaTime, plane_pos);  // move the plane's shadow towards the plane

            System.out.println(WayPointManager.dist(planeShadow.current_shadow_pos, plane_pos));
            if (WayPointManager.dist(planeShadow.current_shadow_pos, plane_pos)
                    <= 2.f) {
                hasLanded = true;
                setMoving(false);
            }
        } else if (starting) {
            setMoving(true);
            // move the plane's shadow away from the plane towards the origin position of the shadow
            movePlaneShadow(deltaTime, planeShadow.origin_pos);

            if (WayPointManager.dist(planeShadow.current_shadow_pos, planeShadow.origin_pos)
                    <= PlaneShadow.STARTING_LANDING_SPEED * 4) {
                hasStarted = true;
                starting = false;
            }
        } else {
            // normal shadow position
            planeShadow.update();
        }
    }

    private void movePlaneShadow(int deltaTime, Vector2f target_pos) {
        float angle = WayPointManager.calculateAngle(planeShadow.current_shadow_pos, target_pos);
        float moveX = (float) Math.sin(angle * Math.PI / 180);
        float moveY = (float) -Math.cos(angle * Math.PI / 180);
        moveX *= deltaTime * PlaneShadow.STARTING_LANDING_SPEED;
        moveY *= deltaTime * PlaneShadow.STARTING_LANDING_SPEED;
        Vector2f m_dir = new Vector2f(moveX, moveY);
        planeShadow.current_shadow_pos.add(m_dir);  // add the dir of the shadow movement
        planeShadow.current_shadow_pos.add(dir);    // add the dir of the plane aswell
        planeShadow.origin_pos.x = position.x - WIDTH_HALF * 2;
        planeShadow.origin_pos.y = position.y;
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        // draw the plane's shadow
        if (!hasLanded) {
            base_image.drawFlash(planeShadow.current_shadow_pos.x, planeShadow.current_shadow_pos.y,
                    WIDTH_HALF * 2, HEIGHT_HALF * 2, Color.black);
        }

        if (isInvincible) {
            if (!invincibility_animation_switch) {
                base_image.drawFlash(position.x - WIDTH_HALF, position.y - HEIGHT_HALF);
            } else {
                base_image.draw(position.x - WIDTH_HALF, position.y - HEIGHT_HALF);
            }
        } else {
            base_image.draw(position.x - WIDTH_HALF, position.y - HEIGHT_HALF);
        }


        if (isDestroyed) {
            //destructionAnimation.draw(graphics);
        }
    }

    @Override
    public void showAccessibleAnimation(boolean activate) {
        super.showAccessibleAnimation(activate);
        // stop the plane when player leaves it, start it again when player enters it back
        if (!activate && hasLanded) {
            start();    // start the plane
        }
    }

    public void land() {
        landing = true;
        starting = false;
        hasStarted = false;
    }

    private void start() {
        landing = false;
        hasLanded = false;
        starting = true;
    }

    public boolean hasLanded() {
        return hasLanded;
    }

    private class PlaneShadow {
        Vector2f current_shadow_pos;
        final static float STARTING_LANDING_SPEED = 0.05f;
        Vector2f origin_pos;  // the original position/ standard drawing position of the plane's shadow

        PlaneShadow(Vector2f current_shadow_pos) {
            this.current_shadow_pos = current_shadow_pos;
            origin_pos = new Vector2f(current_shadow_pos.x - WIDTH_HALF * 2, current_shadow_pos.y);
        }

        void update() {
            planeShadow.current_shadow_pos.x = position.x - WIDTH_HALF * 2;
            planeShadow.current_shadow_pos.y = position.y;
        }

    }
}
