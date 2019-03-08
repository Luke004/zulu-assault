package player;

import models.war_attenders.WarAttender;
import models.war_attenders.soldiers.Soldier;
import models.war_attenders.tanks.Tank;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

public class Player {
    private WarAttender current_warAttender;

    public Player(WarAttender current_warAttender) {
        current_warAttender.showAccessibleAnimation(false);
        this.current_warAttender = current_warAttender;
    }

    public void draw(Graphics graphics) {
        current_warAttender.draw(graphics);
    }

    public void update(GameContainer gameContainer, int deltaTime) {
        current_warAttender.update(gameContainer, deltaTime);
    }

    public WarAttenderType getWarAttenderType() {
        if (current_warAttender instanceof Soldier) return WarAttenderType.SOLDIER;
        if (current_warAttender instanceof Tank) return WarAttenderType.TANK;
        else throw new IllegalStateException("Not a soldier, not a tank!");
    }

    /*
    soldier goes back in a once used tank or plane
     */
    public void setWarAttender(WarAttender warAttender) {
        this.current_warAttender = warAttender;
    }

    public WarAttender getWarAttender() {
        return current_warAttender;
    }

    public enum WarAttenderType {
        SOLDIER, PLANE, TANK
    }
}
