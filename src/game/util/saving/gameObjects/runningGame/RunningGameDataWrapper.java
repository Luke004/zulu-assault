package game.util.saving.gameObjects.runningGame;


import game.levels.LevelManager;
import game.models.Element;
import game.models.entities.Entity;
import game.models.entities.MovableEntity;
import game.models.weapons.Weapon;
import game.models.weapons.projectiles.Projectile;
import game.player.Player;
import game.util.TimeManager;
import game.util.WayPointManager;
import game.util.saving.gameObjects.ASaveDataWrapper;
import game.util.saving.gameObjects.EntityData;
import game.util.saving.gameObjects.newGame.data.WaypointEntityData;
import game.util.saving.gameObjects.runningGame.data.ProjectileData;
import game.util.saving.gameObjects.runningGame.data.REntityData;
import level_editor.util.EditorWaypointList;
import level_editor.util.MapElements;
import org.newdawn.slick.geom.Vector2f;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Wraps all the data that is needed to load a running game.
 * It is converted to an XML-file to later load the data from.
 */
public class RunningGameDataWrapper extends ASaveDataWrapper {

    // extra data needed for games that are already running:
    public boolean isInPlayThrough;             // is the player in a play through or is it just a single game
    public long currTime, totalTime;            // current and total time of player in game
    public int currentScore;                    // current score of the player
    public int[] item_amounts;                  // current items that the player has

    public RunningGameDataWrapper(String name,
                                  List<Element> elements,
                                  MovableEntity player,
                                  List<EditorWaypointList> allWayPointLists,
                                  Map<Double, Vector2f> entityConnections,
                                  String mission_title,
                                  String briefing_message,
                                  String debriefing_message,
                                  int musicIdx) {
        super(name, elements, player, allWayPointLists, entityConnections, mission_title, briefing_message,
                debriefing_message, musicIdx);
        this.currentScore = Player.getPoints();
        this.isInPlayThrough = LevelManager.playerIsInPlayThrough();
        this.currTime = TimeManager.getTimeInLevel();
        if (this.isInPlayThrough) this.totalTime = TimeManager.getTotalTime();
        else this.totalTime = 0L;
        this.item_amounts = Player.item_amounts;
    }

    @Override
    protected EntityData setupEntityData(Entity entity) {
        REntityData entityData = new REntityData();
        entityData.ID = entity.ID;
        entityData.name = entity.getClass().getSimpleName();
        entityData.currentHealth = entity.getHealth();
        entityData.projectileMap = this.getProjectileMap(entity);
        entityData.isHostile = entity.isHostile;
        entityData.isMandatory = entity.isMandatory;
        entityData.rotation = entity.getRotation();
        entityData.xPos = entity.getPosition().x;
        entityData.yPos = entity.getPosition().y;
        return entityData;
    }

    @Override
    protected void savePlayerData(MovableEntity player) {
        REntityData playerData = new REntityData();
        playerData.ID = player.ID;
        playerData.name = player.getClass().getSimpleName();
        playerData.xPos = player.getPosition().x;
        playerData.yPos = player.getPosition().y;
        playerData.isDrivable = true;
        playerData.isHostile = false;
        playerData.isMandatory = false;
        playerData.rotation = player.getRotation();
        playerData.projectileMap = this.getProjectileMap(player);
        playerData.currentHealth = player.getHealth();
        this.player = playerData;
    }

    /**
     * Create a map of <WeaponType, List<Projectiles>>
     *
     * @param entity the game entity that shot the projectile
     * @return the projectile map
     */
    private Map<String, List<ProjectileData>> getProjectileMap(Entity entity) {
        boolean hasProjectiles = false;
        for (Weapon weapon : entity.getWeapons()) {
            if (!weapon.getProjectileList().isEmpty()) {
                hasProjectiles = true;
                break;
            }
        }
        if (!hasProjectiles) return null;
        // player currently has fired projectiles flying
        Map<String, List<ProjectileData>> projectileMap = new HashMap<>();
        for (Weapon weapon : entity.getWeapons()) {
            if (weapon.getProjectileList().isEmpty()) continue;
            List<ProjectileData> projectiles = new LinkedList<>();
            for (Projectile projectile : weapon.getProjectileList()) {
                projectiles.add(new ProjectileData(
                        projectile.pos,
                        projectile.dir,
                        projectile.lifetime,
                        projectile.image.getRotation()
                ));
            }
            projectileMap.put(weapon.getClass().getSimpleName(), projectiles);
        }
        return projectileMap;
    }

    @Override
    public MovableEntity getPlayerEntity(boolean forMapEditor) {
        MovableEntity playerEntity = super.getPlayerEntity(forMapEditor);
        REntityData player = (REntityData) this.player;
        playerEntity.setHealth(player.currentHealth);
        this.loadProjectileList(player, playerEntity);
        return playerEntity;
    }

    @Override
    public List<Entity> getAllEntities() {
        List<Entity> entities = new LinkedList<>();
        for (EntityData entityData : allEntities) {
            Element copy = MapElements.getCopyByName(new Vector2f(entityData.xPos, entityData.yPos), entityData.name,
                    entityData.isHostile, entityData.isDrivable);
            if (copy == null) continue;
            if (copy instanceof Entity) {
                REntityData rEntityData = (REntityData) entityData;
                Entity casted_copy = (Entity) copy;
                casted_copy.ID = entityData.ID;
                casted_copy.setHealth(rEntityData.currentHealth);
                this.loadProjectileList(rEntityData, casted_copy);
                casted_copy.setRotation(entityData.rotation);
                casted_copy.isMandatory = entityData.isMandatory;
                entities.add(casted_copy);
            }
        }
        return entities;
    }

    private void loadProjectileList(REntityData rEntityData, Entity entity) {
        Map<String, List<ProjectileData>> projectileMap = rEntityData.projectileMap;
        if (projectileMap != null) {
            for (Weapon weapon : entity.getWeapons()) {
                List<ProjectileData> projectiles = projectileMap.get(weapon.getClass().getSimpleName());
                if (projectiles != null) {
                    for (ProjectileData projectileData : projectiles) {
                        weapon.loadProjectile(
                                projectileData.pos,
                                projectileData.dir,
                                projectileData.rotation,
                                projectileData.currentLifetime
                        );
                    }
                }
            }
        }
    }

    @Override
    public List<MovableEntity> getAllWaypointEntities() {
        List<MovableEntity> entities = new LinkedList<>();
        for (WaypointEntityData waypointEntityData : allWaypointEntities) {
            Element copy = MapElements.getCopyByName(
                    new Vector2f(waypointEntityData.entityData.xPos, waypointEntityData.entityData.yPos),
                    waypointEntityData.entityData.name,
                    waypointEntityData.entityData.isHostile,
                    waypointEntityData.entityData.isDrivable);
            if (copy == null) continue;
            if (copy instanceof MovableEntity) {
                MovableEntity casted_copy = (MovableEntity) copy;
                casted_copy.addWayPoints(new WayPointManager(casted_copy.getPosition(), casted_copy.getRotation(),
                        waypointEntityData.waypoints, waypointEntityData.waypointListStartIdx));
                casted_copy.ID = waypointEntityData.entityData.ID;
                REntityData rEntityData = (REntityData) waypointEntityData.entityData;
                casted_copy.setRotation(rEntityData.rotation);
                casted_copy.isMandatory = rEntityData.isMandatory;
                casted_copy.setHealth(rEntityData.currentHealth);
                this.loadProjectileList(rEntityData, casted_copy);
                entities.add(casted_copy);
            }
        }
        return entities;
    }

}
