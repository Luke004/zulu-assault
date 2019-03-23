package models.war_attenders;

import models.weapons.MegaPulse;
import models.weapons.Weapon;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

import java.util.ArrayList;
import java.util.List;

public abstract class WarAttender {
    public List<Weapon> weapons;
    public Image health_bar_image;
    public Animation accessible_animation;
    public Image accessible_animation_image;
    public boolean show_accessible_animation;
    // specs related
    public float current_health, max_health, armor;
    public boolean isHostile;

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
            initAccessibleAnimation();
        }
    }

    public void init() {
        if (isHostile) {    // double the reload time if its an enemy
            for (Weapon weapon : weapons) {
                weapon.multiplyShotReloadTime(5);
            }
        } else {
            weapons.add(new MegaPulse());  // add the MEGA_PULSE (special item)
        }
    }

    public void update(GameContainer gc, int deltaTime) {
        // BULLETS
        for (Weapon weapon : weapons) {
            weapon.update(deltaTime);
        }
    }

    public void draw(Graphics graphics) {
        health_bar_image.draw(position.x  - 7.5f, position.y - 15);

        // draw health bar damage using a black rectangle
        graphics.setColor(Color.black);
        if (current_health > 0) {
            graphics.fillRect(position.x + 36, position.y - 14, -(29 - (((float) current_health) / (((float) max_health) / 29))), 5);
        } else {    // destroyed (rectangle is full black size)
            graphics.fillRect(position.x + 36, position.y - 14, -29, 5);
        }

        // BULLETS
        for (Weapon weapon : weapons) {
            weapon.draw(graphics);
        }
    }

    private void initAccessibleAnimation() {
        show_accessible_animation = true;
        try {
            accessible_animation_image = new Image("assets/healthbars/accessible_arrow_animation.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        accessible_animation = new Animation(false);

        int x = 0;
        do {
            accessible_animation.addFrame(accessible_animation_image.getSubImage(x, 0, 17, 28), 1000);
            x += 17;
        } while (x <= 34);
        accessible_animation.setCurrentFrame(1);
    }

    public void showAccessibleAnimation(boolean activate) {
        show_accessible_animation = activate;
    }

    public void shootAtEnemies(MovableWarAttender player, List<MovableWarAttender> friendly_war_attenders, int deltaTime) {
        // calculate dist between the player and the enemy
        float xPos = player.position.x;
        float yPos = player.position.y;
        float dist = (float) Math.sqrt((xPos - position.x) * (xPos - position.x)
                + (yPos - position.y) * (yPos - position.y));

        // calculate dist between each friend and the enemy
        for(MovableWarAttender friendly_war_attender : friendly_war_attenders){
           float next_xPos = friendly_war_attender.position.x;
           float next_yPos = friendly_war_attender.position.y;
           float next_dist = (float) Math.sqrt((next_xPos - position.x) * (next_xPos - position.x)
                   + (next_yPos - position.y) * (next_yPos - position.y));
           if(next_dist < dist) {
               dist = next_dist;
               xPos = next_xPos;
               yPos = next_yPos;
           }
        }

        // aim at the closest enemy
        float rotationDegree;
        if (dist < 500) {
            // aim at player and fire
            float m = (position.y - yPos) / (position.x - xPos);
            float x = xPos - position.x;

            if ((x > 0) && m > 0) {
                rotationDegree = (float) (Math.abs(Math.atan(m / 1) * 180.0 / Math.PI) + 90.f);
            } else if (x > 0 && m <= 0) {
                rotationDegree = (float) Math.abs((Math.atan(1 / m) * 180.0 / Math.PI));
            } else if ((x < 0) && (m <= 0)) {
                rotationDegree = (float) (Math.abs((Math.atan(1 / m) * 180.0 / Math.PI)) + 180.f);
            } else {
                rotationDegree = (float) (Math.abs((Math.atan(m / 1) * 180.0 / Math.PI)) + 270.f);
            }
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
        if(amount < 0){
            // damage
            current_health += amount / armor;
        } else {
            // heal
            current_health += amount;
            if(current_health > max_health) current_health = max_health;
        }
    }

    public boolean isMaxHealth() {
        return current_health == max_health;
    }

    public float getHealth() {
        return current_health;
    }

    public enum WeaponType {
        WEAPON_1, WEAPON_2, MEGA_PULSE
    }
}
