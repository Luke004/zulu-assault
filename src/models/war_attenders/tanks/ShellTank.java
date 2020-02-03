package models.war_attenders.tanks;

import models.CollisionModel;
import models.weapons.Shell;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class ShellTank extends Tank {

    private static Texture shell_tank_hostile_texture, shell_tank_friendly_texture,
            shell_tank_hostile_turret_texture, shell_tank_friendly_turret_texture;

    public ShellTank(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        // attributes equal for humans and bots
        armor = 60;

        scoreValue = 1000;

        if (isDrivable) {
            // individual ShellTank attributes for human players
            max_speed = 0.15f;
            backwards_speed = max_speed / 2;
            acceleration_factor = 0.00005f;
            deceleration_factor = 0.0009f;
            rotate_speed = 0.2f;
        } else {
            // individual ShellTank attributes for bots
            max_speed = 0.05f;
            backwards_speed = max_speed / 2;
            acceleration_factor = 0.00005f;
            deceleration_factor = 0.0009f;
            rotate_speed = 0.05f;
        }

        weapons.add(new Shell(isDrivable));   // WEAPON_1

        // LOAD TEXTURES
        try {
            if (isHostile) {
                if (shell_tank_hostile_texture == null) {
                    shell_tank_hostile_texture = new Image("assets/war_attenders/tanks/shell_tank_hostile.png")
                            .getTexture();
                }
                base_image = new Image(shell_tank_hostile_texture);
                if (shell_tank_hostile_turret_texture == null) {
                    shell_tank_hostile_turret_texture =
                            new Image("assets/war_attenders/tanks/shell_tank_hostile_turret.png")
                                    .getTexture();
                }
                turret = new Image(shell_tank_hostile_turret_texture);
            } else {    // friendly
                if (shell_tank_friendly_texture == null) {
                    shell_tank_friendly_texture = new Image("assets/war_attenders/tanks/shell_tank_friendly.png")
                            .getTexture();
                }
                base_image = new Image(shell_tank_friendly_texture);
                if (shell_tank_friendly_turret_texture == null) {
                    shell_tank_friendly_turret_texture =
                            new Image("assets/war_attenders/tanks/shell_tank_friendly_turret.png")
                                    .getTexture();
                }
                turret = new Image(shell_tank_friendly_turret_texture);
            }
        } catch (SlickException e) {
            e.printStackTrace();
        }

        collisionModel = new CollisionModel(position, base_image.getWidth(), base_image.getHeight());
        super.init();
    }
}
