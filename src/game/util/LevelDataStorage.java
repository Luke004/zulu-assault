package game.util;

import game.models.Element;
import game.models.entities.Entity;
import game.models.entities.MovableEntity;
import game.models.interaction_circles.InteractionCircle;
import game.models.items.Item;
import level_editor.util.MapElements;
import org.newdawn.slick.geom.Vector2f;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

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

    public LevelDataStorage() {
        allItems = new LinkedList<>();
        allCircles = new LinkedList<>();
        allEntities = new LinkedList<>();
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

    public void saveLevel(String name, List<Element> elements, String mission_title, String briefing_message, String debriefing_message) {
        this.levelName = name;
        // save all elements
        for (Element element : elements) {
            if (element instanceof Entity) {
                Entity entity = (Entity) element;
                EntityData entityData = new EntityData();
                entityData.name = entity.getClass().getSimpleName();
                entityData.isHostile = entity.isHostile;
                entityData.isMandatory = entity.isMandatory;
                entityData.rotation = entity.getRotation();
                entityData.xPos = entity.getPosition().x;
                entityData.yPos = entity.getPosition().y;
                if (element instanceof MovableEntity) {
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
        // save mission description
        this.mission_title = mission_title;
        this.briefing_message = briefing_message;
        this.debriefing_message = debriefing_message;
        this.musicIdx = 0;  // TODO: add user music instead of default ?

        // save player data
        // TODO: 27.10.2020 save player data 

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
            /*
            // TODO: add values
            lds.mission_title = "";
            lds.briefing_message = "Default briefing message";
            lds.debriefing_message = "Default debriefing message";
            lds.musicIdx = 0;
            */

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

}
