package game.util.saving;

import com.thoughtworks.xstream.XStream;
import game.models.weapons.projectiles.Projectile;
import game.util.saving.gameObjects.EntityData;
import game.util.saving.gameObjects.newGame.data.*;
import game.util.saving.gameObjects.newGame.NewGameDataWrapper;
import game.util.saving.gameObjects.runningGame.RunningGameDataWrapper;
import game.util.saving.gameObjects.runningGame.data.ProjectileData;
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
    public static final String RUNNING_GAMES_DATA_SAVE_FOLDER = "saves/games/";
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

    public static void saveRunningGameDataToXML(RunningGameDataWrapper runningGameData, int saveIdx) {
        File saveFolderDir = new File(RUNNING_GAMES_DATA_SAVE_FOLDER);
        saveFolderDir.mkdir(); // create save folder dir ("/saves/games")
        final String saveGameDir = RUNNING_GAMES_DATA_SAVE_FOLDER + saveIdx;
        File idxFolderDir = new File(saveGameDir);
        idxFolderDir.mkdir(); // create save game folder dir ("/saves/games/[saveIdx]")
        String xml = xstream.toXML(runningGameData);
        try (PrintWriter out = new PrintWriter(saveGameDir + File.separator + runningGameData.levelName + ".xml")) {
            out.println(xml);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static RunningGameDataWrapper loadRunningGameDataFromXML(String name, int idx) {
        Path fileName = Path.of(RUNNING_GAMES_DATA_SAVE_FOLDER + File.separator + idx
                + File.separator + name + ".xml");
        try {
            String xml = Files.readString(fileName);
            return (RunningGameDataWrapper) xstream.fromXML(xml);
        } catch (IOException e) {
            System.out.println("Error: Could not read XML-file for level '" + name + "'!");
        }
        return null;
    }

    public static void saveTMXMapData(ArrayList<Layer> mapLayers, String name, int saveIdx) {
        String xml = xstream.toXML(mapLayers);
        try (PrintWriter out = new PrintWriter(RUNNING_GAMES_DATA_SAVE_FOLDER + saveIdx
                + File.separator + name + "_layers.xml")) {
            out.println(xml);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Layer> loadTMXMapData(String name) {
        Path fileName = Path.of(RUNNING_GAMES_DATA_SAVE_FOLDER + RunningGameDataWrapper.levelIdxToLoad
                + File.separator + name + "_layers.xml");
        try {
            String xml = Files.readString(fileName);
            return (ArrayList<Layer>) xstream.fromXML(xml);
        } catch (IOException e) {
            System.out.println("Error: Could not read map-file for level '" + name + "'!");
        }
        return null;
    }

    private static void setupXStream() {
        xstream = new XStream();
        xstream.alias("nEntity", NEntityData.class);
        xstream.alias("rEntity", REntityData.class);
        xstream.alias("circle", CircleData.class);
        xstream.alias("item", ItemData.class);
        xstream.alias("waypoint", WaypointData.class);
        xstream.alias("waypointEntity", WaypointEntityData.class);
        xstream.alias("projectileData", ProjectileData.class);
        xstream.alias("initGameData", NewGameDataWrapper.class);
        xstream.alias("runningGameData", NewGameDataWrapper.class);
        xstream.alias("mapLayers", ArrayList.class);
        // setup XStream permissions for used classes
        Class<?>[] classes = new Class[]{NEntityData.class, REntityData.class, Projectile.class, CircleData.class,
                ItemData.class, WaypointData.class, WaypointEntityData.class, NewGameDataWrapper.class, Vector2f.class,
                EntityData.class, RunningGameDataWrapper.class, ProjectileData.class,
                TiledMap.class, TileSet.class, TextureImpl.class, Image.class, Layer.class, MyTileMap.class};
        xstream.allowTypes(classes);
    }

}
