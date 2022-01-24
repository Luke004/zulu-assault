package game.util.saving.gameObjects;

import game.models.Element;
import game.models.entities.Entity;
import game.models.entities.MovableEntity;
import game.models.interaction_circles.InteractionCircle;
import game.models.items.Item;
import game.util.WayPointManager;
import game.util.saving.gameObjects.newGame.NewGameDataWrapper;
import game.util.saving.gameObjects.newGame.data.*;
import level_editor.LevelEditor;
import level_editor.util.EditorWaypointList;
import level_editor.util.MapElements;
import org.newdawn.slick.geom.Vector2f;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Abstract save data wrapper class. It serves as an abstract base class for both the 'NewGameDataWrapper' and the
 * 'RunningGameDataWrapper'.
 */
public abstract class ASaveDataWrapper {

    public List<ItemData> allItems;
    public List<CircleData> allCircles;
    public List<EntityData> allEntities;
    public List<WaypointEntityData> allWaypointEntities;
    public WaypointData waypointData;
    public EntityData player;

    public String levelName;
    public String mission_title, briefing_message, debriefing_message;
    public int musicIdx;

    public ASaveDataWrapper(String name,
                            List<Element> elements,
                            MovableEntity player,
                            List<EditorWaypointList> allWayPointLists,
                            Map<Double, Vector2f> entityConnections,
                            String mission_title,
                            String briefing_message,
                            String debriefing_message,
                            int musicIdx) {
        // init all objects
        allItems = new LinkedList<>();
        allCircles = new LinkedList<>();
        allEntities = new LinkedList<>();
        allWaypointEntities = new LinkedList<>();
        waypointData = new WaypointData();
        // add the data to the objects:
        this.levelName = name; // save level name
        this.saveAllElements(elements, player, allWayPointLists, entityConnections);
        this.saveWaypointData(allWayPointLists);
        this.saveMissionDescription(mission_title, briefing_message, debriefing_message, musicIdx);
        this.savePlayerData(player);
    }

    protected void saveAllElements(List<Element> elements, MovableEntity player, List<EditorWaypointList> allWayPointLists,
                                   Map<Double, Vector2f> entityConnections) {
        for (Element element : elements) {
            if (element instanceof Entity) {
                if (element.equals(player)) {
                    continue;   // don't add the player entity to the bot entity data
                }
                Entity entity = (Entity) element;
                EntityData entityData = this.setupEntityData(entity);

                if (element instanceof MovableEntity) {
                    MovableEntity movableEntity = (MovableEntity) element;
                    if (entityConnections.containsKey(movableEntity.ID)) {
                        // this movableEntity's ID is in entityConnections map - this is an entity that has waypoints!
                        WaypointEntityData waypointEntityData = new WaypointEntityData();
                        waypointEntityData.entityData = entityData;
                        // find out which waypoint list is connected to this entity
                        Vector2f startingWaypoint = entityConnections.get(movableEntity.ID);
                        for (EditorWaypointList editorWaypointList : allWayPointLists) {
                            List<Vector2f> waypoints = editorWaypointList.getWaypoints();
                            if (waypoints.contains(startingWaypoint)) {
                                waypointEntityData.waypoints = waypoints;
                                // for the waypointListStartIdx: if it is init game, use default start idx
                                // else it is a running game, so get the next target idx
                                waypointEntityData.waypointListStartIdx = this instanceof NewGameDataWrapper ?
                                        waypoints.indexOf(startingWaypoint) :
                                        movableEntity.waypointManager.getNextWayPointIdx();
                                waypointEntityData.waypointListIdx = allWayPointLists.indexOf(editorWaypointList);
                                break;
                            }
                        }
                        allWaypointEntities.add(waypointEntityData);
                        continue;   // finished adding waypoint entity
                    }
                    entityData.isDrivable = movableEntity.isDrivable;
                }
                allEntities.add(entityData);
            } else if (element instanceof InteractionCircle) {
                CircleData circleData = new CircleData();
                circleData.name = element.getClass().getSimpleName();
                circleData.xPos = element.getPosition().x;
                circleData.yPos = element.getPosition().y;
                allCircles.add(circleData);
            } else if (element instanceof Item) {
                ItemData itemData = new ItemData();
                itemData.name = element.getClass().getSimpleName();
                itemData.xPos = element.getPosition().x;
                itemData.yPos = element.getPosition().y;
                itemData.isMandatory = ((Item) element).isMandatory;
                allItems.add(itemData);
            }
        }
    }

    protected abstract EntityData setupEntityData(Entity entity);

    protected void saveWaypointData(List<EditorWaypointList> allWayPointLists) {
        List<List<Vector2f>> allWaypointLists_data = new LinkedList<>();
        for (EditorWaypointList editorWaypointList : allWayPointLists) {
            allWaypointLists_data.add(editorWaypointList.getWaypoints());
        }
        waypointData.waypoints = allWaypointLists_data;
        waypointData.entities = allWaypointEntities;
    }

    private void saveMissionDescription(String mission_title, String briefing_message, String debriefing_message,
                                        int musicIdx) {
        this.mission_title = mission_title;
        this.briefing_message = briefing_message;
        this.debriefing_message = debriefing_message;
        this.musicIdx = musicIdx;
    }

    protected abstract void savePlayerData(MovableEntity player);

    public List<Item> getAllItems() {
        List<Item> items = new LinkedList<>();
        for (ItemData itemData : allItems) {
            Element copy = MapElements.getCopyByName(new Vector2f(itemData.xPos, itemData.yPos), itemData.name);
            if (copy == null) continue;
            if (copy instanceof Item) {
                Item casted_copy = (Item) copy;
                casted_copy.isMandatory = itemData.isMandatory;
                items.add(casted_copy);
            }
        }
        return items;
    }

    public List<InteractionCircle> getAllCircles() {
        List<InteractionCircle> circles = new LinkedList<>();
        for (CircleData circleData : allCircles) {
            Element copy = MapElements.getCopyByName(new Vector2f(circleData.xPos, circleData.yPos), circleData.name);
            if (copy == null) continue;
            if (copy instanceof InteractionCircle) {
                InteractionCircle casted_copy = (InteractionCircle) copy;
                circles.add(casted_copy);
            }
        }
        return circles;
    }

    public List<Entity> getAllEntities() {
        List<Entity> entities = new LinkedList<>();
        for (EntityData entityData : allEntities) {
            Element copy = MapElements.getCopyByName(new Vector2f(entityData.xPos, entityData.yPos), entityData.name,
                    entityData.isHostile, entityData.isDrivable);
            if (copy == null) continue;
            if (copy instanceof Entity) {
                Entity casted_copy = (Entity) copy;
                casted_copy.ID = entityData.ID;
                casted_copy.setRotation(entityData.rotation);
                casted_copy.isMandatory = entityData.isMandatory;
                entities.add(casted_copy);
            }
        }
        return entities;
    }

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
                casted_copy.setRotation(waypointEntityData.entityData.rotation);
                casted_copy.isMandatory = waypointEntityData.entityData.isMandatory;
                entities.add(casted_copy);
            }
        }
        return entities;
    }

    public List<EditorWaypointList> getAllWaypointLists(LevelEditor levelEditor) {
        List<EditorWaypointList> allWaypointLists = new LinkedList<>();
        for (List<Vector2f> waypointList : waypointData.waypoints) {
            EditorWaypointList editorWaypointList = new EditorWaypointList(levelEditor, waypointList);
            editorWaypointList.setAsFinished();
            allWaypointLists.add(editorWaypointList);
        }
        return allWaypointLists;
    }

    public Map<Double, Vector2f> getEntityConnections(List<MovableEntity> movableEntities) {
        Map<Double, Vector2f> entityConnections = new HashMap<>();
        for (WaypointEntityData waypointEntity : waypointData.entities) {
            for (MovableEntity movableEntity : movableEntities) {
                if (waypointEntity.entityData.xPos == movableEntity.getPosition().x
                        && waypointEntity.entityData.yPos == movableEntity.getPosition().y) {
                    // this is the right movable entity, now get the connected waypoint
                    List<Vector2f> waypointList = waypointData.waypoints.get(waypointEntity.waypointListIdx);
                    Vector2f connectedWaypoint = waypointList.get(waypointEntity.waypointListStartIdx);
                    entityConnections.put(movableEntity.ID, connectedWaypoint);
                }
            }
        }
        return entityConnections;
    }

    public Map<MovableEntity, Vector2f> getEntityConnectionsForLevelEditor(List<MovableEntity> movableEntities) {
        Map<MovableEntity, Vector2f> entityConnections = new HashMap<>();
        for (WaypointEntityData waypointEntity : waypointData.entities) {
            for (MovableEntity movableEntity : movableEntities) {
                if (waypointEntity.entityData.xPos == movableEntity.getPosition().x
                        && waypointEntity.entityData.yPos == movableEntity.getPosition().y) {
                    // this is the right movable entity, now get the connected waypoint
                    List<Vector2f> waypointList = waypointData.waypoints.get(waypointEntity.waypointListIdx);
                    Vector2f connectedWaypoint = waypointList.get(waypointEntity.waypointListStartIdx);
                    entityConnections.put(movableEntity, connectedWaypoint);
                }
            }
        }
        return entityConnections;
    }

    public MovableEntity getPlayerEntity(boolean forMapEditor) {
        MovableEntity copy = (MovableEntity) MapElements.getCopyByName(new Vector2f(player.xPos, player.yPos), player.name,
                false, !forMapEditor);
        if (copy != null) {
            copy.setRotation(player.rotation);
            return copy;
        }
        return null;
    }

}
