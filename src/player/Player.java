package player;

import logic.level_listeners.ChangeWarAttenderListener;
import logic.level_listeners.ItemChangeListener;
import models.hud.HUD;
import models.war_attenders.MovableWarAttender;
import models.war_attenders.planes.Helicopter;
import models.war_attenders.planes.Plane;
import models.war_attenders.robots.Robot;
import models.war_attenders.soldiers.PlayerSoldier;
import models.war_attenders.soldiers.Soldier;
import models.war_attenders.tanks.Tank;
import models.weapons.MegaPulse;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

import java.util.Arrays;

import static logic.TileMapInfo.LEVEL_HEIGHT_PIXELS;
import static logic.TileMapInfo.LEVEL_WIDTH_PIXELS;

public class Player {
    private PlayerSoldier base_soldier;
    private MovableWarAttender current_warAttender;
    private ItemChangeListener GUI_listener;
    private int[] item_amounts;
    private int points;

    private ChangeWarAttenderListener changeWarAttenderListener;

    public Player() {
        item_amounts = new int[4];
    }

    public void init(MovableWarAttender current_warAttender) {
        current_warAttender.showAccessibleAnimation(false);
        this.current_warAttender = current_warAttender;
        if (this.changeWarAttenderListener != null) this.changeWarAttenderListener.onPlayerChangeWarAttender();
        base_soldier = new PlayerSoldier(new Vector2f(0, 0), false);
    }

    public void update(GameContainer gameContainer, int deltaTime) {
        current_warAttender.update(gameContainer, deltaTime);

        // check for map boundaries, don't let the player cross them
        if (current_warAttender.getPosition().x <= 0) {
            current_warAttender.getPosition().x = 0;
        } else if (current_warAttender.getPosition().x >= LEVEL_WIDTH_PIXELS) {
            current_warAttender.getPosition().x = LEVEL_WIDTH_PIXELS;
        } else if (current_warAttender.getPosition().y <= 0) {
            current_warAttender.getPosition().y = 0;
        } else if (current_warAttender.getPosition().y >= LEVEL_HEIGHT_PIXELS) {
            current_warAttender.getPosition().y = LEVEL_HEIGHT_PIXELS;
        }
    }

    public WarAttenderType getWarAttenderType() {
        if (current_warAttender instanceof Soldier) return WarAttenderType.SOLDIER;
        if (current_warAttender instanceof Tank) return WarAttenderType.TANK;
        if (current_warAttender instanceof Robot) return WarAttenderType.ROBOT;
        if (current_warAttender instanceof Helicopter) return WarAttenderType.HELICOPTER;
        if (current_warAttender instanceof Plane) return WarAttenderType.PLANE;
        else throw new IllegalStateException("Not a valid WarAttender type!");
    }

    /*
    soldier goes back in a once used tank or plane
     */
    public void setWarAttender(MovableWarAttender warAttender, EnterAction action) {
        switch (action) {
            case ENTERING:
                this.current_warAttender = warAttender;
                break;
            case LEAVING:
                Vector2f spawn_position = warAttender.calculateSoldierSpawnPosition();
                base_soldier.setPosition(spawn_position);
                // set soldiers rotation so he's facing towards the tank at its back
                base_soldier.setRotation(warAttender.getRotation());
                this.current_warAttender = base_soldier;
                break;
        }
        this.changeWarAttenderListener.onPlayerChangeWarAttender();
    }

    public MovableWarAttender getWarAttender() {
        return current_warAttender;
    }

    public PlayerSoldier getBaseSoldier() {
        return base_soldier;
    }

    public void addListener(ItemChangeListener gui) {
        this.GUI_listener = gui;
    }

    public void addItem(Item_e item) {
        int idx = -1;
        switch (item) {
            case INVINCIBILITY:
                idx = 0;
                break;
            case EMP:
                idx = 1;
                break;
            case MEGA_PULSE:
                idx = 2;
                break;
            case EXPAND:
                idx = 3;
                break;
            default:
                throw new IllegalStateException("Illegal item index [" + idx + "]!");
        }
        item_amounts[idx]++;
        GUI_listener.itemAdded(idx);
    }

    public void activateItem(Item_e item) {
        int idx = -1;
        switch (item) {
            case INVINCIBILITY:
                if (current_warAttender.isInvincible()) return;
                idx = 0;
                break;
            case EMP:
                idx = 1;
                break;
            case MEGA_PULSE:
                MegaPulse mega_pulse = (MegaPulse) current_warAttender.getWeapon(MovableWarAttender.WeaponType.MEGA_PULSE);
                if (!mega_pulse.canFire()) return;
                else mega_pulse.clearHitList();
                idx = 2;
                break;
            case EXPAND:
                idx = 3;
                break;
            default:
                throw new IllegalStateException("Illegal item index [" + idx + "]!");
        }
        if (item_amounts[idx] == 0) return;
        item_amounts[idx]--;
        current_warAttender.activateItem(item);
        GUI_listener.itemActivated(idx);
    }

    public void addChangeWarAttenderListener(HUD hud) {
        this.changeWarAttenderListener = hud;
    }

    public enum Item_e {
        INVINCIBILITY, EMP, MEGA_PULSE, EXPAND
    }

    public enum EnterAction {
        ENTERING, LEAVING
    }

    public enum WarAttenderType {
        SOLDIER, PLANE, TANK, ROBOT, HELICOPTER
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int amount) {
        this.points += amount;
    }

    public void reset() {
        Arrays.fill(item_amounts, 0);   // reset item amounts
        points = 0; // reset points
    }
}
