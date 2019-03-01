package player;

import models.war_attenders.WarAttender;
import models.war_attenders.soldiers.Soldier;
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
        else throw new IllegalStateException("Not a soldier!");
    }

    public WarAttender getWarAttender() {
        return warAttender;
    }

    public enum WarAttenderType {
        SOLDIER, PLANE, TANK
    }
}
