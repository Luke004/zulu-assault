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

    public void draw(Graphics graphics) {
        current_warAttender.draw(graphics);

        // draw all planes or tanks the user used to ride and got out by pressing 'shift'
        for (WarAttender warAttender : old_warAttenders) {
            warAttender.draw(graphics);
        }
    }

    public void update(GameContainer gameContainer, int deltaTime) {
        current_warAttender.update(gameContainer, deltaTime);

        // update all planes or tanks the user used to ride and got out from by pressing 'shift'
        for (WarAttender warAttender : old_warAttenders) {
            warAttender.update(gameContainer, deltaTime);
        }
    }

    public WarAttenderType getWarAttenderType() {
        if (current_warAttender instanceof Soldier) return WarAttenderType.SOLDIER;
        if (current_warAttender instanceof Tank) return WarAttenderType.TANK;
        else throw new IllegalStateException("Not a soldier, not a tank!");
    }

    /*
    soldier goes back in a once used tank or plane
     */
    public void setWarAttender(WarAttender old_warAttender) {
        old_warAttenders.remove(old_warAttender);
        this.current_warAttender = old_warAttender;
        old_warAttender.setAccessibleAnimation(false);
    }

    /*
    soldier goes out of a tank or plane, so set the players
    current_warAttender -> soldier
    old_warAttender -> used tank or plane (save this in 'old_warAttenders' list)
     */
    public void setWarAttender(WarAttender soldier, WarAttender old_warAttender) {
        this.current_warAttender = soldier;
        old_warAttender.setAccessibleAnimation(true);
        old_warAttenders.add(old_warAttender);
    }

    public WarAttender getWarAttender() {
        return current_warAttender;
    }

    public List<WarAttender> getOldWarAttenders(){
        return old_warAttenders;
    }

    public enum WarAttenderType {
        SOLDIER, PLANE, TANK
    }
}
