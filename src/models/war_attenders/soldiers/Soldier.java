package models.war_attenders.soldiers;

import models.war_attenders.WarAttender;
import models.war_attenders.tanks.Tank;
import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

public abstract class Soldier extends WarAttender {
    Animation animation;

    public Soldier(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);
    }

    @Override
    public void draw(Graphics graphics) {
        animation.draw(position.x - animation.getImage(0).getWidth()/2, position.y-animation.getImage(0).getHeight()/2);
        //collisionModel.draw(graphics);
    }

    @Override
    public void update(GameContainer gameContainer, int deltaTime) {
        animation.update(deltaTime);
        collisionModel.rotate(base_image.getRotation());
    }

    public void startAnimation() {
        animation.start();
    }

    public void stopAnimation() {
        animation.stop();
    }

    public void onCollision(WarAttender enemy){
        if(enemy instanceof Tank){  // enemy is a tank
            current_speed = 0.f;  // stop movement as long as there's collision
        }
        // soldier is not needed, nothing happens
        // plane instanceof is not needed, nothing happens
    }
}
