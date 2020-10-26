package game.models.weapons.projectiles;

import game.models.CollisionModel;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

import java.util.HashMap;
import java.util.Map;

public class AirProjectile extends Projectile {

    private boolean hasHitGround;
    private Map<Target, Boolean> checkedTargets;

    public AirProjectile(Vector2f startPos, Vector2f dir, float rotation, Texture projectile_texture) {
        super(startPos, dir, rotation, projectile_texture);
        // special air bullet collision model (collision model is an impact rectangle 30x30px)
        this.projectile_collision_model = new CollisionModel(projectile_pos, 30, 30);
        checkedTargets = new HashMap<>();
    }


    public boolean hasHitGround() {
        return hasHitGround;
    }

    public void setHitGround() {
        hasHitGround = true;
    }

    public boolean hasChecked(Target target) {
        if (checkedTargets.get(target) == null) return false;
        return checkedTargets.get(target);
    }

    public void setChecked(Target target) {
        checkedTargets.put(target, true);
    }

    public enum Target {
        Entities, Tiles, StaticEntities
    }
}
