package player;

import models.war_attenders.WarAttender;
import models.war_attenders.soldiers.Soldier;
import models.war_attenders.tanks.Tank;
import org.newdawn.slick.*;

public class Player {
    private WarAttender warAttender;

    public Player(WarAttender warAttender) {
        this.warAttender = warAttender;
    }

    public void render() {
        warAttender.draw();
    }

    public void update(GameContainer gameContainer, int deltaTime) {
        warAttender.update(gameContainer, deltaTime);
    }

    public WarAttenderType getWarAttenderType() {
        if (warAttender instanceof Soldier) return WarAttenderType.SOLDIER;
        if (warAttender instanceof Tank) return WarAttenderType.TANK;
        else throw new IllegalStateException("Not a soldier, not a tank!");
    }

    public WarAttender getWarAttender() {
        return warAttender;
    }

    public enum WarAttenderType {
        SOLDIER, PLANE, TANK
    }
}
