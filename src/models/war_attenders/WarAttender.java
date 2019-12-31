package models.war_attenders;

import logic.WayPointManager;
import models.weapons.MegaPulse;
import models.weapons.Weapon;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

import java.util.ArrayList;
import java.util.List;

public abstract class WarAttender {
    public List<Weapon> weapons;
    public Image health_bar_image;
    // specs related
    public float current_health, max_health, armor;
    public boolean isHostile, isDestroyed;
    protected int scoreValue;

    public Vector2f position;

    public WarAttender(Vector2f startPos, boolean isHostile) {
        this.isHostile = isHostile;
        this.position = startPos;
        weapons = new ArrayList<>();    // 3 weapons -> WEAPON_1, WEAPON_2 and MEGA_PULSE
        if (isHostile) {
            try {
                health_bar_image = new Image("assets/healthbars/healthbar_enemy.png");
            } catch (SlickException e) {
                e.printStackTrace();
            }
        } else {
            try {
                health_bar_image = new Image("assets/healthbars/healthbar_friendly.png");
            } catch (SlickException e) {
                e.printStackTrace();
            }
        }

        scoreValue = 100;   // default score value
    }

    public void init() {

    }

    public void update(GameContainer gc, int deltaTime) {
        // BULLETS
        for (Weapon weapon : weapons) {
            weapon.update(deltaTime);
        }
    }

    public void draw(Graphics graphics) {
        health_bar_image.draw(position.x - 7.5f, position.y - 15);
        // draw health bar damage using a black rectangle
        graphics.setColor(Color.black);
        if (current_health > 0) {
            graphics.fillRect(position.x + 36, position.y - 14, -(29 - ((current_health) / ((max_health) / 29))), 5);
        } else {    // destroyed (rectangle is full black size)
            graphics.fillRect(position.x + 36, position.y - 14, -29, 5);
        }

        // BULLETS
        for (Weapon weapon : weapons) {
            weapon.draw(graphics);
        }
    }

    public void shootAtEnemies(MovableWarAttender player, List<? extends WarAttender> enemies_of_tank, int deltaTime) {
        if (isDestroyed) return;
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
        for (WarAttender enemy_war_attender : enemies_of_tank) {
            float next_xPos = enemy_war_attender.position.x;
            float next_yPos = enemy_war_attender.position.y;
            float next_dist = (float) Math.sqrt((next_xPos - position.x) * (next_xPos - position.x)
                    + (next_yPos - position.y) * (next_yPos - position.y));
            if (next_dist < dist) {
                dist = next_dist;
                xPos = next_xPos;
                yPos = next_yPos;
            }
        }

        // aim at the closest enemy
        float rotationDegree;
        if (dist < 500) {
            // aim at player and fire
            rotationDegree = WayPointManager.calculateAngle(position, new Vector2f(xPos, yPos));

            setRotation(rotationDegree);

            fireWeapon(MovableWarAttender.WeaponType.WEAPON_1);
        }
    }

    public abstract void fireWeapon(MovableWarAttender.WeaponType weapon);

    public abstract void setRotation(float degree);

    public Weapon getWeapon(MovableWarAttender.WeaponType weaponType) {
        switch (weaponType) {
            case WEAPON_1:
                return weapons.get(0);
            case WEAPON_2:
                if (weapons.size() == 2) return null;    // does not have a WEAPON_2
                else return weapons.get(1);
            case MEGA_PULSE:
                if (weapons.size() == 2) return weapons.get(1);
                else return weapons.get(2);
        }
        return null;
    }

    public List<Weapon> getWeapons() {
        return weapons;
    }

    public void changeHealth(float amount) {
        if (amount < 0) {
            // damage
            current_health += amount / armor;
            if (current_health <= 0) {
                isDestroyed = true;
            }
        } else {
            // heal
            current_health += amount;
            if (current_health > max_health) current_health = max_health;
        }
    }

    //protected abstract void showDestructionAnimation(Graphics graphics);

    public boolean isMaxHealth() {
        return current_health == max_health;
    }

    public int getHealth() {
        return (int) current_health;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public enum WeaponType {
        WEAPON_1, WEAPON_2, MEGA_PULSE
    }
}
