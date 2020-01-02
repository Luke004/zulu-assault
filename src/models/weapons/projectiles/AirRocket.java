package models.weapons.projectiles;

import models.CollisionModel;
import models.weapons.DoubleRocketLauncher;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

import java.util.HashMap;
import java.util.Map;

public class AirRocket extends Rocket implements iAirProjectile {

    private boolean hasHitGround;
    private Map<Target, Boolean> checkedTargets;

    public AirRocket(Vector2f startPos, Vector2f dir, float rotation, Texture bullet_texture, Animation rocket_animation) {
        super(startPos, dir, rotation, bullet_texture, rocket_animation);
        this.isGroundProjectile = false;
        this.max_lifetime = 650;   // maximum lifetime is 0.65 sec
        // special air rocket collision model (collision model is an impact rectangle 30x30px)
        this.collision_model = new CollisionModel(pos, 30, 30);
        checkedTargets = new HashMap<>();
    }

    @Override
    public void update(int deltaTime) {
        if (lifetime <= max_lifetime - 200) {
            super.update(deltaTime);
        } else {
            this.lifetime += deltaTime;
            if (!hasHitGround()) {
                setHitGround();
                DoubleRocketLauncher.playDestructionAnimation(pos.x, pos.y);
                // only update the collision model in the last 200 milliseconds
                collision_model.update(deltaTime);
            }
        }
        rocket_animation.update(deltaTime);
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        collision_model.draw(graphics);
    }

    @Override
    public boolean hasHitGround() {
        return hasHitGround;
    }

    @Override
    public void setHitGround() {
        hasHitGround = true;
    }

    @Override
    public boolean hasChecked(Target target) {
        if (checkedTargets.get(target) == null) return false;
        return checkedTargets.get(target);
    }

    @Override
    public void setChecked(Target target) {
        checkedTargets.put(target, true);
    }
}
