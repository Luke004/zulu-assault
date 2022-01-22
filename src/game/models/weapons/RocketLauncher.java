package game.models.weapons;

import settings.UserSettings;
import game.models.weapons.projectiles.GroundRocket;
import game.models.weapons.projectiles.Projectile;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

import java.util.ArrayList;
import java.util.List;

public class RocketLauncher extends Weapon {

    private List<Animation> active_rockets;
    private List<Animation> buffered_rockets;
    protected int BUFFER_SIZE;

    private static Sound rocket_fire_sound;
    private static Texture rocket_launcher_hud_texture;
    private static Texture rocket_launcher_bullet_texture;

    public RocketLauncher(boolean isDrivable) {
        super();

        // individual RocketLauncher specs
        bullet_damage = 500;
        shot_reload_time = 800;
        if (!isDrivable) shot_reload_time *= 5;

        try {
            if (rocket_launcher_bullet_texture == null)
                rocket_launcher_bullet_texture = new Image("assets/bullets/shell.png").getTexture();
            projectile_texture = rocket_launcher_bullet_texture;

            if (rocket_launcher_hud_texture == null)
                rocket_launcher_hud_texture = new Image("assets/hud/weapons/rockets.png").getTexture();
            if (isDrivable) weapon_hud_image = new Image(rocket_launcher_hud_texture);

            if (rocket_fire_sound == null) {
                rocket_fire_sound = new Sound("audio/sounds/rocket_shot.ogg");
            }
            fire_sound = rocket_fire_sound;

            if (!isDrivable) {
                BUFFER_SIZE = 3;
            } else {
                BUFFER_SIZE = 8;    // player needs more buffer size, since he can shoot more often
            }
            buffered_rockets = new ArrayList<>();

            addInstances(BUFFER_SIZE);

            active_rockets = new ArrayList<>();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    protected void addInstances(final int AMOUNT) {
        try {
            int IMAGE_COUNT = 8;
            int x;
            int idx;
            Texture rocket_animation_image_texture = new Image("assets/bullets/rocket_animation.png").getTexture();
            for (idx = 0; idx < AMOUNT; ++idx) {
                x = 0;
                Image rocket_animation_image = new Image(rocket_animation_image_texture);
                Animation rocket_animation = new Animation(false);
                for (int idx2 = 0; idx2 < IMAGE_COUNT; ++idx2) {
                    rocket_animation.addFrame(rocket_animation_image.getSubImage(x, 0, 20, 123), 100);
                    x += 20;
                }
                buffered_rockets.add(rocket_animation);
            }
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fire(float spawnX, float spawnY, float rotation_angle) {
        if (canFire()) {
            current_reload_time = 0;    // reset the reload time when a shot is fired

            Projectile projectile = addRocket(spawnX, spawnY, rotation_angle, 0);
            projectile_list.add(projectile);

            fire_sound.play(1.f, UserSettings.soundVolume);
        }
    }

    protected Projectile addRocket(float spawnX, float spawnY, float rotation_angle, float side_offset) {
        Vector2f bullet_spawn = calculateBulletSpawn(spawnX, spawnY, rotation_angle, side_offset);
        Vector2f bullet_dir = calculateBulletDir(rotation_angle);
        Animation preparedRocket = prepareNextRocket(rotation_angle);
        return new GroundRocket(bullet_spawn, bullet_dir, rotation_angle, projectile_texture, preparedRocket);
    }

    @Override
    protected Projectile getNewBullet(Vector2f bullet_spawn, Vector2f bullet_dir, float rotation_angle) {
        Animation preparedRocket = prepareNextRocket(rotation_angle);
        return new GroundRocket(bullet_spawn, bullet_dir, rotation_angle, projectile_texture, preparedRocket);
    }

    protected Animation prepareNextRocket(float rotation_angle) {
        Animation fresh_rocket = getNextFreshRocket();
        for (int idx = 0; idx < fresh_rocket.getFrameCount(); ++idx) {
            fresh_rocket.getImage(idx).setRotation(rotation_angle);
        }
        fresh_rocket.setCurrentFrame(0);
        fresh_rocket.stopAt(7);
        fresh_rocket.start();
        return fresh_rocket;
    }

    protected Vector2f calculateBulletSpawn(float spawnX, float spawnY, float rotation_angle, float side_offset) {
        float m_spawn_x = spawnX + (float) (Math.cos(((rotation_angle) * Math.PI) / 180) * side_offset
                + -Math.sin(((rotation_angle) * Math.PI) / 180) * -30.f);
        float m_spawn_y = spawnY + (float) (Math.sin(((rotation_angle) * Math.PI) / 180) * side_offset
                + Math.cos(((rotation_angle) * Math.PI) / 180) * -30.f);
        return new Vector2f(m_spawn_x, m_spawn_y);
    }

    protected Vector2f calculateBulletDir(float rotation_angle) {
        float dirX = (float) Math.sin(rotation_angle * Math.PI / 180);
        float dirY = (float) -Math.cos(rotation_angle * Math.PI / 180);
        return new Vector2f(dirX, dirY);
    }

    protected Animation getNextFreshRocket() {
        Animation rocket = null;
        try {
            rocket = buffered_rockets.get(0);
            active_rockets.add(rocket);
            buffered_rockets.remove(rocket);
        } catch (Exception e) {
            System.out.println("Buffered rockets size too low! Increase size of buffered_rockets list!");
        }
        return rocket;
    }

    protected void putRocketBackToBuffer() {
        Animation rocket = active_rockets.get(active_rockets.size() - 1);
        buffered_rockets.add(rocket);
        active_rockets.remove(rocket);
    }

    @Override
    public void onProjectileRemove(Projectile projectile) {
        putRocketBackToBuffer();
    }

}
