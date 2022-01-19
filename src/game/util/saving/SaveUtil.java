package game.util.saving;

import com.thoughtworks.xstream.XStream;
import game.models.Element;
import game.models.entities.Entity;
import game.models.entities.MovableEntity;
import game.util.saving.data.*;
import level_editor.util.EditorWaypointList;
import org.newdawn.slick.geom.Vector2f;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class SaveUtil {

    private static final String CUSTOM_MAPS_DATA_SAVE_FOLDER = "assets/maps_custom/";
    private static final String STANDARD_MAPS_DATA_SAVE_FOLDER = "assets/maps/";
    private static XStream xstream;

    static {
        setupXStream();
    }

    public static void saveLevelDataToXML(LevelData levelData) {
        String xml = xstream.toXML(levelData);
        try (PrintWriter out = new PrintWriter(STANDARD_MAPS_DATA_SAVE_FOLDER + levelData.levelName + ".xml")) {
            out.println(xml);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static LevelData loadLevelDataFromXML(String name, boolean isStandardLevel) {
        Path fileName = Path.of((isStandardLevel ? STANDARD_MAPS_DATA_SAVE_FOLDER : CUSTOM_MAPS_DATA_SAVE_FOLDER)
                + name + ".xml");
        try {
            String xml = Files.readString(fileName);
            return (LevelData) xstream.fromXML(xml);
        } catch (IOException e) {
            System.out.println("Error: Could not read XML-file for level '" + name + "'!");
        }
        return null;
    }

    public static void saveRunningGameDataToXML(String name,
                                                List<Element> elements,
                                                Entity player,
                                                List<EditorWaypointList> allWayPointLists,
                                                Map<MovableEntity, Vector2f> entityConnections,
                                                String mission_title,
                                                String briefing_message,
                                                String debriefing_message,
                                                int musicIdx) {
        // TODO
    }

    private static void setupXStream() {
        xstream = new XStream();
        xstream.alias("entity", EntityData.class);
        xstream.alias("circle", CircleData.class);
        xstream.alias("item", ItemData.class);
        xstream.alias("waypoint", WaypointData.class);
        xstream.alias("waypointEntity", WaypointEntityData.class);
        xstream.alias("levelData", LevelData.class);
        // setup XStream permissions for used classes
        Class<?>[] classes = new Class[]{EntityData.class, CircleData.class, ItemData.class, WaypointData.class,
                WaypointEntityData.class, LevelData.class, Vector2f.class };
        xstream.allowTypes(classes);
    }

}
