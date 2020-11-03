package game.models.entities.windmills;

import game.util.WayPointManager;
import game.graphics.animations.smoke.SmokeAnimation;
import game.models.CollisionModel;
import game.models.entities.Entity;
import game.models.entities.MovableEntity;
import game.models.weapons.Weapon;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import java.util.Random;

public abstract class Windmill extends Entity {

    protected Image turret;
    protected Vector2f turret_position;
    private SmokeAnimation smokeAnimation;
    private final static int SMOKE_ANIMATION_FREQUENCY = 300;  // two per second
    private int smoke_animation_timer;
    private Random random;

    private static final float WINDMILL_DEFAULT_ARMOR = 30.f;
    private static final int WINDMILL_DEFAULT_SCORE_VALUE = 200;
    private static final float TURRET_ROTATE_SPEED = 0.07f;


    public Windmill(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);
        random = new Random();
        smokeAnimation = new SmokeAnimation(3);
    }

    @Override
    public void init() {
        collisionModel = new CollisionModel(position, base_image.getWidth(), base_image.getHeight());
        super.init();
    }

    @Override
    public void update(GameContainer gc, int deltaTime) {
        super.update(gc, deltaTime);
        smokeAnimation.update(deltaTime);
        // draw smoke animation when below half health
        if (current_health < MAX_HEALTH / 2) {
            smoke_animation_timer += deltaTime;
            if (smoke_animation_timer > SMOKE_ANIMATION_FREQUENCY) {
                smoke_animation_timer = 0;
                int rotation = random.nextInt(360);
                float xVal = (float) (position.x + -Math.sin(((rotation) * Math.PI) / 180) * -30.f);
                float yVal = (float) (position.y + Math.cos(((rotation) * Math.PI) / 180) * -30.f);
                smokeAnimation.play(xVal, yVal, rotation);
            }
        }

        if (isDestroyed) {
            level_delete_listener.notifyForEntityDestruction(this);
        }
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        base_image.drawCentered(position.x, position.y);
        turret.draw(turret_position.x, turret_position.y);
        smokeAnimation.draw();
        drawHealthBar(graphics);
    }

    @Override
    public void drawPreview(Graphics graphics) {
        base_image.draw(position.x, position.y, 0.6f);
    }

    @Override
    public void changeAimingDirection(float angle, int deltaTime) {
        float rotation = WayPointManager.getShortestSignedAngle(turret.getRotation(), angle);
        if (rotation < 0) {
            turret.rotate(-TURRET_ROTATE_SPEED * deltaTime);
        } else {
            turret.rotate(TURRET_ROTATE_SPEED * deltaTime);
        }
    }

    @Override
    public float getAimingDirection() {
        return turret.getRotation();
    }

    @Override
    public void changeHealth(float amount) {
        super.changeHealth(amount, WINDMILL_DEFAULT_ARMOR);
    }

    @Override
    public void setRotation(float degree) {
        // can't rotate a windmill
    }

    @Override
    public float getRotation() {
        return 0;
    }

    @Override
    public int getScoreValue() {
        return WINDMILL_DEFAULT_SCORE_VALUE;
    }
}
