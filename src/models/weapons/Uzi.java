package models.weapons;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Uzi extends Weapon {

    public Uzi() {
        super();
        try {
            bullet_texture = new Image("assets/bullets/bullet_small.png").getTexture();
        } catch (SlickException e) {
            e.printStackTrace();
        }
        // individual Uzi specs
        bullet_damage = 50;
        bullet_speed = 0.8f;
        shot_reload_time = 300;
    }
}
