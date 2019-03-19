package models.war_attenders.tanks;

import models.CollisionModel;
import models.weapons.MachineGun;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class AgileTank extends Tank {

    public AgileTank(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);

        // individual AgileTank attributes
        max_health = 100;
        current_health = max_health;
        max_speed = 0.3f;
        backwards_speed = 0.15f;
        acceleration_factor = 0.0005f;
        deceleration_factor = 0.0005f;
        rotate_speed = 0.15f;
        turret_rotate_speed = 0.2f;
        weapons.add(new MachineGun());  // WEAPON_1

        try {
            base_image = new Image("assets/tanks/agile_tank.png");
            turret = new Image("assets/tanks/agile_tank_turret.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        collisionModel = new CollisionModel(position, base_image.getWidth(), base_image.getHeight());
        super.init();
    }

/*
    @Override
    public void update(GameContainer gc, int deltaTime) {
        super.update(gc, deltaTime);
        System.out.println(isMoving);
    }
*/
}
