package game.models.weapons;

import game.models.entities.Entity;

import java.util.ArrayList;
import java.util.List;

public abstract class PiercingWeapon extends Weapon {

    private List<Integer> hit_indices;

    public PiercingWeapon() {
        super();
        hit_indices = new ArrayList<>();
    }

    public boolean hasAlreadyHit(int idx) {
        if (!hit_indices.contains(idx)) {
            hit_indices.add(idx);
            return false;
        } else return true;
    }

    public boolean hasAlreadyHit(Entity entity) {
        if (!hit_indices.contains(entity.hashCode())) {
            hit_indices.add(entity.hashCode());
            return false;
        } else return true;
    }

    public void clearHitList() {
        hit_indices.clear();
    }
}
