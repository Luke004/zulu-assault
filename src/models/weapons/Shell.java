package models.weapons;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Shell extends Weapon {
    public Shell() {
        super();
        try {
            bullet_texture = new Image("assets/bullets/shell.png").getTexture();
        } catch (SlickException e) {
            e.printStackTrace();
        }
        // individual Shell specs
        bullet_damage = 750;
        bullet_speed = 0.8f;
        shot_reload_time = 1000;
    }
}
