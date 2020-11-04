package game.models.entities;

import game.levels.Level;
import game.models.weapons.MegaPulse;
import game.util.WayPointManager;
import game.logic.level_listeners.EntityDeleteListener;
import game.models.Element;
import game.models.weapons.Weapon;
import game.models.weapons.projectiles.iGroundTileDamageWeapon;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

import java.util.ArrayList;
import java.util.List;

public abstract class Entity extends Element {

    protected List<Weapon> weapons;
    protected Image health_bar_image;
    protected Vector2f health_bar_position;

    // specs related
    protected static final float MAX_HEALTH = 100;
    protected float current_health;
    public boolean isHostile, isDestroyed, isMandatory;
    protected boolean isEnemyNear;

    // game.graphics related
    private static Texture health_bar_texture_enemy, health_bar_texture_friendly;

    // model related
    protected Vector2f health_bar_offset;

    // listener
    protected EntityDeleteListener level_delete_listener;

    public Entity(Vector2f startPos, boolean isHostile) {
        this.isHostile = isHostile;
        this.position = startPos;
        health_bar_position = new Vector2f(startPos);
        weapons = new ArrayList<>();    // 3 weapons -> WEAPON_1, WEAPON_2 and MEGA_PULSE
        current_health = MAX_HEALTH;

        // LOAD TEXTURES
        try {
            if (isHostile) {
                if (health_bar_texture_enemy == null) {
                    health_bar_texture_enemy = new Image("assets/healthbars/healthbar_enemy.png")
                            .getTexture();
                }
                health_bar_image = new Image(health_bar_texture_enemy);
            } else {
                if (health_bar_texture_friendly == null) {
                    health_bar_texture_friendly = new Image("assets/healthbars/healthbar_friendly.png")
                            .getTexture();
                }
                health_bar_image = new Image(health_bar_texture_friendly);
            }
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        health_bar_offset = new Vector2f((health_bar_image.getWidth() + 12) / 2.f, base_image.getHeight() / 2.f + 15);
        setHealthBarPosition(position);
    }

    @Override
    public void update(GameContainer gc, int deltaTime) {
        // BULLETS
        for (Weapon weapon : weapons) {
            weapon.update(deltaTime);
        }
    }

    @Override
    public void editorUpdate(GameContainer gc, int deltaTime) {
        setHealthBarPosition(this.position);
    }

    @Override
    public void draw(Graphics graphics) {
        // BULLETS
        for (Weapon weapon : weapons) {
            weapon.draw(graphics);
        }
    }

    protected void drawHealthBar(Graphics graphics) {
        health_bar_image.draw(health_bar_position.x, health_bar_position.y);
        // draw health bar damage using a black rectangle
        graphics.setColor(Color.black);
        if (current_health > 0) {
            graphics.fillRect(
                    health_bar_position.x + 44.f,
                    health_bar_position.y + 1.f,
                    -(29 - ((current_health) / ((MAX_HEALTH) / 29))),
                    5.f
            );
        } else {    // destroyed (rectangle is full black size)
            graphics.fillRect(
                    health_bar_position.x + 44.f,
                    health_bar_position.y + 1.f,
                    -29.f,
                    5.f
            );
        }
    }

    public void shootAtEnemies(MovableEntity player, List<? extends Entity> enemies_of_entity, int deltaTime) {
        if (isDestroyed) return;
        if (this instanceof MovableEntity) {
            if (((MovableEntity) this).isDrivable) return;
        }
        float xPos, yPos, dist;
        if (player != null) {
            // player not null means it's a hostile tank
            // calculate dist between the player and the enemy
            xPos = player.position.x;
            yPos = player.position.y;
            dist = (float) Math.sqrt((xPos - position.x) * (xPos - position.x)
                    + (yPos - position.y) * (yPos - position.y));
        } else {
            // player null means it's a friendly tank
            xPos = 0;
            yPos = 0;
            dist = Float.MAX_VALUE;
        }
        // calculate dist between each tank and all its enemies
        for (Entity enemy_war_attender : enemies_of_entity) {
            float next_xPos = enemy_war_attender.position.x;
            float next_yPos = enemy_war_attender.position.y;
            float next_dist = WayPointManager.dist(position, new Vector2f(next_xPos, next_yPos));

            if (next_dist < dist) {
                dist = next_dist;
                xPos = next_xPos;
                yPos = next_yPos;
            }
        }

        // aim at the closest enemy
        float rotationDegree;
        if (dist < 700) {
            isEnemyNear = true;
            // aim at game.player
            rotationDegree = WayPointManager.calculateAngleToRotateTo(position, new Vector2f(xPos, yPos));
            changeAimingDirection(rotationDegree, deltaTime);
        } else {
            isEnemyNear = false;
        }
        if (dist < 500) {
            // fire
            fireWeapon(WeaponType.WEAPON_1);
            fireWeapon(WeaponType.WEAPON_2);
        }
    }

    public void fireWeapon(WeaponType weaponType) {
        if (weapons.size() == 0) return;
        Weapon weapon = null;
        switch (weaponType) {
            case WEAPON_1:
                weapon = getWeapon(WeaponType.WEAPON_1);
                break;
            case WEAPON_2:
                weapon = getWeapon(WeaponType.WEAPON_2);
                break;
            case MEGA_PULSE:
                weapon = getWeapon(WeaponType.MEGA_PULSE);
                break;
        }
        if (weapon == null) return;  // does not have a WEAPON_2, so return
        weapon.fire(position.x, position.y, getAimingDirection());
    }

    public Weapon getWeapon(WeaponType weaponType) {
        switch (weaponType) {
            case WEAPON_1:
                return weapons.get(0);
            case WEAPON_2:
                if (weapons.size() < 2) return null;    // does not have a WEAPON_2
                if (weapons.get(1) instanceof MegaPulse) return null;
                else return weapons.get(1);
            case MEGA_PULSE:
                return weapons.get(weapons.size() - 1);     // mega pulse is always at the end of the weapons list
        }
        return null;
    }

    public abstract void changeAimingDirection(float degree, int deltaTime);

    public float getAimingDirection() {
        return base_image.getRotation();
    }

    public List<Weapon> getWeapons() {
        return weapons;
    }

    @Override
    public void setPosition(Vector2f position) {
        super.setPosition(position);
        setHealthBarPosition(position);
    }

    public abstract void setRotation(float degree);

    protected void setHealthBarPosition(Vector2f position) {
        health_bar_position.x = position.x - health_bar_offset.x;
        health_bar_position.y = position.y - health_bar_offset.y;
    }

    protected void changeHealth(float amount, float armor) {
        if (amount < 0) {
            // damage
            current_health += amount / armor;
            if (current_health <= 0) {
                isDestroyed = true;
            }
        } else {
            // heal
            current_health += amount;
            if (current_health > MAX_HEALTH) current_health = MAX_HEALTH;
        }
    }

    public abstract void changeHealth(float amount);

    public boolean isMaxHealth() {
        return current_health == MAX_HEALTH;
    }

    public int getHealth() {
        return (int) current_health;
    }

    public abstract int getScoreValue();

    public abstract float getRotation();

    public enum WeaponType {
        WEAPON_1, WEAPON_2, MEGA_PULSE
    }

    public void addListeners(Level level) {
        this.level_delete_listener = level;
        for (Weapon weapon : weapons) {
            if (weapon instanceof iGroundTileDamageWeapon) {
                ((iGroundTileDamageWeapon) weapon).addListener(level);
            }
        }
    }

}
