package game.models.weapons;

import settings.UserSettings;
import game.models.weapons.projectiles.GroundBullet;
import game.models.weapons.projectiles.Projectile;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Weapon {

    protected Image weapon_hud_image;   // the image for the weapon drawn on the HUD
    protected Sound fire_sound;  // fire sound of the weapon

    public Texture projectile_texture;
    protected List<Projectile> projectile_list;

    // specs related
    float bullet_damage, shot_reload_time, current_reload_time;

    public Weapon() {
        this.projectile_list = new ArrayList<>();
    }

    public void update(int deltaTime) {
        if (current_reload_time < shot_reload_time) {
            current_reload_time += deltaTime;
        }
        Iterator<Projectile> iterator = projectile_list.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            projectile.update(deltaTime);
            // remove bullet if max lifetime was reached
            if (projectile.lifetime > projectile.maxLifetime) {
                onProjectileRemove(projectile);
                iterator.remove();
            }
        }
    }

    public void onProjectileRemove(Projectile projectile) {
        // method to override
    }

    public void draw(Graphics graphics) {
        for (Projectile b : projectile_list) {
            b.draw(graphics);
        }
    }

    public void fire(float spawnX, float spawnY, float rotation_angle) {
        if (canFire()) {
            current_reload_time = 0;    // reset the reload time when a shot is fired
            spawnX += -Math.sin(((rotation_angle) * Math.PI) / 180) * -30.f;
            spawnY += Math.cos(((rotation_angle) * Math.PI) / 180) * -30.f;
            Vector2f bullet_spawn = new Vector2f(spawnX, spawnY);

            float xVal = (float) Math.sin(rotation_angle * Math.PI / 180);
            float yVal = (float) -Math.cos(rotation_angle * Math.PI / 180);
            Vector2f bullet_dir = new Vector2f(xVal, yVal);

            Projectile bullet = getNewBullet(bullet_spawn, bullet_dir, rotation_angle);
            projectile_list.add(bullet);
            if (fire_sound != null) {
                fire_sound.play(1.f, UserSettings.soundVolume);
            }
        }
    }

    protected Projectile getNewBullet(Vector2f bullet_spawn, Vector2f bullet_dir, float rotation_angle) {
        return new GroundBullet(bullet_spawn, bullet_dir, rotation_angle, projectile_texture);
    }

    public void loadProjectile(Vector2f pos, Vector2f dir, float rotation, int lifetimeLeft) {
        Projectile projectile = getNewBullet(pos, dir ,rotation);
        projectile.lifetime = lifetimeLeft;
        this.projectile_list.add(projectile);
    }

    public boolean canFire() {
        return current_reload_time >= shot_reload_time;
    }

    public Iterator<Projectile> getProjectileIterator() {
        return projectile_list.iterator();
    }

    public List<Projectile> getProjectileList() {
        return projectile_list;
    }

    public float getBulletDamage() {
        return bullet_damage;
    }

    public Image getHUDImage() {
        return weapon_hud_image;
    }
}
