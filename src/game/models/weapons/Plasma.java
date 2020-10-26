package game.models.weapons;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.opengl.Texture;

public class Plasma extends Weapon {

    private static Sound plasma_fire_sound;
    private static Texture plasma_hud_texture;
    private static Texture plasma_bullet_texture;

    public Plasma(boolean isDrivable) {
        super();
        try {
            if (plasma_bullet_texture == null)
                plasma_bullet_texture = new Image("assets/bullets/plasma.png").getTexture();
            projectile_texture = plasma_bullet_texture;

            if (plasma_hud_texture == null)
                plasma_hud_texture = new Image("assets/hud/weapons/plasma.png").getTexture();
            if (isDrivable) weapon_hud_image = new Image(plasma_hud_texture);

            if (plasma_fire_sound == null) {
                plasma_fire_sound = new Sound("audio/sounds/plasma_shot.ogg");
            }
            fire_sound = plasma_fire_sound;
        } catch (SlickException e) {
            e.printStackTrace();
        }
        // individual Plasma specs
        bullet_damage = 100;
        shot_reload_time = 400;
        if (!isDrivable) shot_reload_time *= 11;
    }
}
