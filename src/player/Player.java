package player;

import models.war_attenders.WarAttender;
import models.war_attenders.soldiers.Soldier;
import models.war_attenders.tanks.Tank;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private WarAttender current_warAttender;
    private List<WarAttender> old_warAttenders;
    private Vector2f mapDir;

    public Player(WarAttender current_warAttender) {
        this.current_warAttender = current_warAttender;
        old_warAttenders = new ArrayList<>();
        mapDir = new Vector2f(0, 0);
    }

    public void render() {
        current_warAttender.draw();

        // render all planes or tanks the user used to ride and got out by pressing 'shift'
        for (WarAttender warAttender : old_warAttenders) {
            warAttender.draw();
        }
    }

    public void update(GameContainer gameContainer, int deltaTime) {
        current_warAttender.update(gameContainer, deltaTime);

        // update all planes or tanks the user used to ride and got out from by pressing 'shift'
        for (WarAttender warAttender : old_warAttenders) {
            warAttender.map_position.add(mapDir);
        }
    }

    public WarAttenderType getWarAttenderType() {
        if (current_warAttender instanceof Soldier) return WarAttenderType.SOLDIER;
        if (current_warAttender instanceof Tank) return WarAttenderType.TANK;
        else throw new IllegalStateException("Not a soldier, not a tank!");
    }

    public void setWarAttender(WarAttender new_warAttender, WarAttender old_warAttender) {
        this.current_warAttender = new_warAttender;
        old_warAttenders.add(old_warAttender);
    }

    public WarAttender getWarAttender() {
        return current_warAttender;
    }

    /*
    give the player an info about where the map is going so it can update its old WarAttenders accordingly (->stand still)
     */
    public void setMapDir(Vector2f mapDir) {
        this.mapDir = mapDir;
    }

    public enum WarAttenderType {
        SOLDIER, PLANE, TANK
    }
}
