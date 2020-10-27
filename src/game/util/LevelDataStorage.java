package game.util;

import game.models.Element;
import game.models.entities.Entity;
import game.models.entities.MovableEntity;
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

    public List<ElementData> allElements;
    public List<EntityData> allEntities;

    public LevelDataStorage() {
        allElements = new LinkedList<>();
        allEntities = new LinkedList<>();
    }

    static class ElementData implements Serializable {
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
            } else {
                ElementData elementData = new ElementData();
                elementData.name = element.getClass().getSimpleName();
                elementData.xPos = element.getPosition().x;
                elementData.yPos = element.getPosition().y;
                allElements.add(elementData);
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

    public List<Element> getAllElements() {
        List<Element> elements = new LinkedList<>();
        for (ElementData elementData : allElements) {
            Element copy = MapElements.getCopyByName(elementData.name);
            copy.setPosition(new Vector2f(elementData.xPos, elementData.yPos));
            elements.add(copy);
        }
        return elements;
    }

    public List<Entity> getAllEntities() {
        List<Entity> entities = new LinkedList<>();
        for (EntityData entityData : allEntities) {
            Element copy = MapElements.getCopyByName(entityData.name, entityData.isHostile, entityData.isDrivable);
            if (copy == null) continue;
            if (copy instanceof Entity) {
                Entity casted_copy = (Entity) copy;
                casted_copy.setPosition(new Vector2f(entityData.xPos, entityData.yPos));
                casted_copy.setRotation(entityData.rotation);
                casted_copy.isMandatory = entityData.isMandatory;
                entities.add(casted_copy);
            }
        }
        return entities;
    }

}
