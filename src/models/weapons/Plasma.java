package models.weapons;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class Plasma extends Weapon {

    public Plasma(boolean isDrivable) {
        super();
        try {
            weapon_hud_image = new Image("assets/hud/weapons/plasma.png");
            projectile_texture = new Image("assets/bullets/plasma.png").getTexture();
            fire_sound = new Sound("audio/sounds/plasma_shot.ogg");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        // individual Plasma specs
        bullet_damage = 100;
        shot_reload_time = 400;
        if (!isDrivable) shot_reload_time *= 11;
    }
}
