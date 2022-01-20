package game.util.saving.running;


import game.levels.LevelManager;
import game.models.Element;
import game.models.entities.Entity;
import game.models.entities.MovableEntity;
import game.models.items.Item;
import game.models.weapons.projectiles.Projectile;
import game.player.Player;
import game.util.TimeManager;
import game.util.WayPointManager;
import game.util.saving.data.ASaveDataWrapper;
import game.util.saving.data.EntityData;
import game.util.saving.init.data.WaypointEntityData;
import game.util.saving.running.data.REntityData;
import level_editor.util.EditorWaypointList;
import level_editor.util.MapElements;
import org.newdawn.slick.geom.Vector2f;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * All the data that is needed to save and load a running game is stored here.
 */
public class RunningGameDataWrapper extends ASaveDataWrapper {

    // extra data needed for games that are already running:
    public boolean isInPlayThrough;             // is the player in a play through or is it just a single game
    public long currTime, totalTime;            // current and total time of player in game
    public int currentScore;                    // current score of the player
    public int[] item_amounts;                  // current items that the player has
    public List<Projectile> projectileList;     // current flying projectiles

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
        playerData.currentHealth = player.getHealth();
        this.player = playerData;
    }

    @Override
    public MovableEntity getPlayerEntity(boolean forMapEditor) {
        MovableEntity playerEntity = super.getPlayerEntity(forMapEditor);
        REntityData player = (REntityData) this.player;
        playerEntity.setHealth(player.currentHealth);
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
                casted_copy.setRotation(entityData.rotation);
                casted_copy.isMandatory = entityData.isMandatory;
                entities.add(casted_copy);
            }
        }
        return entities;
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
                REntityData movableWaypointEntity = (REntityData)waypointEntityData.entityData;
                casted_copy.setRotation(movableWaypointEntity.rotation);
                casted_copy.isMandatory = movableWaypointEntity.isMandatory;
                casted_copy.setHealth(movableWaypointEntity.currentHealth);
                entities.add(casted_copy);
            }
        }
        return entities;
    }

}
