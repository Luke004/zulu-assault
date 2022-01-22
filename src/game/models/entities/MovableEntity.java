package game.models.entities;

import game.models.entities.aircraft.AttackHelicopter;
import game.util.WayPointManager;
import game.models.entities.soldiers.Soldier;
import settings.UserSettings;
import game.models.interaction_circles.TeleportCircle;
import game.models.entities.aircraft.Aircraft;
import game.models.weapons.MegaPulse;
import game.models.weapons.Weapon;
import game.models.weapons.projectiles.Projectile;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import game.player.Player;

import java.util.Iterator;

import static game.levels.Level.all_entities;
import static game.levels.Level.all_hostile_entities;


public abstract class MovableEntity extends Entity {

    // drivable related
    public Animation drivable_animation;
    private Image drivable_animation_image;
    private boolean show_drivable_animation;

    // math related
    public Vector2f dir;
    public float current_speed;
    protected int WIDTH_HALF, HEIGHT_HALF;

    // way-points related
    public WayPointManager waypointManager;

    // specs related
    public static final float DAMAGE_TO_DESTRUCTIBLE_TILE = 5.f;
    private static final float TURRET_ROTATE_SPEED_PLAYER = 0.2f, TURRET_ROTATE_SPEED_BOT = 0.07f;

    // booleans
    protected boolean isMoving, isMovingForward, canTeleport;
    public boolean isDrivable;

    private static Sound soldier_death_sound;

    static {
        try {
            soldier_death_sound = new Sound("audio/sounds/bone_crack.ogg");
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
        super.init();
        isMovingForward = true;
        if (isDrivable && !isHostile) {
            initAccessibleAnimation();
            weapons.add(new MegaPulse(isDrivable));  // add the MEGA_PULSE (special item)
        }
    }

    @Override
    public void update(GameContainer gc, int deltaTime) {
        super.update(gc, deltaTime);

        // GRAPHICS RELATED STUFF
        if (show_drivable_animation) {
            drivable_animation.update(deltaTime);
        }

        // COLLISION RELATED STUFF
        collisionModel.update(base_image.getRotation());

        if (isDestroyed) {
            blockMovement();
        }

        setHealthBarPosition(this.position);
    }

    @Override
    public void editorUpdate(GameContainer gc, int deltaTime) {
        super.editorUpdate(gc, deltaTime);
        if (show_drivable_animation) {
            drivable_animation.update(deltaTime);
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

    /**
     * Extra method for player to animate him when invincible.
     */
    public abstract void drawInvincible(Graphics graphics, boolean invincibility_animation_switch);

    protected void drawDrivableAnimation() {
        if (show_drivable_animation) {
            drivable_animation.draw(health_bar_position.x + 21.f, health_bar_position.y - 3.f);
        }
    }

    public void addWayPoints(WayPointManager waypointManager) {
        this.waypointManager = waypointManager;
        setMoving(true);
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

    public boolean isHostile() {
        return isHostile;
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

    /* Default implementation of 'onCollision': Like if it was a war machine */
    public void onCollision(Entity entity) {
        if (entity instanceof Soldier) {   // enemy is a soldier (bad for him)
            if (entity.isDestroyed) return;
            if (!isHostile && entity.isHostile) {
                entity.changeHealth(-150.f);
                if (entity.getHealth() <= 0) {
                    soldier_death_sound.play(1.f, UserSettings.soundVolume);
                }
            }
            blockMovement();
        } else {
            if (entity instanceof Aircraft) {
                if (((Aircraft) entity).isMoving) return;
                if (entity instanceof AttackHelicopter) return;
            }
            blockMovement();
            if (!isHostile && entity.isHostile) {
                entity.changeHealth(-10.f);
            }
        }
    }

    public void blockMovement() {
        if (isDestroyed) return;
        position.sub(dir);  // set the position on last position before the collision
        collisionModel.update(base_image.getRotation());    // update collision model
    }

    public abstract void rotate(RotateDirection r, int deltaTime);

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
