package models.weapons;

import models.war_attenders.WarAttender;

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

    public boolean hasAlreadyHit(WarAttender warAttender) {
        if (!hit_indices.contains(warAttender.hashCode())) {
            hit_indices.add(warAttender.hashCode());
            return false;
        } else return true;
    }

    public void clearHitList() {
        hit_indices.clear();
    }
}
