package models.war_attenders;

import levels.AbstractLevel;
import logic.level_listeners.WarAttenderDeleteListener;
import logic.WayPointManager;
import menus.UserSettings;
import models.CollisionModel;
import models.war_attenders.robots.Robot;
import models.war_attenders.tanks.Tank;
import models.weapons.MegaPulse;
import models.weapons.Weapon;
import models.weapons.projectiles.iGroundTileDamageWeapon;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import player.Player;

public abstract class MovableWarAttender extends WarAttender {
    // listener
    protected WarAttenderDeleteListener level_delete_listener;
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
    public float max_speed, current_speed;
    public float rotate_speed;

    // booleans
    public boolean isMoving, isDrivable;

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

    public MovableWarAttender(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);
        dir = new Vector2f(0, 0);
    }

    public MovableWarAttender(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        this(startPos, isHostile);
        this.isDrivable = isDrivable;

        if (isDrivable) {
            turret_rotate_speed = 0.15f;
        } else {
            turret_rotate_speed = 0.5f;
        }
    }

    @Override
    public void init() {
        if (isDrivable) {
            initAccessibleAnimation();
            weapons.add(new MegaPulse());  // add the MEGA_PULSE (special item)
        }
        health_bar_offset = new Vector2f(30.f, base_image.getHeight() / 2.f + 20.f);
        super.init();
    }

    @Override
    public void update(GameContainer gc, int deltaTime) {
        super.update(gc, deltaTime);

        // GRAPHICS RELATED STUFF
        if (show_drivable_animation) {
            drivable_animation.update(deltaTime);
        }

        health_bar_position.x = position.x - health_bar_offset.x;
        health_bar_position.y = position.y - health_bar_offset.y;

        // COLLISION RELATED STUFF
        collisionModel.update(base_image.getRotation());

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
                if (invincibility_animation_switch) invincibility_animation_switch = false;
                else invincibility_animation_switch = true;
                invincible_animation_current_time = 0;
            }
        }

        if (isDestroyed) {
            blockMovement();
        }
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);

        // DRAW DRIVABLE ANIMATION
        showDrivableAnimation();

        // COLLISION RELATED STUFF
        collisionModel.draw(graphics);
    }

    public void showDrivableAnimation() {
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

    public boolean isMoving() {
        return isMoving;
    }

    public abstract void onCollision(MovableWarAttender enemy);

    public abstract void blockMovement();

    public abstract float getRotation();

    public abstract void rotate(RotateDirection r, int deltaTime);

    public void activateItem(Player.Item_e item) {
        switch (item) {
            case INVINCIBILITY:
                isInvincible = true;
                break;
            case EMP:
                emp_use_sound.play(1.f, UserSettings.SOUND_VOLUME);
                // TODO: destroy all planes here
                break;
            case MEGA_PULSE:
                mega_pulse_use_sound.play(1.f, UserSettings.SOUND_VOLUME);
                fireWeapon(WeaponType.MEGA_PULSE);
                break;
            case EXPAND:
                expand_use_sound.play(1.f, UserSettings.SOUND_VOLUME);
                // TODO: reflect enemy bullets here
                break;
        }
    }

    public enum RotateDirection {
        ROTATE_DIRECTION_LEFT, ROTATE_DIRECTION_RIGHT
    }

    public enum Direction {
        FORWARD, BACKWARDS
    }
}
