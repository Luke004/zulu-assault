package game.models.weapons;

import game.models.weapons.projectiles.Flame;
import game.models.weapons.projectiles.Projectile;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class Napalm extends PiercingWeapon {

    private static Texture napalm_hud_texture;

    public Napalm(boolean isDrivable) {
        super();
        try {
            if (napalm_hud_texture == null)
                napalm_hud_texture = new Image("assets/hud/weapons/napalm.png").getTexture();
            if (isDrivable) weapon_hud_image = new Image(napalm_hud_texture);

            projectile_texture = new Image("assets/animations/big_explosion.png").getTexture();
        } catch (SlickException e) {
            e.printStackTrace();
        }
        // individual Napalm specs
        bullet_damage = 90;
        shot_reload_time = 200;
    }


    @Override
    public void fire(float spawnX, float spawnY, float rotation_angle) {
        if (canFire()) {
            clearHitList();
            current_reload_time = 0;    // reset the reload time when a shot is fired
            spawnX += -Math.sin(((rotation_angle) * Math.PI) / 180) * -30.f;
            spawnY += Math.cos(((rotation_angle) * Math.PI) / 180) * -30.f;
            Vector2f bullet_spawn = new Vector2f(spawnX, spawnY);

            float xVal = (float) Math.sin(rotation_angle * Math.PI / 180);
            float yVal = (float) -Math.cos(rotation_angle * Math.PI / 180);
            Vector2f bullet_dir = new Vector2f(xVal, yVal);

            Projectile bullet = new Flame(bullet_spawn, bullet_dir, 0, projectile_texture);
            projectile_list.add(bullet);
        }
    }
}
