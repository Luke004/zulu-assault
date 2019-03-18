package player;

import models.war_attenders.WarAttender;
import models.war_attenders.soldiers.PlayerSoldier;
import models.war_attenders.soldiers.Soldier;
import models.war_attenders.tanks.Tank;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

public class Player {
    private WarAttender base_soldier;
    private WarAttender current_warAttender;
    private int[] items;

    public void init(WarAttender current_warAttender) {
        current_warAttender.showAccessibleAnimation(false);
        this.current_warAttender = current_warAttender;
        items = new int[4];
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
    public void setWarAttender(WarAttender warAttender, EnterAction action) {
        switch (action) {
            case ENTERING:
                if (base_soldier == null) {
                    base_soldier = current_warAttender;
                }
                this.current_warAttender = warAttender;
                break;
            case LEAVING:
                Tank tank = ((Tank) warAttender);
                Vector2f spawn_position = tank.calculateSoldierSpawnPosition();
                if (base_soldier == null) {
                    base_soldier = new PlayerSoldier(spawn_position.x, spawn_position.y);
                } else {
                    // soldier already exists, set its position to tank current pos
                    ((Soldier) base_soldier).setPosition(spawn_position);
                }
                // set soldiers rotation so he's facing away from the tank
                ((Soldier) base_soldier).setRotation(tank.getRotation() - 180);
                this.current_warAttender = base_soldier;
                break;
        }
    }

    public WarAttender getWarAttender() {
        return current_warAttender;
    }

    public void addItem(Item item) {
        switch (item) {
            case INVINCIBLE:
                items[0]++;
                break;
            case EMP:
                items[1]++;
                break;
            case MEGA_PULSE:
                items[2]++;
                break;
            case EXPAND:
                items[3]++;
                break;
        }
    }

    public void removeItem(Item item) {
        switch (item) {
            case INVINCIBLE:
                items[0]--;
                break;
            case EMP:
                items[1]--;
                break;
            case MEGA_PULSE:
                items[2]--;
                break;
            case EXPAND:
                items[3]--;
                break;
        }
    }

    public int [] getItems(){
        return items;
    }

    public enum Item {
        INVINCIBLE, EMP, MEGA_PULSE, EXPAND
    }

    public enum EnterAction {
        ENTERING, LEAVING
    }

    public enum WarAttenderType {
        SOLDIER, PLANE, TANK
    }
}
