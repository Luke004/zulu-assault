package save_game;

import models.Element;
import models.entities.Entity;
import models.entities.MovableEntity;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/* This class holds all relevant data for loading and saving an element */
public class GameSaver implements Serializable {

    private List<ElementData> allElements;
    private List<EntityData> allEntities;

    public GameSaver() {
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

    public void saveLevel(String name, List<Element> elements) {
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

        try {
            FileOutputStream fileOutputStream
                    = new FileOutputStream("saves/levels/" + name + ".ser");
            ObjectOutputStream objectOutputStream
                    = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this);
            objectOutputStream.flush();
            objectOutputStream.close();
            System.out.println("successfully saved level");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadLevel(String name) {

        try {
            FileInputStream fileInputStream
                    = new FileInputStream("saves/levels/" + name + ".ser");
            ObjectInputStream objectInputStream
                    = new ObjectInputStream(fileInputStream);
            GameSaver p2 = (GameSaver) objectInputStream.readObject();
            objectInputStream.close();

            System.out.println("successfully loaded map!");

        } catch (IOException | ClassNotFoundException i) {
            i.printStackTrace();
        }

    }

}
