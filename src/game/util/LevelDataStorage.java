package game.util;

import game.models.Element;
import game.models.entities.Entity;
import game.models.entities.MovableEntity;
import game.models.interaction_circles.InteractionCircle;
import game.models.items.Item;
import level_editor.LevelEditor;
import level_editor.util.EditorWaypointList;
import level_editor.util.MapElements;
import org.newdawn.slick.geom.Vector2f;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/* This class stores all relevant data for loading and saving a level */
public class LevelDataStorage implements Serializable {

    private static final String CUSTOM_MAPS_DATA_SAVE_FOLDER = "saves/custom_maps_data/";
    private static final String STANDARD_MAPS_DATA_SAVE_FOLDER = "assets/maps/";

    public String levelName;
    public String mission_title, briefing_message, debriefing_message;
    public int musicIdx;

    public List<ItemData> allItems;
    public List<CircleData> allCircles;
    public List<EntityData> allEntities;
    public List<WaypointEntityData> allWaypointEntities;
    public WaypointData waypointData;
    public EntityData player;

    public LevelDataStorage() {
        allItems = new LinkedList<>();
        allCircles = new LinkedList<>();
        allEntities = new LinkedList<>();
        allWaypointEntities = new LinkedList<>();
        waypointData = new WaypointData();
    }

    static class ItemData implements Serializable {
        private String name;
        float xPos, yPos;
        boolean isMandatory;
    }

    static class CircleData implements Serializable {
        private String name;
        float xPos, yPos;
    }

    static class EntityData implements Serializable {
        String name;
        float xPos, yPos;
        float rotation;
        boolean isHostile, isDrivable, isMandatory;
    }

    static class WaypointEntityData implements Serializable {
        EntityData entityData;
        List<Vector2f> waypoints;
        int waypointListIdx;
        int waypointListStartIdx;
    }

    static class WaypointData implements Serializable {
        List<List<Vector2f>> waypoints;
        List<WaypointEntityData> entities;
    }

    public void saveLevel(String name,
                          List<Element> elements,
                          Entity player,
                          List<EditorWaypointList> allWayPointLists,
                          Map<MovableEntity, Vector2f> entityConnections,
                          String mission_title,
                          String briefing_message,
                          String debriefing_message,
                          int musicIdx) {
        this.levelName = name;
        // save all elements
        for (Element element : elements) {
            if (element instanceof Entity) {
                if (element.equals(player)) {
                    continue;   // don't add the player entity to the bot entity data
                }
                Entity entity = (Entity) element;
                EntityData entityData = new EntityData();
                entityData.name = entity.getClass().getSimpleName();
                entityData.isHostile = entity.isHostile;
                entityData.isMandatory = entity.isMandatory;
                entityData.rotation = entity.getRotation();
                entityData.xPos = entity.getPosition().x;
                entityData.yPos = entity.getPosition().y;

                if (element instanceof MovableEntity) {
                    if (entityConnections.containsKey(element)) {   // check if its a entity that has waypoints
                        WaypointEntityData waypointEntityData = new WaypointEntityData();
                        waypointEntityData.entityData = entityData;
                        // find out which waypoint list is connected to this entity
                        for (Map.Entry<MovableEntity, Vector2f> entry : entityConnections.entrySet()) {
                            MovableEntity movableEntity = entry.getKey();
                            if (movableEntity.equals(element)) {
                                Vector2f startingWaypoint = entry.getValue();
                                for (EditorWaypointList editorWaypointList : allWayPointLists) {
                                    List<Vector2f> waypoints = editorWaypointList.getWaypoints();
                                    if (waypoints.contains(startingWaypoint)) {
                                        waypointEntityData.waypoints = waypoints;
                                        waypointEntityData.waypointListStartIdx = waypoints.indexOf(startingWaypoint);
                                        waypointEntityData.waypointListIdx = allWayPointLists.indexOf(editorWaypointList);
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        allWaypointEntities.add(waypointEntityData);
                        continue;   // finished adding waypoint entity
                    }
                    MovableEntity movableEntity = (MovableEntity) element;
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
        // save waypoint data
        List<List<Vector2f>> allWaypointLists_data = new LinkedList<>();
        for (EditorWaypointList editorWaypointList : allWayPointLists) {
            allWaypointLists_data.add(editorWaypointList.getWaypoints());
        }
        waypointData.waypoints = allWaypointLists_data;
        waypointData.entities = allWaypointEntities;

        // save mission description
        this.mission_title = mission_title;
        this.briefing_message = briefing_message;
        this.debriefing_message = debriefing_message;
        this.musicIdx = musicIdx;

        // save player data
        EntityData playerData = new EntityData();
        playerData.name = player.getClass().getSimpleName();
        playerData.xPos = player.getPosition().x;
        playerData.yPos = player.getPosition().y;
        playerData.isDrivable = true;
        playerData.isHostile = false;
        playerData.isMandatory = false;
        playerData.rotation = player.getRotation();
        this.player = playerData;

        try {
            FileOutputStream fileOutputStream
                    = new FileOutputStream(CUSTOM_MAPS_DATA_SAVE_FOLDER + name + ".data");
            ObjectOutputStream objectOutputStream
                    = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static LevelDataStorage loadLevel(String name, boolean isStandardLevel) {
        try {
            FileInputStream fileInputStream
                    = new FileInputStream((isStandardLevel ? STANDARD_MAPS_DATA_SAVE_FOLDER : CUSTOM_MAPS_DATA_SAVE_FOLDER) + name + ".data");
            ObjectInputStream objectInputStream
                    = new ObjectInputStream(fileInputStream);
            LevelDataStorage lds = (LevelDataStorage) objectInputStream.readObject();
            objectInputStream.close();
            return lds;
        } catch (IOException | ClassNotFoundException i) {
            //i.printStackTrace();
            return null;
        }
    }

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

    public Map<MovableEntity, Vector2f> getEntityConnections(List<MovableEntity> movableEntities) {
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
