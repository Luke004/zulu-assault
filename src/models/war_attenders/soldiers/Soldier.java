package models.war_attenders.soldiers;

import models.war_attenders.WarAttender;
import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

public abstract class Soldier extends WarAttender {
    Animation animation;

    public Soldier(Vector2f startPos) {
        super(startPos);
    }

    @Override
    public void draw(Graphics graphics) {
        animation.draw(position.x, position.y);
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
}
