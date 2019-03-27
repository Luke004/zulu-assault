package models.weapons;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import java.util.ArrayList;
import java.util.List;

public class MegaPulse extends PiercingWeapon {
    public MegaPulse() {
        super();
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
}
