package models.entities;

import levels.AbstractLevel;
import logic.level_listeners.EntityDeleteListener;
import logic.WayPointManager;
import settings.UserSettings;
import models.CollisionModel;
import models.interaction_circles.TeleportCircle;
import models.entities.aircraft.Aircraft;
import models.weapons.MegaPulse;
import models.weapons.Weapon;
import models.weapons.projectiles.Projectile;
import models.weapons.projectiles.iGroundTileDamageWeapon;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import player.Player;

import java.util.Iterator;

import static levels.AbstractLevel.all_movable_entities;
import static levels.AbstractLevel.hostile_movable_entities;

public abstract class MovableEntity extends Entity {
    // listener
    protected EntityDeleteListener level_delete_listener;
    // model related
    public Image base_image;
    public CollisionModel collisionModel;
    public int WIDTH_HALF, HEIGHT_HALF;
    private Vector2f health_bar_offset;

    // drivable related
    public Animation drivable_animation;
    public Image drivable_animation_image;
    public boolean show_drivable_animation;

    // math related
    public Vector2f dir;

    // way-points related
    public WayPointManager waypointManager;

    // specs related
    public static final float DAMAGE_TO_DESTRUCTIBLE_TILE = 5.f;
    public float current_speed;
    private static final float TURRET_ROTATE_SPEED_PLAYER = 0.2f, TURRET_ROTATE_SPEED_BOT = 0.07f;

    // booleans
    protected boolean isMoving, isMovingForward, isDrivable, canTeleport;


    // invincibility item related
    public boolean isInvincible, invincibility_animation_switch;
    private int invincibility_lifetime;
    private final static int INVINCIBILITY_TIME = 10000;   // 10 sec
    public static final int INVINCIBLE_ANIMATION_TIME_SWITCH = 1000;    // once every sec switch to white color while invincible
    public int invincible_animation_current_time;

    // item sounds (ONLY PLAYER CAN USE THEM -> static)
    private static Sound expand_use_sound, emp_use_sound, mega_pulse_use_sound;

    static {
        try {
            expand_use_sound = new Sound("audio/sounds/expand_use.ogg");
            mega_pulse_use_sound = new Sound("audio/sounds/mega_pulse_use.ogg");
            emp_use_sound = new Sound("audio/sounds/emp_use.ogg");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public MovableEntity(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);
        dir = new Vector2f(0, 0);
    }

    public MovableEntity(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        this(startPos, isHostile);
        this.isDrivable = isDrivable;
    }

    @Override
    public void init() {
        isMovingForward = true;
        if (isDrivable && !isHostile) {
            initAccessibleAnimation();
            weapons.add(new MegaPulse(isDrivable));  // add the MEGA_PULSE (special item)
        }
        health_bar_offset = new Vector2f(30.f, base_image.getHeight() / 2.f + 20.f);
        super.init();
    }

    @Override
    public void update(GameContainer gc, int deltaTime) {
        super.update(gc, deltaTime);

        //System.out.println("canTeleport: " +canTeleport);

        // GRAPHICS RELATED STUFF
        if (show_drivable_animation) {
            drivable_animation.update(deltaTime);
        }

        health_bar_position.x = position.x - health_bar_offset.x;
        health_bar_position.y = position.y - health_bar_offset.y;

        // COLLISION RELATED STUFF
        collisionModel.update(base_image.getRotation());

        if (!isHostile) {
            // ITEMS RELATED STUFF

            // INVINCIBILITY
            if (isInvincible) {
                // invincibility logic itself
                invincibility_lifetime += deltaTime;
                if (invincibility_lifetime > INVINCIBILITY_TIME) {
                    isInvincible = false;
                    invincibility_lifetime = 0;
                }

                // invincibility animation related
                invincible_animation_current_time += deltaTime;
                if (invincible_animation_current_time >= INVINCIBLE_ANIMATION_TIME_SWITCH) {
                    invincibility_animation_switch = !invincibility_animation_switch;
                    invincible_animation_current_time = 0;
                }
            }
        }

        if (isDestroyed) {
            blockMovement();
        }
    }

    @Override
    public void draw(Graphics graphics) {
        // DRAW DRIVABLE ANIMATION
        drawHealthBar(graphics);
        drawDrivableAnimation();
        super.draw(graphics);
        //collisionModel.draw(graphics);
    }

    protected void drawDrivableAnimation() {
        if (show_drivable_animation) {
            drivable_animation.draw(health_bar_position.x + 21.f, health_bar_position.y - 3.f);
        }
    }

    public void addListeners(AbstractLevel abstractLevel) {
        this.level_delete_listener = abstractLevel;
        for (Weapon weapon : weapons) {
            if (weapon instanceof iGroundTileDamageWeapon) {
                ((iGroundTileDamageWeapon) weapon).addListener(abstractLevel);
            }
        }
    }

    public void addWayPoints(WayPointManager waypointManager) {
        this.waypointManager = waypointManager;
    }

    private void initAccessibleAnimation() {
        show_drivable_animation = true;
        try {
            drivable_animation_image = new Image("assets/healthbars/accessible_arrow_animation.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        drivable_animation = new Animation(false);

        int x = 0;
        do {
            drivable_animation.addFrame(drivable_animation_image.getSubImage(x, 0, 17, 28), 1000);
            x += 17;
        } while (x <= 34);
        drivable_animation.setCurrentFrame(1);
    }

    public void showAccessibleAnimation(boolean activate) {
        show_drivable_animation = activate;
    }

    public void calculateMovementVector(int deltaTime, Direction direction) {
        dir.x = (float) Math.sin(getRotation() * Math.PI / 180);
        dir.y = (float) -Math.cos(getRotation() * Math.PI / 180);
        if (direction == Direction.BACKWARDS) {
            dir.x *= -1;
            dir.y *= -1;
        }
        dir.x *= deltaTime * current_speed;
        dir.y *= deltaTime * current_speed;
    }

    public Vector2f calculateSoldierSpawnPosition() {
        // set player 10 pixels behind the tank
        final float DISTANCE = 10;
        final float SPAWN_X = 0;
        final float SPAWN_Y = base_image.getHeight() / 2.f + DISTANCE;

        float xVal = (float) (Math.cos(((base_image.getRotation()) * Math.PI) / 180) * SPAWN_X
                + -Math.sin(((base_image.getRotation()) * Math.PI) / 180) * SPAWN_Y);
        float yVal = (float) (Math.sin(((base_image.getRotation()) * Math.PI) / 180) * SPAWN_X
                + Math.cos(((base_image.getRotation()) * Math.PI) / 180) * SPAWN_Y);
        return new Vector2f(xVal + position.x, yVal + position.y);
    }

    public CollisionModel getCollisionModel() {
        return collisionModel;
    }

    public boolean isHostile() {
        return isHostile;
    }

    public boolean isInvincible() {
        return isInvincible;
    }

    public void setMoving(boolean b) {
        isMoving = b;
    }

    public void setMovingForward(boolean b) {
        isMovingForward = b;
    }

    public boolean isMovingForward() {
        return isMovingForward;
    }

    public boolean isMoving() {
        return isMoving;
    }

    protected float getTurretRotateSpeed() {
        return isDrivable ? TURRET_ROTATE_SPEED_PLAYER : TURRET_ROTATE_SPEED_BOT;
    }

    protected abstract float getBaseRotateSpeed();

    protected abstract float getMaxSpeed();

    public boolean isDrivable() {
        return isDrivable;
    }

    public abstract void onCollision(MovableEntity enemy);

    public void blockMovement() {
        if (isDestroyed) return;
        position.sub(dir);  // set the position on last position before the collision
        collisionModel.update(base_image.getRotation());    // update collision model
        //current_speed = 0.f;    // set the speed to zero (stop moving on collision)
        //rotate_speed = 0.f;
    }

    public abstract float getRotation();

    public abstract void rotate(RotateDirection r, int deltaTime);

    public abstract void setRotation(float degree);

    public void activateItem(Player.Item_e item) {
        if (isDestroyed) return;
        switch (item) {
            case INVINCIBILITY:
                isInvincible = true;
                break;
            case EMP:   // destroy all nearby planes
                emp_use_sound.play(1.f, UserSettings.soundVolume);
                for (int idx = 0; idx < hostile_movable_entities.size(); ++idx) {
                    MovableEntity hostileEntity = hostile_movable_entities.get(idx);
                    if (hostileEntity instanceof Aircraft) {
                        if (WayPointManager.dist(hostileEntity.getPosition(), this.getPosition()) < 300) {
                            hostileEntity.isDestroyed = true;
                        }
                    }
                }
                break;
            case MEGA_PULSE:
                mega_pulse_use_sound.play(1.f, UserSettings.soundVolume);
                fireWeapon(WeaponType.MEGA_PULSE);
                break;
            case EXPAND:
                expand_use_sound.play(1.f, UserSettings.soundVolume);
                for (int idx = 0; idx < all_movable_entities.size(); ++idx) {
                    MovableEntity movableEntity = all_movable_entities.get(idx);
                    if (WayPointManager.dist(movableEntity.getPosition(), this.getPosition()) < 500) {
                        for (Weapon weapon : movableEntity.getWeapons()) {
                            Iterator<Projectile> projectile_iterator = weapon.getProjectiles();
                            while (projectile_iterator.hasNext()) {
                                Projectile projectile = projectile_iterator.next();
                                projectile.projectile_dir.x *= -1;
                                projectile.projectile_dir.y *= -1;
                            }
                        }
                    }
                }
                break;
        }
    }

    public void teleport(Vector2f new_position) {
        if (canTeleport) {
            canTeleport = false;
            this.position.x = new_position.x;
            this.position.y = new_position.y;
            TeleportCircle.playTeleportSound();
        }
    }

    public void allowTeleport() {
        canTeleport = true;
    }

    public enum RotateDirection {
        ROTATE_DIRECTION_LEFT, ROTATE_DIRECTION_RIGHT
    }

    public enum Direction {
        FORWARD, BACKWARDS
    }
}
