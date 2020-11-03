package game.models.entities.soldiers;

import game.logic.CollisionHandler;
import game.util.WayPointManager;
import game.models.CollisionModel;
import game.models.entities.MovableEntity;
import game.models.entities.Entity;
import game.models.entities.aircraft.Aircraft;
import game.models.entities.robots.Robot;
import game.models.entities.tanks.Tank;
import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import java.util.List;

public abstract class Soldier extends MovableEntity {
    Animation animation;

    private boolean isFleeing;
    private static final int FLEE_TIMER = 1500;
    private int time_fleeing;

    // default soldier attributes
    private static final float ARMOR = 2.5f;
    private static final int SCORE_VALUE = 100;
    private static final float ROTATE_SPEED_PLAYER = 0.25f, ROTATE_SPEED_BOT = 0.25f;
    private static final float MAX_SPEED_PLAYER = 0.1f, MAX_SPEED_BOT = 0.1f;

    public Soldier(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);
    }

    public void init() {
        animation = new Animation(false);
        int x = 0;
        do {
            animation.addFrame(base_image.getSubImage(x, 0, 12, 12), 300);
            x += 12;
        } while (x <= 24);
        animation.setCurrentFrame(1);
        animation.setLooping(true);
        animation.setPingPong(true);
        animation.stop();

        WIDTH_HALF = animation.getImage(0).getWidth() / 2;
        HEIGHT_HALF = animation.getImage(0).getHeight() / 2;

        this.current_speed = getMaxSpeed();

        // just use index 0, all indices are same width and height
        collisionModel = new CollisionModel(position, animation.getImage(0).getWidth(), animation.getImage(0).getHeight());
        super.init();
    }

    @Override
    public void update(GameContainer gameContainer, int deltaTime) {
        super.update(gameContainer, deltaTime);
        animation.update(deltaTime);

        if (isDestroyed) {
            level_delete_listener.notifyForEntityDestruction(this);
        } else if (isFleeing) {
            time_fleeing += deltaTime;
            if (time_fleeing > FLEE_TIMER) {
                isFleeing = false;
                setMoving(false);
                time_fleeing = 0;
            } else {
                setMoving(true);
                moveForward(deltaTime);
            }
        }
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);

        if (isInvincible) {
            if (!invincibility_animation_switch) {
                animation.getCurrentFrame().drawFlash(position.x - WIDTH_HALF, position.y - HEIGHT_HALF);
            } else {
                animation.draw(position.x - WIDTH_HALF, position.y - HEIGHT_HALF);
            }
        } else {
            animation.draw(position.x - WIDTH_HALF, position.y - HEIGHT_HALF);
        }

        //collisionModel.draw(graphics);
    }

    @Override
    public void drawPreview(Graphics graphics) {
        animation.getImage(0).draw(position.x, position.y, 1.2f);
    }

    public void startAnimation() {
        animation.start();
    }

    public void stopAnimation() {
        animation.stop();
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
    public void onCollision(Entity entity) {
        //if (movableEntity.isDrivable()) blockMovement();
        if (!(entity instanceof Aircraft)) {
            blockMovement();
        } else {
            if (!(((Aircraft) entity).isMoving())) {
                blockMovement();
            }
        }
    }

    @Override
    public void changeHealth(float amount) {
        super.changeHealth(amount, ARMOR);
    }

    @Override
    public void rotate(RotateDirection rotateDirection, int deltaTime) {
        switch (rotateDirection) {
            case ROTATE_DIRECTION_LEFT:
                for (int idx = 0; idx < animation.getFrameCount(); ++idx) {
                    animation.getImage(idx).rotate(-getBaseRotateSpeed() * deltaTime);
                }
                break;
            case ROTATE_DIRECTION_RIGHT:
                for (int idx = 0; idx < animation.getFrameCount(); ++idx) {
                    animation.getImage(idx).rotate(getBaseRotateSpeed() * deltaTime);
                }
                break;
        }
    }

    @Override
    public void changeAimingDirection(float angle, int deltaTime) {
        for (int idx = 0; idx < animation.getFrameCount(); ++idx) {
            animation.getImage(idx).setRotation(angle);
        }
    }

    @Override
    public void setRotation(float angle) {
        changeAimingDirection(angle, 0);
    }

    @Override
    public float getRotation() {
        return animation.getCurrentFrame().getRotation();
    }

    @Override
    public float getAimingDirection() {
        return animation.getCurrentFrame().getRotation();
    }

    @Override
    public void shootAtEnemies(MovableEntity player, List<? extends Entity> enemies_of_entity, int deltaTime) {
        if (isFleeing) return;
        MovableEntity closest_entity = player;
        // calculate dist between the game.player and the enemy
        float xPos = player.getPosition().x;
        float yPos = player.getPosition().y;
        float dist = (float) Math.sqrt((xPos - position.x) * (xPos - position.x)
                + (yPos - position.y) * (yPos - position.y));

        // calculate dist between each friend and the enemy
        for (Entity enemyEntity : enemies_of_entity) {
            float next_xPos = enemyEntity.getPosition().x;
            float next_yPos = enemyEntity.getPosition().y;
            float next_dist = (float) Math.sqrt((next_xPos - position.x) * (next_xPos - position.x)
                    + (next_yPos - position.y) * (next_yPos - position.y));
            if (next_dist < dist) {
                dist = next_dist;
                xPos = next_xPos;
                yPos = next_yPos;
                closest_entity = (MovableEntity) enemyEntity;
            }
        }

        // flee when the closest entity gets too close and is a tank or a robot
        if (dist < 100 && (closest_entity instanceof Tank || closest_entity instanceof Robot)) {
            changeAimingDirection(closest_entity.getRotation(), deltaTime);
            isFleeing = true;
        } else if (dist < 500) {
            // aim at the closest enemy and fire
            float rotationDegree = WayPointManager.calculateAngleToRotateTo(position, new Vector2f(xPos, yPos));

            changeAimingDirection(rotationDegree, deltaTime);

            fireWeapon(Entity.WeaponType.WEAPON_1);
        }
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

