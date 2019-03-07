package models.war_attenders.tanks;

import models.war_attenders.WarAttender;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Vector2f;

public abstract class Tank extends WarAttender {
    Image turret;
    float turret_rotate_speed;

    public Tank(Vector2f startPos) {
        super(startPos);
    }

    @Override
    public void draw(Graphics graphics) {
        base_image.draw(position.x - base_image.getWidth() / 2, position.y - base_image.getHeight() / 2);
        turret.draw(position.x - turret.getWidth() / 2, position.y - turret.getHeight() / 2);
        if (accessible_animation != null) {
            accessible_animation.draw(position.x - base_image.getWidth() / 4, position.y - base_image.getHeight() + 17);
        }
        //collisionModel.draw(graphics);
    }

    @Override
    public void update(GameContainer gc, int deltaTime) {
        if (accessible_animation != null) {
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

    public abstract void rotateTurret(RotateDirection r, int deltaTime);
}
