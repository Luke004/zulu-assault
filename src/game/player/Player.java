package game.player;

import game.logic.level_listeners.ChangeVehicleListener;
import game.graphics.hud.HUD;
import game.models.entities.Entity;
import game.models.entities.MovableEntity;
import game.models.entities.aircraft.AttackHelicopter;
import game.models.entities.aircraft.Aircraft;
import game.models.entities.robots.Robot;
import game.models.entities.soldiers.PlayerSoldier;
import game.models.entities.soldiers.Soldier;
import game.models.entities.tanks.Tank;
import game.models.weapons.MegaPulse;
import game.models.weapons.Weapon;
import game.models.weapons.projectiles.Projectile;
import game.util.WayPointManager;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import settings.UserSettings;

import java.util.Arrays;
import java.util.Iterator;

import static game.levels.Level.all_entities;
import static game.levels.Level.all_hostile_entities;
import static game.logic.TileMapData.LEVEL_HEIGHT_PIXELS;
import static game.logic.TileMapData.LEVEL_WIDTH_PIXELS;

public class Player {

    private PlayerSoldier base_soldier;
    private MovableEntity current_entity;
    public static int[] item_amounts;
    private static int points;

    private ChangeVehicleListener changeVehicleListener;

    // ITEMS
    // invincibility
    public boolean isInvincible, invincibility_animation_switch;
    private int invincibility_lifetime;
    private final static int INVINCIBILITY_TIME = 10000;   // 10 sec
    public static final int INVINCIBLE_ANIMATION_TIME_SWITCH = 1000;    // once every sec switch to white color while invincible
    public int invincible_animation_current_time;
    // item sounds
    private static Sound expand_use_sound, emp_use_sound, mega_pulse_use_sound;

    public Player() {
        item_amounts = new int[4];
    }

    static {
        try {
            expand_use_sound = new Sound("audio/sounds/expand_use.ogg");
            mega_pulse_use_sound = new Sound("audio/sounds/mega_pulse_use.ogg");
            emp_use_sound = new Sound("audio/sounds/emp_use.ogg");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public void init(MovableEntity current_entity) {
        base_soldier = new PlayerSoldier(new Vector2f(current_entity.getPosition().x, current_entity.getPosition().y), false);
        // player starts as a base soldier (Uzi or rocket-soldier)
        if (current_entity.getClass().getSimpleName().equals("UziSoldier") ||
                current_entity.getClass().getSimpleName().equals("RocketSoldier")) {
            this.current_entity = base_soldier;
            return;
        }
        // player does not start as a soldier
        current_entity.showAccessibleAnimation(false);
        this.current_entity = current_entity;
        if (this.changeVehicleListener != null) this.changeVehicleListener.onPlayerChangeVehicle();
    }

    public void update(GameContainer gameContainer, int deltaTime) {
        current_entity.update(gameContainer, deltaTime);

        base_soldier.update(gameContainer, deltaTime);

        // INVINCIBILITY
        if (isInvincible) {
            // invincibility game.logic itself
            invincibility_lifetime += deltaTime;
            if (invincibility_lifetime > INVINCIBILITY_TIME) {
                isInvincible = false;
                invincibility_lifetime = 0;
            }
            // invincibility animation related
            invincible_animation_current_time += deltaTime;
            if (invincible_animation_current_time >= INVINCIBLE_ANIMATION_TIME_SWITCH) {
                invincibility_animation_switch = !invincibility_animation_switch;
                invincible_animation_current_time = 0;
            }
        }

        // check for map boundaries, don't let the player cross them
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

    public void draw(Graphics graphics) {
        if (isInvincible) {
            current_entity.drawInvincible(graphics, invincibility_animation_switch);
        } else {
            // normal draw
            current_entity.draw(graphics);
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
    public void setEntity(MovableEntity vehicle, EnterAction action) {
        switch (action) {
            case ENTERING:
                this.current_entity = vehicle;
                break;
            case LEAVING:
                setSoldierSpawnPos(base_soldier, vehicle);
                this.current_entity = base_soldier;
                break;
        }
        this.changeVehicleListener.onPlayerChangeVehicle();
    }

    private static void setSoldierSpawnPos(Soldier baseSoldier, Entity vehicle) {
        Image baseImage = vehicle.getBaseImage();
        // set player 10 pixels behind the tank
        final float DISTANCE = 10;
        final float SPAWN_X = 0;
        final float SPAWN_Y = baseImage.getHeight() / 2.f + DISTANCE;

        float xVal = (float) (Math.cos(((baseImage.getRotation()) * Math.PI) / 180) * SPAWN_X
                + -Math.sin(((baseImage.getRotation()) * Math.PI) / 180) * SPAWN_Y);
        float yVal = (float) (Math.sin(((baseImage.getRotation()) * Math.PI) / 180) * SPAWN_X
                + Math.cos(((baseImage.getRotation()) * Math.PI) / 180) * SPAWN_Y);
        baseSoldier.getPosition().x = xVal + vehicle.getPosition().x;
        baseSoldier.getPosition().y = yVal + vehicle.getPosition().y;
        // set soldiers rotation so he's facing towards the tank at its back
        baseSoldier.setRotation(vehicle.getRotation());
    }

    public MovableEntity getEntity() {
        return current_entity;
    }

    public PlayerSoldier getBaseSoldier() {
        return base_soldier;
    }

    public static void addItem(Item_e item) {
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
        HUD.notifyItemAdded(idx);
    }

    public void activateItem(Item_e item) {
        int idx = -1;
        switch (item) {
            case INVINCIBILITY:
                if (this.isInvincible) return;
                isInvincible = true;
                idx = 0;
                break;
            case EMP:   // destroy all nearby planes
                emp_use_sound.play(1.f, UserSettings.soundVolume);
                for (int idx2 = 0; idx2 < all_hostile_entities.size(); ++idx2) {
                    Entity hostileEntity = all_hostile_entities.get(idx2);
                    if (hostileEntity instanceof Aircraft) {
                        if (WayPointManager.dist(hostileEntity.getPosition(), this.current_entity.getPosition()) < 300) {
                            hostileEntity.setHealth(0);
                            hostileEntity.isDestroyed = true;
                        }
                    }
                }
                idx = 1;
                break;
            case MEGA_PULSE:
                MegaPulse mega_pulse = (MegaPulse) current_entity.getWeapon(Entity.WeaponType.MEGA_PULSE);
                if (!mega_pulse.canFire()) return;
                else mega_pulse.clearHitList();
                mega_pulse_use_sound.play(1.f, UserSettings.soundVolume);
                current_entity.fireWeapon(Entity.WeaponType.MEGA_PULSE);
                idx = 2;
                break;
            case EXPAND:
                idx = 3;
                expand_use_sound.play(1.f, UserSettings.soundVolume);
                for (int idx3 = 0; idx3 < all_entities.size(); ++idx3) {
                    Entity entity = all_entities.get(idx3);
                    if (WayPointManager.dist(entity.getPosition(), this.current_entity.getPosition()) < 500) {
                        for (Weapon weapon : entity.getWeapons()) {
                            Iterator<Projectile> projectile_iterator = weapon.getProjectileIterator();
                            while (projectile_iterator.hasNext()) {
                                Projectile projectile = projectile_iterator.next();
                                projectile.dir.x *= -1;
                                projectile.dir.y *= -1;
                            }
                        }
                    }
                }
                break;
            default:
                throw new IllegalStateException("Illegal item index [" + idx + "]!");
        }
        if (item_amounts[idx] == 0) return;
        item_amounts[idx]--;
        HUD.notifyItemActivated(idx);
    }

    /**
     * Needed for loading a game: Setup items that the player already had collected.
     */
    public static void setupItems() {
        int[] item_amounts_copy = Arrays.copyOf(item_amounts, item_amounts.length);
        for (int i = 0; i < item_amounts_copy.length; ++i) {
            while (item_amounts_copy[i] > 0) {
                switch (i) {
                    case 0:
                        Player.addItem(Player.Item_e.INVINCIBILITY);
                        break;
                    case 1:
                        Player.addItem(Player.Item_e.EMP);
                        break;
                    case 2:
                        Player.addItem(Player.Item_e.MEGA_PULSE);
                        break;
                    case 3:
                        Player.addItem(Player.Item_e.EXPAND);
                        break;
                }
                item_amounts_copy[i]--;
            }
        }
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

    public static int getPoints() {
        return points;
    }

    public static void addPoints(int amount) {
        points += amount;
    }

    public void reset() {
        Arrays.fill(item_amounts, 0);   // reset item amounts
        points = 0; // reset points
    }
}
