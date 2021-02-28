package game.models.weapons;

import game.models.entities.Entity;
import game.models.weapons.projectiles.AirRocket;
import game.models.weapons.projectiles.GuidedRocket;
import game.models.weapons.projectiles.Projectile;
import game.util.WayPointManager;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;


public class GuidedRockets extends DoubleRocketLauncher {

    private static Texture guided_hud_texture;

    public GuidedRockets(boolean isDrivable, int SIDE_OFFSET) {
        super(isDrivable, SIDE_OFFSET);

        try {
            if (guided_hud_texture == null)
                guided_hud_texture = new Image("assets/hud/weapons/guided.png").getTexture();
            if (isDrivable) weapon_hud_image = new Image(guided_hud_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }

        BUFFER_SIZE *= 2;   // double the buffer size, since this is a double rocket launcher
    }

    @Override
    protected Projectile addRocket(float spawnX, float spawnY, float rotation_angle, float side_offset) {
        Vector2f bullet_spawn = calculateBulletSpawn(spawnX, spawnY, rotation_angle, side_offset);
        Vector2f bullet_dir = calculateBulletDir(rotation_angle);
        Animation preparedRocket = prepareNextRocket(rotation_angle);
        return new GuidedRocket(bullet_spawn, bullet_dir, rotation_angle, projectile_texture, preparedRocket);
    }

}
