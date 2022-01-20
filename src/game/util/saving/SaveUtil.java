package game.util.saving;

import com.thoughtworks.xstream.XStream;
import game.models.Element;
import game.models.entities.Entity;
import game.models.entities.MovableEntity;
import game.models.weapons.projectiles.Projectile;
import game.util.saving.data.EntityData;
import game.util.saving.init.data.*;
import game.util.saving.init.InitGameDataWrapper;
import game.util.saving.running.RunningGameDataWrapper;
import game.util.saving.running.data.REntityData;
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
    private static final String RUNNING_GAMES_DATA_SAVE_FOLDER = "saves/games/";
    private static XStream xstream;

    static {
        setupXStream();
    }

    public static void saveInitGameDataToXML(InitGameDataWrapper levelData) {
        String xml = xstream.toXML(levelData);
        try (PrintWriter out = new PrintWriter(STANDARD_MAPS_DATA_SAVE_FOLDER + levelData.levelName + ".xml")) {
            out.println(xml);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static InitGameDataWrapper loadInitGameDataFromXML(String name, boolean isOfficialLevel) {
        Path fileName = Path.of((isOfficialLevel ? STANDARD_MAPS_DATA_SAVE_FOLDER : CUSTOM_MAPS_DATA_SAVE_FOLDER)
                + name + ".xml");
        try {
            String xml = Files.readString(fileName);
            return (InitGameDataWrapper) xstream.fromXML(xml);
        } catch (IOException e) {
            System.out.println("Error: Could not read XML-file for level '" + name + "'!");
        }
        return null;
    }

    public static void saveRunningGameDataToXML(RunningGameDataWrapper runningGameData) {
        File properties_file = new File(RUNNING_GAMES_DATA_SAVE_FOLDER);
        properties_file.mkdir();

        String xml = xstream.toXML(runningGameData);
        try (PrintWriter out = new PrintWriter(RUNNING_GAMES_DATA_SAVE_FOLDER + runningGameData.levelName + ".xml")) {
            out.println(xml);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static RunningGameDataWrapper loadRunningGameDataFromXML(String name) {
        Path fileName = Path.of(RUNNING_GAMES_DATA_SAVE_FOLDER + name + ".xml");
        try {
            String xml = Files.readString(fileName);
            return (RunningGameDataWrapper) xstream.fromXML(xml);
        } catch (IOException e) {
            System.out.println("Error: Could not read XML-file for level '" + name + "'!");
        }
        return null;
    }

    private static void setupXStream() {
        xstream = new XStream();
        xstream.alias("entity", EntityData.class);
        xstream.alias("circle", CircleData.class);
        xstream.alias("item", ItemData.class);
        xstream.alias("waypoint", WaypointData.class);
        xstream.alias("waypointEntity", WaypointEntityData.class);
        xstream.alias("initGameData", InitGameDataWrapper.class);
        xstream.alias("runningGameData", InitGameDataWrapper.class);
        // setup XStream permissions for used classes
        Class<?>[] classes = new Class[]{IEntityData.class, REntityData.class, Projectile.class, CircleData.class,
                ItemData.class, WaypointData.class, WaypointEntityData.class, InitGameDataWrapper.class, Vector2f.class,
                EntityData.class, RunningGameDataWrapper.class };
        xstream.allowTypes(classes);
    }

}
