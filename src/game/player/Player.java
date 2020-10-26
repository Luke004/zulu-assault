package game.player;

import game.logic.level_listeners.ChangeVehicleListener;
import game.logic.level_listeners.ItemChangeListener;
import game.graphics.hud.HUD;
import game.models.entities.MovableEntity;
import game.models.entities.aircraft.AttackHelicopter;
import game.models.entities.aircraft.Aircraft;
import game.models.entities.robots.Robot;
import game.models.entities.soldiers.PlayerSoldier;
import game.models.entities.soldiers.Soldier;
import game.models.entities.tanks.Tank;
import game.models.weapons.MegaPulse;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

import java.util.Arrays;

import static settings.TileMapData.LEVEL_HEIGHT_PIXELS;
import static settings.TileMapData.LEVEL_WIDTH_PIXELS;

public class Player {
    private PlayerSoldier base_soldier;
    private MovableEntity current_entity;
    private ItemChangeListener GUI_listener;
    private int[] item_amounts;
    private int points;

    private ChangeVehicleListener changeVehicleListener;

    public Player() {
        item_amounts = new int[4];
    }

    public void init(MovableEntity current_entity) {
        current_entity.showAccessibleAnimation(false);
        this.current_entity = current_entity;
        if (this.changeVehicleListener != null) this.changeVehicleListener.onPlayerChangeVehicle();
        base_soldier = new PlayerSoldier(new Vector2f(0, 0), false);
    }

    public void update(GameContainer gameContainer, int deltaTime) {
        current_entity.update(gameContainer, deltaTime);

        // check for map boundaries, don't let the game.player cross them
        if (current_entity.getPosition().x <= 0) {
            current_entity.getPosition().x = 0;
        } else if (current_entity.getPosition().x >= LEVEL_WIDTH_PIXELS) {
            current_entity.getPosition().x = LEVEL_WIDTH_PIXELS;
        } else if (current_entity.getPosition().y <= 0) {
            current_entity.getPosition().y = 0;
        } else if (current_entity.getPosition().y >= LEVEL_HEIGHT_PIXELS) {
            current_entity.getPosition().y = LEVEL_HEIGHT_PIXELS;
        }
    }

    public EntityType getEntityType() {
        if (current_entity instanceof Soldier) return EntityType.SOLDIER;
        if (current_entity instanceof Tank) return EntityType.TANK;
        if (current_entity instanceof Robot) return EntityType.ROBOT;
        if (current_entity instanceof AttackHelicopter) return EntityType.HELICOPTER;
        if (current_entity instanceof Aircraft) return EntityType.PLANE;
        else throw new IllegalStateException("Not a valid entity type!");
    }

    /*
    soldier goes back in a once used tank or plane
     */
    public void setEntity(MovableEntity entity, EnterAction action) {
        switch (action) {
            case ENTERING:
                this.current_entity = entity;
                break;
            case LEAVING:
                Vector2f spawn_position = entity.calculateSoldierSpawnPosition();
                base_soldier.setPosition(spawn_position);
                // set soldiers rotation so he's facing towards the tank at its back
                base_soldier.setRotation(entity.getRotation());
                this.current_entity = base_soldier;
                break;
        }
        this.changeVehicleListener.onPlayerChangeVehicle();
    }

    public MovableEntity getEntity() {
        return current_entity;
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
                if (current_entity.isInvincible()) return;
                idx = 0;
                break;
            case EMP:
                idx = 1;
                break;
            case MEGA_PULSE:
                MegaPulse mega_pulse = (MegaPulse) current_entity.getWeapon(MovableEntity.WeaponType.MEGA_PULSE);
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
        current_entity.activateItem(item);
        GUI_listener.itemActivated(idx);
    }

    public void addChangeVehicleListener(HUD hud) {
        this.changeVehicleListener = hud;
    }

    public enum Item_e {
        INVINCIBILITY, EMP, MEGA_PULSE, EXPAND
    }

    public enum EnterAction {
        ENTERING, LEAVING
    }

    public enum EntityType {
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
