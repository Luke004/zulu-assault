package models.war_attenders.planes;

import logic.WayPointManager;
import models.CollisionModel;
import models.war_attenders.MovableWarAttender;
import models.weapons.AGM;
import models.weapons.Uzi;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class GreenEnemyPlane extends Plane {

    public GreenEnemyPlane(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        // attributes equal for humans and bots
        max_health = 100;
        current_health = max_health;
        armor = 60; // unknown

        scoreValue = 500;

        setMoving(true);    // green plane is always flying

        if (isDrivable) {
            // individual GreenEnemyPlane attributes for human players
            max_speed = 0.15f;
            rotate_speed = 0.15f;
        } else {
            // individual GreenEnemyPlane attributes for bots
            max_speed = 0.15f;
            rotate_speed = 0.3f;
        }

        current_speed = max_speed;  // speed is always the same for this plane

        weapons.add(new Uzi(isDrivable));  // WEAPON_1
        weapons.add(new AGM(true));  // WEAPON_2

        try {
            base_image = new Image("assets/war_attenders/planes/green_enemy_plane.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        collisionModel = new CollisionModel(position, base_image.getWidth(), base_image.getHeight());
        super.init();
    }

    @Override
    public void update(GameContainer gc, int deltaTime) {
        super.update(gc, deltaTime);
        if (isMoving) {
            fly(deltaTime); // the plane is always flying forward
        }
    }

    public void fly(int deltaTime) {
        calculateMovementVector(deltaTime, Direction.FORWARD);
        position.add(dir);
        collisionModel.update(base_image.getRotation());
    }

    @Override
    public void onCollision(MovableWarAttender enemy) {
        // a plane doesn't have collisions
    }

    @Override
    public void blockMovement() {
        // a plane doesn't get its movement blocked
    }

    @Override
    public float getRotation() {
        return base_image.getRotation();
    }

    @Override
    public void fireWeapon(WeaponType weapon) {
        switch (weapon) {
            case WEAPON_1:
                weapons.get(0).fire(position.x, position.y, base_image.getRotation());
                break;
            case WEAPON_2:
                if (weapons.size() == 2) return;    // does not have a WEAPON_2, so return
                weapons.get(1).fire(position.x, position.y, base_image.getRotation());
                break;
            case MEGA_PULSE:
                if (weapons.size() == 2) {   // does not have a WEAPON_2, MEGA_PULSE it at index [1]
                    weapons.get(1).fire(position.x, position.y, base_image.getRotation());
                } else {    // does have a WEAPON_2, MEGA_PULSE it at index [2]
                    weapons.get(2).fire(position.x, position.y, base_image.getRotation());
                }
                break;
        }
    }

    @Override
    public void setRotation(float angle) {
        float rotation = WayPointManager.getShortestAngle(base_image.getRotation(), angle);
        //System.out.println("shortest angle: " + rotation);
        if (rotation == 0) return;

        if (rotation < 0) {
            base_image.rotate(-rotate_speed * 5);
        } else {
            base_image.rotate(rotate_speed * 5);
        }
    }
}
