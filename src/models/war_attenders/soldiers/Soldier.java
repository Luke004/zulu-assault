package models.war_attenders.soldiers;

import models.war_attenders.WarAttender;
import models.war_attenders.tanks.Tank;
import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

public abstract class Soldier extends WarAttender {
    Animation animation;
    private int SOLDIER_WIDTH_HALF, SOLDIER_HEIGHT_HALF;

    public Soldier(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);
    }

    void init() {
        SOLDIER_WIDTH_HALF = animation.getImage(0).getWidth() / 2;
        SOLDIER_HEIGHT_HALF = animation.getImage(0).getHeight() / 2;
    }

    @Override
    public void update(GameContainer gameContainer, int deltaTime) {
        super.update(gameContainer, deltaTime);
        animation.update(deltaTime);
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        animation.draw(position.x - SOLDIER_WIDTH_HALF, position.y - SOLDIER_HEIGHT_HALF);
        //collisionModel.draw(graphics);
    }

    public void startAnimation() {
        animation.start();
    }

    public void stopAnimation() {
        animation.stop();
    }

    public void moveForward(int deltaTime) {
        calculateMovementVector(deltaTime, Direction.FORWARD);
        position.add(dir);
        collisionModel.update(base_image.getRotation());
    }

    public void moveBackwards(int deltaTime) {
        calculateMovementVector(deltaTime, Direction.BACKWARDS);
        position.add(dir);
        collisionModel.update(base_image.getRotation());
    }

    @Override
    public void onCollision(WarAttender enemy) {
        if (enemy instanceof Tank) {  // enemy is a tank
            blockMovement();
        }
        // soldier is not needed, nothing happens
        // plane instanceof is not needed, nothing happens
    }

    @Override
    public void blockMovement() {
        position.sub(dir);  // set the position on last position before the collision
        collisionModel.update(base_image.getRotation());    // update collision model
    }

    public void setPosition(Vector2f position) {
        this.position.x = position.x;
        this.position.y = position.y;
        collisionModel.update(base_image.getRotation());
    }

    @Override
    public void setRotation(float angle) {
        for (int idx = 0; idx < animation.getFrameCount(); ++idx) {
            animation.getImage(idx).setRotation(angle);
        }
    }
}
