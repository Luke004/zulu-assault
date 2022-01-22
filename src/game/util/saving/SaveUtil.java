package game.util.saving;

import com.thoughtworks.xstream.XStream;
import game.models.weapons.projectiles.Projectile;
import game.util.saving.gameObjects.EntityData;
import game.util.saving.gameObjects.newGame.data.*;
import game.util.saving.gameObjects.newGame.NewGameDataWrapper;
import game.util.saving.gameObjects.runningGame.RunningGameDataWrapper;
import game.util.saving.mapLayers.MyTileMap;
import game.util.saving.gameObjects.runningGame.data.REntityData;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.tiled.Layer;
import org.newdawn.slick.tiled.TileSet;
import org.newdawn.slick.tiled.TiledMap;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class SaveUtil {

    private static final String CUSTOM_MAPS_DATA_SAVE_FOLDER = "assets/maps_custom/";
    private static final String STANDARD_MAPS_DATA_SAVE_FOLDER = "assets/maps/";
    private static final String RUNNING_GAMES_DATA_SAVE_FOLDER = "saves/games/";
    private static XStream xstream;

    static {
        setupXStream();
    }

    public static void saveInitGameDataToXML(NewGameDataWrapper levelData) {
        String xml = xstream.toXML(levelData);
        try (PrintWriter out = new PrintWriter(STANDARD_MAPS_DATA_SAVE_FOLDER + levelData.levelName + ".xml")) {
            out.println(xml);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static NewGameDataWrapper loadInitGameDataFromXML(String name, boolean isOfficialLevel) {
        Path fileName = Path.of((isOfficialLevel ? STANDARD_MAPS_DATA_SAVE_FOLDER : CUSTOM_MAPS_DATA_SAVE_FOLDER)
                + name + ".xml");
        try {
            String xml = Files.readString(fileName);
            return (NewGameDataWrapper) xstream.fromXML(xml);
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

    public static void saveTMXMapData(ArrayList<Layer> mapLayers) {
        String xml = xstream.toXML(mapLayers);
        try (PrintWriter out = new PrintWriter(RUNNING_GAMES_DATA_SAVE_FOLDER + "testData" + ".xml")) {
            out.println(xml);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Layer> loadTMXMapData(String name) {
        Path fileName = Path.of(RUNNING_GAMES_DATA_SAVE_FOLDER + "testData" + ".xml");
        try {
            String xml = Files.readString(fileName);
            return (ArrayList<Layer>) xstream.fromXML(xml);
        } catch (IOException e) {
            System.out.println("Error: Could not read map-file for level '" + "testData" + "'!");
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
        xstream.alias("initGameData", NewGameDataWrapper.class);
        xstream.alias("runningGameData", NewGameDataWrapper.class);
        xstream.alias("mapLayers", ArrayList.class);
        // setup XStream permissions for used classes
        Class<?>[] classes = new Class[]{IEntityData.class, REntityData.class, Projectile.class, CircleData.class,
                ItemData.class, WaypointData.class, WaypointEntityData.class, NewGameDataWrapper.class, Vector2f.class,
                EntityData.class, RunningGameDataWrapper.class,
                TiledMap.class, TileSet.class, TextureImpl.class, Image.class, Layer.class, MyTileMap.class};
        xstream.allowTypes(classes);
    }

}
