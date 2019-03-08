package models.war_attenders.tanks;

import models.war_attenders.WarAttender;
import models.war_attenders.soldiers.Soldier;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Vector2f;
import player.Player;

public abstract class Tank extends WarAttender {
    Image turret;
    float turret_rotate_speed;

    public Tank(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);
    }

    @Override
    public void draw(Graphics graphics) {
        base_image.draw(position.x - base_image.getWidth() / 2, position.y - base_image.getHeight() / 2);
        turret.draw(position.x - turret.getWidth() / 2, position.y - turret.getHeight() / 2);
        if (show_accessible_animation) {
            accessible_animation.draw(position.x - base_image.getWidth() / 4, position.y - base_image.getHeight() + 17);
        }
        collisionModel.draw(graphics);
    }

    @Override
    public void update(GameContainer gc, int deltaTime) {
        if (show_accessible_animation) {
            accessible_animation.update(deltaTime);
        }
        collisionModel.rotate(base_image.getRotation());
        //System.out.println(collisionModel.rotate(base_image.getRotation())[1].x);
    }

    public Vector2f calculateSoldierSpawnPosition() {
        // set player 10 pixels behind the tank
        final float DISTANCE = 10;
        final float SPAWN_X = 0;
        final float SPAWN_Y = base_image.getHeight() / 2 + DISTANCE;

        float xVal = (float) (Math.cos(((base_image.getRotation()) * Math.PI) / 180) * SPAWN_X
                + -Math.sin(((base_image.getRotation()) * Math.PI) / 180) * SPAWN_Y);
        float yVal = (float) (Math.sin(((base_image.getRotation()) * Math.PI) / 180) * SPAWN_X
                + Math.cos(((base_image.getRotation()) * Math.PI) / 180) * SPAWN_Y);
        return new Vector2f(xVal + position.x, yVal + position.y);
    }

    public void rotateTurret(RotateDirection r, int deltaTime) {
        switch (r) {
            case ROTATE_DIRECTION_LEFT:
                turret.rotate(-turret_rotate_speed * deltaTime);
                break;
            case ROTATE_DIRECTION_RIGHT:
                turret.rotate(turret_rotate_speed * deltaTime);
                break;
        }
    }

    @Override
    public float getRotation() {
        return base_image.getRotation();
    }

    @Override
    public void rotate(RotateDirection r, int deltaTime) {
        float degree;
        switch (r) {
            case ROTATE_DIRECTION_LEFT:
                degree = -rotate_speed * deltaTime;
                base_image.rotate(degree);
                turret.rotate(degree);
                break;
            case ROTATE_DIRECTION_RIGHT:
                degree = rotate_speed * deltaTime;
                base_image.rotate(degree);
                turret.rotate(degree);
                break;
        }
    }

    public void onCollision(WarAttender enemy){
        if(enemy instanceof Tank){  // enemy is a tank
            current_speed = 0.f;    // stop movement as long as there's collision
        } else if (enemy instanceof Soldier){   // enemy is a soldier (bad for him)
            // kill soldier
        }
        // plane instanceof is not needed, nothing happens there
    }

    /*
    let the tank bounce a few meters back from its current position
     */
    private void bounceBack(){
        // TODO
    }
}
