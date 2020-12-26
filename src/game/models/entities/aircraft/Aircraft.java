package game.models.entities.aircraft;

import game.logic.TileMapData;
import game.util.WayPointManager;
import game.audio.MenuSounds;
import game.models.entities.Entity;
import settings.UserSettings;
import game.models.CollisionModel;
import game.models.entities.MovableEntity;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import static game.logic.TileMapData.*;

public abstract class Aircraft extends MovableEntity {

    public boolean hasLanded;
    protected boolean landing, starting, hasStarted;
    protected PlaneShadow shadow;

    private static final int AIRCRAFT_DEFAULT_SCORE_VALUE = 500;

    public Aircraft(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);
    }

    @Override
    public void init() {
        WIDTH_HALF = base_image.getWidth() / 2;
        HEIGHT_HALF = base_image.getHeight() / 2;

        shadow = new PlaneShadow(new Vector2f(position));

        if (!isDrivable) {
            setMoving(true);
            hasStarted = true;    // bot planes are already flying from the start
        } else {
            landing = true;     // for the player - land the plane so he can get in eventually
        }
        collisionModel = new CollisionModel(position, base_image.getWidth(), base_image.getHeight());
        super.init();
    }

    @Override
    public void rotate(RotateDirection r, int deltaTime) {
        if (landing) return;
        float degree;
        switch (r) {
            case ROTATE_DIRECTION_LEFT:
                degree = -getBaseRotateSpeed() * deltaTime;
                base_image.rotate(degree);
                break;
            case ROTATE_DIRECTION_RIGHT:
                degree = getBaseRotateSpeed() * deltaTime;
                base_image.rotate(degree);
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
    public void onCollision(Entity enemy) {
        // a plane doesn't have collision
    }

    @Override
    public void setRotation(float degree) {
        base_image.setRotation(degree);
    }

    @Override
    public float getRotation() {
        return base_image.getRotation();
    }

    @Override
    public void update(GameContainer gc, int deltaTime) {
        super.update(gc, deltaTime);

        if (isMoving) {
            fly(deltaTime); // the plane is always flying forward
        }

        if (landing) {
            if (isDestroyed) level_delete_listener.notifyForEntityDestruction(this);
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
                if (hasLanded) level_delete_listener.notifyForEntityDestruction(this);
            } else {
                // normal shadow position
                shadow.update();
            }
        }

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

    public void fly(int deltaTime) {
        calculateMovementVector(deltaTime, Direction.FORWARD);
        position.add(dir);
        //collisionModel.update(base_image.getRotation());
    }

    public abstract void increaseSpeed(int deltaTime);

    public abstract void decreaseSpeed(int deltaTime);

    protected void movePlaneShadow(int deltaTime, Vector2f target_pos) {
        float angle = WayPointManager.calculateAngleToRotateTo(shadow.current_shadow_pos, target_pos);
        float moveX = (float) Math.sin(angle * Math.PI / 180);
        float moveY = (float) -Math.cos(angle * Math.PI / 180);
        moveX *= deltaTime * PlaneShadow.STARTING_LANDING_SPEED;
        moveY *= deltaTime * PlaneShadow.STARTING_LANDING_SPEED;
        Vector2f m_dir = new Vector2f(moveX, moveY);
        shadow.current_shadow_pos.add(m_dir);  // add the dir of the shadow movement
        shadow.current_shadow_pos.add(dir);    // add the dir of the plane as well
        shadow.origin_pos.x = position.x - WIDTH_HALF * 2;
        shadow.origin_pos.y = position.y;
    }

    private void startPlane(int deltaTime) {
        // move the plane's shadow away from the plane towards the origin position of the shadow
        movePlaneShadow(deltaTime, shadow.origin_pos);

        if (WayPointManager.dist(shadow.current_shadow_pos, shadow.origin_pos)
                <= PlaneShadow.STARTING_LANDING_SPEED * 4) {
            hasStarted = true;
            starting = false;
        }
    }

    private void landPlane(int deltaTime) {
        Vector2f plane_pos = new Vector2f(position.x - WIDTH_HALF, position.y - HEIGHT_HALF);
        movePlaneShadow(deltaTime, plane_pos);  // move the plane's shadow towards the plane
        if (WayPointManager.dist(shadow.current_shadow_pos, plane_pos)
                <= 2.f) {
            hasLanded = true;
            setMoving(false);
        }
    }

    @Override
    public void blockMovement() {
        // a plane doesn't get its movement blocked
    }

    @Override
    public void draw(Graphics graphics) {
        // draw the plane's shadow
        if (!hasLanded) {
            base_image.drawFlash(shadow.current_shadow_pos.x, shadow.current_shadow_pos.y,
                    WIDTH_HALF * 2, HEIGHT_HALF * 2, Color.black);
        }
        drawBaseImage();

        super.draw(graphics);

        // TODO: add destruction animation to plane crash
        /*
        if (isDestroyed) {
            //destructionAnimation.draw(game.graphics);
        }
         */
    }

    /*
        this is an extra method so helicopter can draw the base image above its shadow wings
     */
    protected void drawBaseImage() {
        if (isInvincible) {
            if (!invincibility_animation_switch) {
                base_image.drawFlash(position.x - WIDTH_HALF, position.y - HEIGHT_HALF);
            } else {
                base_image.draw(position.x - WIDTH_HALF, position.y - HEIGHT_HALF);
            }
        } else {
            base_image.draw(position.x - WIDTH_HALF, position.y - HEIGHT_HALF);
        }
    }

    @Override
    public void drawPreview(Graphics graphics) {
        base_image.draw(position.x, position.y, 0.4f);
    }

    @Override
    public void showAccessibleAnimation(boolean activate) {
        super.showAccessibleAnimation(activate);
        // stop the plane when the player leaves it, start it again when the player enters it back
        if (!activate && hasLanded) {
            initStart();    // start the plane
        }
    }

    public void initLanding() {
        if (!canLand()) {
            MenuSounds.ERROR_SOUND.play(1.f, UserSettings.soundVolume);
            return;
        }
        landing = true;
        starting = false;
        hasStarted = false;
    }

    protected boolean canLand() {
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
            if (mapX < 0 || mapX >= LEVEL_WIDTH_TILES) return false;  // game.player wants to land out of map
            int mapY = (int) (tile_before_plane.y / TILE_HEIGHT);
            if (mapY < 0 || mapY >= LEVEL_HEIGHT_TILES) return false;  // game.player wants to land out of map
            int tileID = map.getTileId(mapX, mapY, LANDSCAPE_TILES_LAYER_IDX);
            if (TileMapData.isCollisionTile(tileID)) return false;
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

    /* call this right after creating an instance of 'Plane' whenever a game.player is starting the level in this instance */
    public void setStarting() {
        landing = false;
        hasStarted = true;
        setMoving(true);
    }

    @Override
    public int getScoreValue() {
        return AIRCRAFT_DEFAULT_SCORE_VALUE;
    }

    protected class PlaneShadow {
        protected Vector2f current_shadow_pos;
        final static float STARTING_LANDING_SPEED = 0.05f;
        Vector2f origin_pos;  // the original position/ standard drawing position of the plane's shadow

        PlaneShadow(Vector2f current_shadow_pos) {
            this.current_shadow_pos = current_shadow_pos;
            origin_pos = new Vector2f(current_shadow_pos.x - WIDTH_HALF * 2, current_shadow_pos.y);
        }

        void update() {
            shadow.current_shadow_pos.x = position.x - WIDTH_HALF * 2;
            shadow.current_shadow_pos.y = position.y;
        }

    }
}
