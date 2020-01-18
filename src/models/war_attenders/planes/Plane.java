package models.war_attenders.planes;

import logic.TileMapInfo;
import logic.WayPointManager;
import main.SoundManager;
import menus.UserSettings;
import models.war_attenders.MovableWarAttender;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import static logic.TileMapInfo.*;

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


        // TODO: REMOVE THIS! (FOR TESTING PURPOSES)
        // THIS EXISTS SO THAT WE CAN FLY FROM START ON IN A GREEN PLANE AND DONT HAVE TO ENTER IT FIRST
        hasStarted = true;


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
            landPlane(deltaTime);
        } else if (starting) {
            setMoving(true);
            startPlane(deltaTime);
        } else {
            if (isDestroyed) {
                // crash the plane and remove it after it has reached the ground
                landPlane(deltaTime);   // land plane -> to simulate a plane crash -> it looks like landing
                if (hasLanded) level_delete_listener.notifyForWarAttenderDeletion(this);
            } else {
                // normal shadow position
                planeShadow.update();
            }

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

    private void startPlane(int deltaTime) {
        // move the plane's shadow away from the plane towards the origin position of the shadow
        movePlaneShadow(deltaTime, planeShadow.origin_pos);

        if (WayPointManager.dist(planeShadow.current_shadow_pos, planeShadow.origin_pos)
                <= PlaneShadow.STARTING_LANDING_SPEED * 4) {
            hasStarted = true;
            starting = false;
        }
    }

    private void landPlane(int deltaTime) {
        Vector2f plane_pos = new Vector2f(position.x - WIDTH_HALF, position.y - HEIGHT_HALF);
        movePlaneShadow(deltaTime, plane_pos);  // move the plane's shadow towards the plane
        if (WayPointManager.dist(planeShadow.current_shadow_pos, plane_pos)
                <= 2.f) {
            hasLanded = true;
            setMoving(false);
        }
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

        // TODO: add destruction animation to plane crash
        /*
        if (isDestroyed) {
            //destructionAnimation.draw(graphics);
        }
         */
    }

    @Override
    public void showAccessibleAnimation(boolean activate) {
        super.showAccessibleAnimation(activate);
        // stop the plane when player leaves it, start it again when player enters it back
        if (!activate && hasLanded) {
            initStart();    // start the plane
        }
    }

    public void initLanding() {
        if (!canLand()) {
            SoundManager.ERROR_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
            return;
        }
        landing = true;
        starting = false;
        hasStarted = false;
    }

    private boolean canLand() {
        // check the next six tiles before the plane, if they are collision tiles, the plane can't land

        // the direction the plane is heading towards
        float m_dir_x = (float) Math.sin(getRotation() * Math.PI / 180);
        float m_dir_y = (float) -Math.cos(getRotation() * Math.PI / 180);

        final int NEXT_TILE_OFFSET = 40;
        int tile_idx = 1;
        do {
            Vector2f tile_before_plane = new Vector2f(
                    position.x + m_dir_x * (NEXT_TILE_OFFSET * tile_idx),
                    position.y + m_dir_y * (NEXT_TILE_OFFSET * tile_idx));
            int mapX = (int) (tile_before_plane.x / TILE_WIDTH);
            if (mapX < 0 || mapX >= LEVEL_WIDTH_TILES) return false;  // player wants to land out of map
            int mapY = (int) (tile_before_plane.y / TILE_HEIGHT);
            if (mapY < 0 || mapY >= LEVEL_HEIGHT_TILES) return false;  // player wants to land out of map
            int tileID = map.getTileId(mapX, mapY, LANDSCAPE_TILES_LAYER_IDX);
            if (TileMapInfo.isCollisionTile(tileID)) return false;
        } while (tile_idx++ < 6);    // do it six times (6 tiles before the plane)

        return true;
    }

    private void initStart() {
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
