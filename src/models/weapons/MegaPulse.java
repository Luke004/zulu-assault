package models.weapons;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class MegaPulse extends Weapon {
    public MegaPulse() {
        super();
        try {
            bullet_texture = new Image("assets/bullets/mega_pulse.png").getTexture();
        } catch (SlickException e) {
            e.printStackTrace();
        }
        // individual MegaPulse specs
        bullet_damage = 60;
        bullet_speed = 0.5f;
        shot_reload_time = 500;
    }
}
