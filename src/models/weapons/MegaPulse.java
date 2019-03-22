package models.weapons;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import java.util.ArrayList;
import java.util.List;

public class MegaPulse extends Weapon {
    private List<Integer> hit_indices;

    public MegaPulse() {
        super();
        hit_indices = new ArrayList<>();
        try {
            bullet_texture = new Image("assets/bullets/mega_pulse.png").getTexture();
        } catch (SlickException e) {
            e.printStackTrace();
        }
        // individual MegaPulse specs
        bullet_damage = 3000;
        bullet_speed = 0.5f;
        shot_reload_time = 500;
    }

    public boolean hasAlreadyHit(int idx) {
        if (!hit_indices.contains(idx)) {
            hit_indices.add(idx);
            return false;
        } else return true;
    }

    public void clearHitList(){
        hit_indices.clear();
    }
}
