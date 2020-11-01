package level_editor;

import game.audio.MenuSounds;
import game.graphics.fonts.FontManager;
import game.logic.Camera;
import game.models.CollisionModel;
import game.models.Element;
import game.models.entities.Entity;
import game.models.entities.MovableEntity;
import game.models.entities.aircraft.Aircraft;
import game.models.entities.aircraft.WarPlane;
import game.models.entities.robots.PlasmaRobot;
import game.models.entities.soldiers.UziSoldier;
import game.models.entities.tanks.*;
import game.models.interaction_circles.HealthCircle;
import game.models.items.GoldenWrenchItem;
import game.models.items.InvincibilityItem;
import game.models.items.Item;
import game.models.items.SilverWrenchItem;
import game.util.LevelDataStorage;
import game.util.WayPointManager;
import level_editor.screens.windows.CenterPopupWindow;
import level_editor.screens.windows.Window;
import level_editor.screens.windows.toolbars.bottom.BottomToolbar;
import level_editor.screens.windows.toolbars.right.RightToolbar;
import level_editor.screens.windows.toolbars.right.screens.EntityAdder;
import level_editor.screens.windows.toolbars.right.screens.WaypointAdder;
import level_editor.util.EditorWaypointList;
import level_editor.util.MapElements;
import main.ZuluAssault;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.tiled.TiledMap;
import settings.TileMapData;
import settings.UserSettings;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.*;

public class LevelEditor extends BasicGameState {

    // for choosing the map
    public static final String CUSTOM_MAPS_FOLDER = "custom_maps/";

    private String mapName;

    // elements
    private List<Element> elements;
    private Element elementToPlace, elementToModify;
    private EditorWaypointList currentWaypointList;
    private List<EditorWaypointList> allWayPointLists;
    private boolean isPlacingWaypoints;

    // player entity
    private Element playerEntity;
    private static final Color opacityBlue = new Color(16, 133, 199, 80);
    private int playerOvalX, playerOvalY, playerOvalSize;

    private static final String title_string;
    private static TrueTypeFont title_string_drawer;

    // helper vars
    private static final int TITLE_RECT_HEIGHT = 40;    // absolute height of the title rect

    // map coords
    private int screenMouseX, screenMouseY;
    private float mapX, mapY;
    private int mapWidth, mapHeight;
    private int prevMouseX, prevMouseY, mouseSlideX, mouseSlideY;   // to slide the map using mouse's right key
    private boolean allowMouseSlide, isMouseInEditor;

    private static final float MOVE_SPEED = 0.3f;

    // all windows of the level editor
    private RightToolbar rightToolbar;
    private BottomToolbar bottomToolbar;
    private CenterPopupWindow popupWindow;

    // for selecting elements
    private CollisionModel mouseCollisionModel;
    private Vector2f mapMousePosition;

    private Camera camera;

    static {
        title_string = "LEVEL EDITOR";
    }

    public LevelEditor() {
        elements = new ArrayList<>();
        //currentWaypointList = new EditorWaypointList(this);
        allWayPointLists = new LinkedList<>();
        mapMousePosition = new Vector2f();
        mouseCollisionModel = new CollisionModel(mapMousePosition, 4, 4);
    }

    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        Window.Props.initMargin(gc.getWidth());
        title_string_drawer = FontManager.getStencilBigFont();
        rightToolbar = new RightToolbar(this, TITLE_RECT_HEIGHT + 1, gc);
        bottomToolbar = new BottomToolbar(this, rightToolbar, gc, sbg);
    }

    @Override
    public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
        gc.setFullscreen(false);
        this.elements.clear();
        // prompt the user to select a map file
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Select Level File");
        fc.setCurrentDirectory(new File(CUSTOM_MAPS_FOLDER));
        fc.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".tmx");
            }

            @Override
            public String getDescription() {
                return ".tmx (TileMap created using Tiled)";
            }
        });
        fc.setAcceptAllFileFilterUsed(false);

        int returnValue = fc.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fc.getSelectedFile();
            this.mapName = selectedFile.getName();
            TiledMap map = new TiledMap(CUSTOM_MAPS_FOLDER + mapName, TileMapData.TILESETS_LOCATION);
            camera = new Camera(gc, map);
            mapWidth = map.getWidth() * map.getTileWidth();
            mapHeight = map.getHeight() * map.getTileHeight();
            mapX = mapWidth / 2.f;
            mapY = mapHeight / 2.f;

            // attempt to load already created map data
            LevelDataStorage lds = LevelDataStorage.loadLevel(getSimpleMapName(), false);

            //if (lds != null) {
                /*
                setPlayerEntity(lds.getPlayerEntity(true));
                this.elements.add(playerEntity);
                this.elements.addAll(lds.getAllItems());
                this.elements.addAll(lds.getAllCircles());
                this.elements.addAll(lds.getAllEntities());
                List<MovableEntity> waypointEntities = lds.getAllWaypointEntities();
                this.elements.addAll(waypointEntities);
                EditorWaypointList.setEntityConnections(lds.getEntityConnections(waypointEntities));
                allWayPointLists.addAll(lds.getAllWaypointLists(this));
                // fill in the save-text-fields with the 'title', 'briefing' and 'debriefing' message
                this.bottomToolbar.fillSaveTextFields(lds.mission_title, lds.briefing_message, lds.debriefing_message);


                // FOR MAP REBUILD:

                // ADD PLAYER
                Vector2f playerStartPos = new Vector2f(750, 800);
                CannonTank tank = new CannonTank(playerStartPos, false, true);
                tank.setRotation(190);
                setPlayerEntity(tank);
                this.elements.add(playerEntity);



                // ---------- SETUP ITEMS ----------

                // silver wrenches
                Item silver_wrench_1 = new SilverWrenchItem(new Vector2f(160.f, 100.f));
                this.elements.add(silver_wrench_1);

                Item silver_wrench_2 = new SilverWrenchItem(new Vector2f(220.f, 100.f));
                this.elements.add(silver_wrench_2);

                Item silver_wrench_3 = new SilverWrenchItem(new Vector2f(227.f, 45.f));
                this.elements.add(silver_wrench_3);

                Item silver_wrench_4 = new SilverWrenchItem(new Vector2f(158.f, 45.f));
                this.elements.add(silver_wrench_4);

                Item silver_wrench_5 = new SilverWrenchItem(new Vector2f(1215, 2315));
                this.elements.add(silver_wrench_5);

                Item silver_wrench_6 = new SilverWrenchItem(new Vector2f(1220, 2410));
                this.elements.add(silver_wrench_6);

                Item silver_wrench_7 = new SilverWrenchItem(new Vector2f(1222, 2610));
                this.elements.add(silver_wrench_7);

                Item silver_wrench_8 = new SilverWrenchItem(new Vector2f(1220, 2680));
                this.elements.add(silver_wrench_8);

                Item silver_wrench_9 = new SilverWrenchItem(new Vector2f(25, 3720));
                this.elements.add(silver_wrench_9);

                Item silver_wrench_10 = new SilverWrenchItem(new Vector2f(100, 3780));
                this.elements.add(silver_wrench_10);

                Item silver_wrench_11 = new SilverWrenchItem(new Vector2f(185, 3840));
                this.elements.add(silver_wrench_11);

                Item silver_wrench_12 = new SilverWrenchItem(new Vector2f(190, 3940));
                this.elements.add(silver_wrench_12);

                Item silver_wrench_13 = new SilverWrenchItem(new Vector2f(3278, 3760));
                this.elements.add(silver_wrench_13);

                Item silver_wrench_14 = new SilverWrenchItem(new Vector2f(3280, 3905));
                this.elements.add(silver_wrench_14);

                Item silver_wrench_15 = new SilverWrenchItem(new Vector2f(1250, 14));
                this.elements.add(silver_wrench_15);

                Item silver_wrench_16 = new SilverWrenchItem(new Vector2f(1610, 16));
                this.elements.add(silver_wrench_16);

                Item silver_wrench_17 = new SilverWrenchItem(new Vector2f(1625, 110));
                this.elements.add(silver_wrench_17);

                Item silver_wrench_18 = new SilverWrenchItem(new Vector2f(1845, 595));
                this.elements.add(silver_wrench_18);

                Item silver_wrench_19 = new SilverWrenchItem(new Vector2f(2278, 735));
                this.elements.add(silver_wrench_19);

                Item silver_wrench_20 = new SilverWrenchItem(new Vector2f(2172, 185));
                this.elements.add(silver_wrench_20);

                Item silver_wrench_21 = new SilverWrenchItem(new Vector2f(2145, 85));
                this.elements.add(silver_wrench_21);

                Item silver_wrench_22 = new SilverWrenchItem(new Vector2f(2090, 25));
                this.elements.add(silver_wrench_22);

                Item silver_wrench_23 = new SilverWrenchItem(new Vector2f(3725, 400));
                this.elements.add(silver_wrench_23);

                Item silver_wrench_24 = new SilverWrenchItem(new Vector2f(3775, 475));
                this.elements.add(silver_wrench_24);

                Item silver_wrench_25 = new SilverWrenchItem(new Vector2f(3625, 900));
                this.elements.add(silver_wrench_25);

                Item silver_wrench_26 = new SilverWrenchItem(new Vector2f(3577, 1225));
                this.elements.add(silver_wrench_26);

                Item silver_wrench_27 = new SilverWrenchItem(new Vector2f(3645, 1320));
                this.elements.add(silver_wrench_27);

                Item silver_wrench_28 = new SilverWrenchItem(new Vector2f(3340, 2040));
                this.elements.add(silver_wrench_28);

                Item silver_wrench_29 = new SilverWrenchItem(new Vector2f(3475, 2040));
                this.elements.add(silver_wrench_29);

                Item silver_wrench_30 = new SilverWrenchItem(new Vector2f(3650, 2040));
                this.elements.add(silver_wrench_30);

                Item silver_wrench_31 = new SilverWrenchItem(new Vector2f(3800, 3650));
                this.elements.add(silver_wrench_31);

                Item silver_wrench_32 = new SilverWrenchItem(new Vector2f(3800, 3740));
                this.elements.add(silver_wrench_32);

                // golden wrenches
                Item golden_wrench_1 = new GoldenWrenchItem(new Vector2f(55, 3890));
                this.elements.add(golden_wrench_1);

                Item golden_wrench_2 = new GoldenWrenchItem(new Vector2f(3520, 2715));
                this.elements.add(golden_wrench_2);

                // mega pulse

                // invincibility
                Item invincibility_1 = new InvincibilityItem(new Vector2f(1780, 2240));
                this.elements.add(invincibility_1);

                // EMP





                // ---------- SETUP TELEPORT CIRCLES ----------



                // ---------- SETUP HEALTH CIRCLES ----------
                HealthCircle health_circle_1 = new HealthCircle(new Vector2f(2380.f, 340.f));
                this.elements.add(health_circle_1);



                // ---------- SETUP ENEMY ENTITIES NOT MOVING ---------
                MovableEntity enemy_cannon_tank = new CannonTank(new Vector2f(3055, 3110), true, false);
                this.elements.add(enemy_cannon_tank);

                MovableEntity enemy_meme_car = new MemeCar(new Vector2f(2110, 1960), true, false);
                enemy_meme_car.setRotation(15);
                this.elements.add(enemy_meme_car);

                MovableEntity enemy_soldier_1 = new UziSoldier(new Vector2f(1370.f, 3150.f), true);
                this.elements.add(enemy_soldier_1);

                MovableEntity enemy_soldier_2 = new UziSoldier(new Vector2f(1473.f, 3198.f), true);
                this.elements.add(enemy_soldier_2);

                MovableEntity enemy_soldier_3 = new UziSoldier(new Vector2f(1266.f, 3206.f), true);
                this.elements.add(enemy_soldier_3);

                MovableEntity enemy_tank_3_rocket = new RocketTank(new Vector2f(2970.f, 130.f), true, false);
                enemy_tank_3_rocket.setRotation(100);
                this.elements.add(enemy_tank_3_rocket);

                MovableEntity enemy_tank_4_rocket = new RocketTank(new Vector2f(3250.f, 140.f), true, false);
                enemy_tank_4_rocket.setRotation(160);
                this.elements.add(enemy_tank_4_rocket);

                MovableEntity enemy_tank_5_rocket = new RocketTank(new Vector2f(3460.f, 70.f), true, false);
                enemy_tank_5_rocket.setRotation(225);
                this.elements.add(enemy_tank_5_rocket);

                MovableEntity enemy_soldier_4 = new UziSoldier(new Vector2f(2565.f, 160.f), true);
                this.elements.add(enemy_soldier_4);

                MovableEntity enemy_soldier_5 = new UziSoldier(new Vector2f(2500.f, 240.f), true);
                this.elements.add(enemy_soldier_5);

                MovableEntity enemy_soldier_6 = new UziSoldier(new Vector2f(2460.f, 60.f), true);
                this.elements.add(enemy_soldier_6);

                MovableEntity enemy_soldier_7 = new UziSoldier(new Vector2f(2350.f, 100.f), true);
                this.elements.add(enemy_soldier_7);

                MovableEntity enemy_soldier_8 = new UziSoldier(new Vector2f(2340.f, 160.f), true);
                this.elements.add(enemy_soldier_8);


                List<Vector2f> wayPoints = new ArrayList<>();
                MovableEntity enemy_shell_tank_1 = new ShellTank(new Vector2f(2100.f, 2050.f), true, false);
                enemy_shell_tank_1.isMandatory = true;
                enemy_shell_tank_1.setMoving(true);
                enemy_shell_tank_1.setRotation(180);
                wayPoints.add(new Vector2f(2100.f, 2100.f));
                wayPoints.add(new Vector2f(2100.f, 2500.f));
                wayPoints.add(new Vector2f(1600.f, 2500.f));
                wayPoints.add(new Vector2f(1600.f, 2100.f));
                enemy_shell_tank_1.addWayPoints(new WayPointManager(enemy_shell_tank_1.getPosition(), enemy_shell_tank_1.getRotation(),
                        wayPoints, 0));
                this.elements.add(enemy_shell_tank_1);

                MovableEntity enemy_shell_tank_2 = new ShellTank(new Vector2f(1600.f, 2600.f), true, false);
                enemy_shell_tank_2.isMandatory = true;
                enemy_shell_tank_2.setMoving(true);

                enemy_shell_tank_2.addWayPoints(new WayPointManager(enemy_shell_tank_2.getPosition(),
                        enemy_shell_tank_2.getRotation(),
                        wayPoints, 2));
                this.elements.add(enemy_shell_tank_2);


                EditorWaypointList editorWaypointList = new EditorWaypointList(this, wayPoints);
                editorWaypointList.setAsFinished();
                allWayPointLists.add(editorWaypointList);


                // ---------- SETUP ENEMY PLANES ----------
                MovableEntity enemy_plane_1 = new WarPlane(new Vector2f(2700, 3500), true, false);
                wayPoints = new ArrayList<>();
                wayPoints.add(new Vector2f(2700, 3367));
                wayPoints.add(new Vector2f(2700, 3034));
                wayPoints.add(new Vector2f(3033, 2700));
                wayPoints.add(new Vector2f(3366, 2700));
                wayPoints.add(new Vector2f(3700, 3033));
                wayPoints.add(new Vector2f(3700, 3366));
                wayPoints.add(new Vector2f(3367, 3700));
                wayPoints.add(new Vector2f(3034, 3700));
                enemy_plane_1.addWayPoints(new WayPointManager(enemy_plane_1.getPosition(),
                        enemy_plane_1.getRotation(),
                        wayPoints));
                this.elements.add(enemy_plane_1);

                editorWaypointList = new EditorWaypointList(this, wayPoints);
                editorWaypointList.setAsFinished();
                allWayPointLists.add(editorWaypointList);

                MovableEntity enemy_plane_2 = new WarPlane(new Vector2f(2700, 1900), true, false);
                wayPoints = new ArrayList<>();
                wayPoints.add(new Vector2f(2700, 1767));
                wayPoints.add(new Vector2f(2700, 1434));
                wayPoints.add(new Vector2f(3033, 1100));
                wayPoints.add(new Vector2f(3366, 1100));
                wayPoints.add(new Vector2f(3700, 1433));
                wayPoints.add(new Vector2f(3700, 1766));
                wayPoints.add(new Vector2f(3366, 2100));
                wayPoints.add(new Vector2f(3033, 2100));
                enemy_plane_2.addWayPoints(new WayPointManager(enemy_plane_2.getPosition(),
                        enemy_plane_2.getRotation(),
                        wayPoints));
                this.elements.add(enemy_plane_2);

                editorWaypointList = new EditorWaypointList(this, wayPoints);
                editorWaypointList.setAsFinished();
                allWayPointLists.add(editorWaypointList);

                MovableEntity enemy_plane_3 = new WarPlane(new Vector2f(3260, 200), true, false);
                enemy_plane_3.setRotation(180);
                wayPoints = new ArrayList<>();
                wayPoints.add(new Vector2f(3260, 613));
                wayPoints.add(new Vector2f(3260, 946));
                wayPoints.add(new Vector2f(2926, 1280));
                wayPoints.add(new Vector2f(2593, 1280));
                wayPoints.add(new Vector2f(2260, 946));
                wayPoints.add(new Vector2f(2260, 613));
                wayPoints.add(new Vector2f(2593, 280));
                wayPoints.add(new Vector2f(2926, 280));
                enemy_plane_3.addWayPoints(new WayPointManager(enemy_plane_3.getPosition(),
                        enemy_plane_3.getRotation(),
                        wayPoints));
                this.elements.add(enemy_plane_3);

                editorWaypointList = new EditorWaypointList(this, wayPoints);
                editorWaypointList.setAsFinished();
                allWayPointLists.add(editorWaypointList);


                // waypoint list 1
                wayPoints = new ArrayList<>();
                wayPoints.add(new Vector2f(1005, 1000));    // y: 1000
                wayPoints.add(new Vector2f(806, 1277));
                wayPoints.add(new Vector2f(600, 1726));
                wayPoints.add(new Vector2f(770, 1883));
                wayPoints.add(new Vector2f(1084, 1665));
                wayPoints.add(new Vector2f(1596, 1342));
                wayPoints.add(new Vector2f(1651, 1079));
                wayPoints.add(new Vector2f(1494, 872));

                editorWaypointList = new EditorWaypointList(this, wayPoints);
                editorWaypointList.setAsFinished();
                allWayPointLists.add(editorWaypointList);

                MovableEntity player_ally_1 = new NapalmTank(new Vector2f(1005, 900), false, false);
                player_ally_1.setRotation(180);
                player_ally_1.setMoving(true);
                player_ally_1.addWayPoints(new WayPointManager(player_ally_1.getPosition(),
                        player_ally_1.getRotation(),
                        wayPoints));
                this.elements.add(player_ally_1);


                // waypoint list2
                wayPoints = new ArrayList<>();
                wayPoints.add(new Vector2f(1376, 903));
                wayPoints.add(new Vector2f(1005, 1000));    // added
                wayPoints.add(new Vector2f(806, 1277));
                wayPoints.add(new Vector2f(600, 1726));
                wayPoints.add(new Vector2f(770, 1883));
                wayPoints.add(new Vector2f(789, 2769));
                wayPoints.add(new Vector2f(845, 3380));
                wayPoints.add(new Vector2f(1290, 3465));
                wayPoints.add(new Vector2f(1580, 3363));
                wayPoints.add(new Vector2f(1582, 3153));
                wayPoints.add(new Vector2f(1893, 2840));
                wayPoints.add(new Vector2f(2335, 2840));
                wayPoints.add(new Vector2f(2693, 2530));
                wayPoints.add(new Vector2f(2863, 2169));
                wayPoints.add(new Vector2f(2645, 1661));
                wayPoints.add(new Vector2f(1656, 1361));
                wayPoints.add(new Vector2f(1651, 1079));
                wayPoints.add(new Vector2f(1494, 872));

                editorWaypointList = new EditorWaypointList(this, wayPoints);
                editorWaypointList.setAsFinished();
                allWayPointLists.add(editorWaypointList);

                MovableEntity player_ally_2 = new NapalmTank(new Vector2f(1376, 903), false, false);
                player_ally_2.setRotation(180);
                player_ally_2.setMoving(true);
                player_ally_2.addWayPoints(new WayPointManager(player_ally_2.getPosition(),
                        player_ally_2.getRotation(),
                        wayPoints));
                this.elements.add(player_ally_2);


                // waypoint list4
                wayPoints = new ArrayList<>();
                wayPoints.add(new Vector2f(1171, 955));    // y: 1000
                wayPoints.add(new Vector2f(1005, 1000));    // added
                wayPoints.add(new Vector2f(806, 1277));
                wayPoints.add(new Vector2f(600, 1726));
                wayPoints.add(new Vector2f(770, 1883));
                wayPoints.add(new Vector2f(789, 2769));
                wayPoints.add(new Vector2f(845, 3380));
                wayPoints.add(new Vector2f(1290, 3465));
                wayPoints.add(new Vector2f(1580, 3363));
                wayPoints.add(new Vector2f(1930, 3260));
                wayPoints.add(new Vector2f(2380, 3327));
                wayPoints.add(new Vector2f(3120, 3350));
                wayPoints.add(new Vector2f(3288, 3187));
                wayPoints.add(new Vector2f(2863, 2169));
                wayPoints.add(new Vector2f(2645, 1661));
                wayPoints.add(new Vector2f(1656, 1361));
                wayPoints.add(new Vector2f(1651, 1079));
                wayPoints.add(new Vector2f(1494, 872));


                editorWaypointList = new EditorWaypointList(this, wayPoints);
                editorWaypointList.setAsFinished();
                allWayPointLists.add(editorWaypointList);

                MovableEntity player_ally_3 = new NapalmTank(new Vector2f(1171, 955), false, false);
                player_ally_3.setRotation(180);
                player_ally_3.setMoving(true);
                player_ally_3.addWayPoints(new WayPointManager(player_ally_3.getPosition(),
                        player_ally_3.getRotation(),
                        wayPoints));
                this.elements.add(player_ally_3);




                // ---------- SETUP THE PLAYERS DRIVABLE ENTITIES ----------
                MovableEntity player_drivable_robot_1 = new PlasmaRobot(new Vector2f(2465.f, 170.f), false, true);
                this.elements.add(player_drivable_robot_1);




            //}

        } else {
            // USER CANCELLED
            sbg.enterState(ZuluAssault.MAIN_MENU, new FadeOutTransition(), new FadeInTransition());
        }
        //gc.setFullscreen(true);   // TODO: 26.10.2020 - don't forget to enable this for a build!

    }

    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics graphics) throws SlickException {
        if (camera == null) return;
        camera.drawMap();
        camera.translateGraphics();
        // draw all instances that are moving with the map (ENTITIES, ITEMS, CIRCLES etc..) below


        // draw the player
        if (playerEntity != null) {
            graphics.setColor(opacityBlue);
            graphics.fillOval(playerOvalX, playerOvalY, playerOvalSize, playerOvalSize);
        }

        // draw placed elements
        for (Element element : elements) {
            element.draw(graphics);
        }

        // draw current waypoint list
        if (isPlacingWaypoints) {
            currentWaypointList.draw(graphics, mapMousePosition);
        }

        // draw already created waypoint lists
        for (EditorWaypointList editorWaypointList : allWayPointLists) {
            editorWaypointList.draw(graphics, mapMousePosition);
        }

        // draw connections from movable entities to waypoints
        for (Map.Entry<MovableEntity, Vector2f> entry : EditorWaypointList.getEntityConnections().entrySet()) {
            graphics.drawLine(entry.getKey().getPosition().x, entry.getKey().getPosition().y,
                    entry.getValue().x, entry.getValue().y);
        }


        camera.untranslateGraphics();
        // draw all instances that are static above the map (HUD) below

        // draw the selected element
        if (isMouseInEditor) {
            if (elementToPlace != null) {
                elementToPlace.draw(graphics);
            }
        }

        // draw waypoint circle
        if (isPlacingWaypoints) {
            if (currentWaypointList.isLockedToFirstWaypoint()) {
                graphics.drawOval(currentWaypointList.getFirstWaypoint().x - EditorWaypointList.WAYPOINT_RADIUS,
                        currentWaypointList.getFirstWaypoint().y - EditorWaypointList.WAYPOINT_RADIUS,
                        EditorWaypointList.WAYPOINT_DIAMETER,
                        EditorWaypointList.WAYPOINT_DIAMETER);
            } else {
                graphics.drawOval(screenMouseX - EditorWaypointList.WAYPOINT_RADIUS,
                        screenMouseY - EditorWaypointList.WAYPOINT_RADIUS,
                        EditorWaypointList.WAYPOINT_DIAMETER,
                        EditorWaypointList.WAYPOINT_DIAMETER);
            }
        }

        // draw the title rect
        graphics.setColor(Color.black);
        graphics.fillRect(0, 0, gc.getWidth(), TITLE_RECT_HEIGHT);
        graphics.setColor(Color.lightGray);
        graphics.setLineWidth(1);
        graphics.drawLine(0,
                TITLE_RECT_HEIGHT,
                gc.getWidth(),
                TITLE_RECT_HEIGHT);
        title_string_drawer.drawString(
                gc.getWidth() / 2.f - title_string_drawer.getWidth(title_string) / 2.f,     // center text
                2,      // top margin
                title_string,
                Color.lightGray
        );

        if (hasPopupWindow()) {
            popupWindow.draw(gc, graphics);
        }

        rightToolbar.draw(gc, graphics);
        bottomToolbar.draw(gc, graphics);

    }

    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int dt) {
        if (hasPopupWindow()) {
            popupWindow.update(gc);
            return;
        }

        this.screenMouseX = gc.getInput().getMouseX();
        this.screenMouseY = gc.getInput().getMouseY();

        camera.centerOn(mapX - mouseSlideX,
                mapY - mouseSlideY,
                gc.getHeight(), gc.getWidth(), rightToolbar.getWidth(), TITLE_RECT_HEIGHT, bottomToolbar.getHeight());

        rightToolbar.update(gc);
        bottomToolbar.update(gc);

        // move the map using keys
        if (gc.getInput().isKeyDown(Input.KEY_UP) || gc.getInput().isKeyDown(Input.KEY_W)) {
            mapY -= MOVE_SPEED * dt;
            if (mapY < -TITLE_RECT_HEIGHT) mapY = -TITLE_RECT_HEIGHT;
        }
        if (gc.getInput().isKeyDown(Input.KEY_DOWN) || gc.getInput().isKeyDown(Input.KEY_S)) {
            mapY += MOVE_SPEED * dt;
            if (mapY > mapHeight + bottomToolbar.getHeight() - gc.getHeight()) {
                mapY = mapHeight + bottomToolbar.getHeight() - gc.getHeight();
            }
        }
        if (gc.getInput().isKeyDown(Input.KEY_LEFT) || gc.getInput().isKeyDown(Input.KEY_A)) {
            mapX -= MOVE_SPEED * dt;
            if (mapX < 0) mapX = 0;
        }
        if (gc.getInput().isKeyDown(Input.KEY_RIGHT) || gc.getInput().isKeyDown(Input.KEY_D)) {
            mapX += MOVE_SPEED * dt;
            if (mapX > mapWidth - gc.getWidth() + rightToolbar.getWidth()) {
                mapX = mapWidth - gc.getWidth() + rightToolbar.getWidth();
            }
        }

        // slide the map while right mouse key is pressed
        if (gc.getInput().isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) {
            if (allowMouseSlide) {
                mouseSlideX = screenMouseX - prevMouseX;
                mouseSlideY = screenMouseY - prevMouseY;
                // make it unable to slide past the borders
                if (mapY < -TITLE_RECT_HEIGHT) mapY = -TITLE_RECT_HEIGHT;
                if (mapY > mapHeight + bottomToolbar.getHeight() - gc.getHeight()) {
                    mapY = mapHeight + bottomToolbar.getHeight() - gc.getHeight();
                }
                if (mapX < 0) mapX = 0;
                if (mapX > mapWidth - gc.getWidth() + rightToolbar.getWidth()) {
                    mapX = mapWidth - gc.getWidth() + rightToolbar.getWidth();
                }
            }
        }

        if (elementToPlace != null) {
            if (!(elementToPlace instanceof Item)) {
                elementToPlace.editorUpdate(gc, dt);
            }
            elementToPlace.getPosition().x = screenMouseX;
            elementToPlace.getPosition().y = screenMouseY;
        }

        for (Element element : elements) {
            // show the drivable animation for friendly drivable entities
            if (element instanceof MovableEntity) {
                MovableEntity movableEntity = (MovableEntity) element;
                if (movableEntity.isDrivable && !movableEntity.isHostile) {
                    ((MovableEntity) element).drivable_animation.update(dt);
                }
            }
        }

        // update current waypoint list
        if (isPlacingWaypoints) {
            currentWaypointList.update(mapMousePosition);
        }

        this.isMouseInEditor = (screenMouseX < rightToolbar.getX()
                && screenMouseY > TITLE_RECT_HEIGHT
                && screenMouseY < bottomToolbar.getY());

        this.mapMousePosition.x = mapX + screenMouseX - mouseSlideX;
        this.mapMousePosition.y = mapY + screenMouseY - mouseSlideY;
        this.mouseCollisionModel.update(0);
    }

    @Override
    public void mouseWheelMoved(int change) {
        change /= 20.f;
        if (elementToPlace != null) {
            if (elementToPlace instanceof Entity) {
                Entity entity = (Entity) elementToPlace;
                entity.setRotation(entity.getRotation() + change);
            }
            /* else {   // should not allow: rotation of items and circles
                selectedElement.getBaseImage().rotate(change);
            }
             */
        }
    }

    @Override
    public void mousePressed(int button, int mouseX, int mouseY) {
        if (button == Input.MOUSE_RIGHT_BUTTON) {
            if (isMouseInEditor) {
                allowMouseSlide = true;
                prevMouseX = mouseX;
                prevMouseY = mouseY;
            } else {
                allowMouseSlide = false;
            }
        }
    }

    @Override
    public void mouseReleased(int button, int mouseX, int mouseY) {
        if (button == Input.MOUSE_RIGHT_BUTTON) {
            mapX = mapX - mouseSlideX;
            mapY = mapY - mouseSlideY;
            mouseSlideX = 0;
            mouseSlideY = 0;
        }
    }

    @Override
    public void mouseClicked(int button, int mouseX, int mouseY, int clickCount) {
        if (hasPopupWindow()) {
            if (popupWindow.isMouseOver(mouseX, mouseY)) {
                popupWindow.onMouseClick(button, mouseX, mouseY);
            } else {
                popupWindow = null;
            }
            return;
        }

        if (isMouseInEditor) {
            System.out.println(mapMousePosition);
            if (button == Input.MOUSE_LEFT_BUTTON) {
                if (rightToolbar.getState() == RightToolbar.STATE_ADD_ELEMENT) {
                    if (elementToPlace != null) {
                        // PLACE AN ELEMENT
                        Element copy = MapElements.getDeepCopy(elementToPlace, mapX, mapY);
                        if (copy != null) {
                            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
                            // add rotation and mandatory
                            if (elementToPlace instanceof Entity) {
                                Entity entityCopy = (Entity) copy;
                                entityCopy.setRotation(((Entity) elementToPlace).getRotation());
                                entityCopy.isMandatory = EntityAdder.isMandatory;
                            }
                            elements.add(copy);
                        }
                    }
                } else if (rightToolbar.getState() == RightToolbar.STATE_MODIFY_ELEMENT) {
                    // first: special case -> move an already placed element
                    if (elementToPlace != null) {
                        Element copy = MapElements.getDeepCopy(elementToPlace, mapX, mapY);
                        elements.add(copy);
                        if (this.elementToPlace.equals(playerEntity)) setPlayerEntity(copy);  // player entity is moved
                        elementToPlace = null;
                    }

                    // SELECT A PLACED ELEMENT TO MODIFY
                    boolean hasSelectedElement = false;
                    for (Element element : elements) {
                        if (element.getCollisionModel().intersects(mouseCollisionModel)) {
                            this.elementToModify = element;
                            rightToolbar.notifyForModification(element);
                            hasSelectedElement = true;
                            break;
                        }
                    }
                    if (!hasSelectedElement) {
                        rightToolbar.notifyForModification(null);
                        this.elementToModify = null;
                    }
                } else if (rightToolbar.getState() == RightToolbar.STATE_ADD_WAYPOINT) {
                    if (currentWaypointList.isLockedToFirstWaypoint()) {
                        // is locked to the first waypoint
                        currentWaypointList.setAsFinished();
                        finishCurrentWaypointList();
                        rightToolbar.setWaypointListStatus(WaypointAdder.Status.SAVED);
                    } else {
                        // normal
                        currentWaypointList.addWaypoint(new Vector2f(mapMousePosition));
                        rightToolbar.setWaypointListStatus(WaypointAdder.Status.NOT_CONNECTED);
                    }
                }
            } else if (button == Input.MOUSE_RIGHT_BUTTON) {
                if (rightToolbar.getState() == RightToolbar.STATE_MODIFY_ELEMENT) {
                    if (elementToModify != null) {
                        // user tries to assign element to a waypoint
                        if (!(elementToModify instanceof MovableEntity)) return;    // no normal entities allowed
                        if (elementToModify.equals(playerEntity)) return;           // no player entity allowed
                        if (((MovableEntity) elementToModify).isDrivable) return;   // no drivable entities allowed
                        for (EditorWaypointList editorWaypointList : allWayPointLists) {
                            if (editorWaypointList.attemptToConnect(mapMousePosition, (MovableEntity) elementToModify))
                                return; // return on successful connection to one of the waypoint lists
                        }
                    }
                }
            }
        } else {
            // mouse is not in editor -> is on a toolbar
            if (bottomToolbar.isMouseOver(mouseX, mouseY)) {
                bottomToolbar.onMouseClick(button, mouseX, mouseY);
            } else if (rightToolbar.isMouseOver(mouseX, mouseY)) {
                rightToolbar.onMouseClick(button, mouseX, mouseY);
            }
        }
    }

    public void setElementToPlace(Element element) {
        this.elementToPlace = element;
        if (element != null) {
            element.getBaseImage().setAlpha(0.7f);
        }
    }

    public Element getElementToPlace() {
        return this.elementToPlace;
    }

    public void replaceModifiedElement(Element replacingElement) {
        Iterator<Element> i = elements.iterator();
        while (i.hasNext()) {
            Element nextElement = i.next();
            if (nextElement.getPosition().x == replacingElement.getPosition().x
                    && nextElement.getPosition().y == replacingElement.getPosition().y) {   // identify via position
                i.remove();
                elements.add(replacingElement);
                this.elementToModify = replacingElement;
                return;
            }
        }
    }

    public void removeElement(Element elementToRemove) {
        if (elementToRemove.equals(this.playerEntity)) this.playerEntity = null;
        Iterator<Element> i = elements.iterator();
        while (i.hasNext()) {
            Element nextElement = i.next();
            if (nextElement.getPosition().x == elementToRemove.getPosition().x
                    && nextElement.getPosition().y == elementToRemove.getPosition().y) {    // identify via position
                i.remove();
                this.elementToModify = null;
                return;
            }
        }
    }

    public void moveElement(Element elementToMove) {
        Iterator<Element> i = elements.iterator();
        while (i.hasNext()) {
            Element nextElement = i.next();
            if (nextElement.getPosition().x == elementToMove.getPosition().x
                    && nextElement.getPosition().y == elementToMove.getPosition().y) {    // identify via position
                i.remove();
                this.elementToPlace = elementToMove;
                break;
            }
        }
        // remove waypoint connection on entity move
        for (EditorWaypointList editorWaypointList : allWayPointLists) {
            boolean success = editorWaypointList.removeConnection((MovableEntity) elementToMove);
            if (success) break;
        }
    }

    public Element getElementToModify() {
        return this.elementToModify;
    }

    public List<Element> getElements() {
        return elements;
    }

    private boolean hasPopupWindow() {
        return this.popupWindow != null;
    }

    public void setPlayerEntity(Element playerEntity) {
        this.playerEntity = playerEntity;
        if (playerEntity == null) return;
        Image playerImage = playerEntity.getBaseImage();

        playerOvalSize = (int) (Math.max(playerImage.getHeight(), playerImage.getWidth()) * 1.5);
        playerOvalX = (int) (playerEntity.getPosition().x - playerOvalSize / 2);
        playerOvalY = (int) (playerEntity.getPosition().y - playerOvalSize / 2);
        // remove waypoint connection on player set
        for (EditorWaypointList editorWaypointList : allWayPointLists) {
            boolean success = editorWaypointList.removeConnection((MovableEntity) this.playerEntity);
            if (success) break;
        }
    }

    public Element getPlayerEntity() {
        return this.playerEntity;
    }

    public void setPlacingWaypoints(boolean val) {
        this.isPlacingWaypoints = val;
        if (val) {
            currentWaypointList = new EditorWaypointList(this);
        } else {
            currentWaypointList = null;
        }
    }

    public void finishCurrentWaypointList() {
        allWayPointLists.add(currentWaypointList);
        currentWaypointList = new EditorWaypointList(this);
    }

    public void removeLastWaypoint() {
        currentWaypointList.removeLastWaypoint();
    }

    public void clearCurrentWaypointList() {
        currentWaypointList.clear();
        rightToolbar.setWaypointListStatus(WaypointAdder.Status.EMPTY);
    }

    public List<EditorWaypointList> getAllWayPointLists() {
        return allWayPointLists;
    }

    public boolean hasNoMandatoryEntities() {
        for (Element element : elements) {
            if (element instanceof Item) {
                if (((Item) element).isMandatory) return false;
            } else if (element instanceof Entity) {
                if (((Entity) element).isMandatory) return false;
            }
        }
        return true;
    }

    public boolean hasAircraftWithoutWaypoints() {
        for (Element element : elements) {
            if (element instanceof Aircraft) {
                if (!EditorWaypointList.getEntityConnections().containsKey(element)) return true;
            }
        }
        return false;
    }

    public void setPopupWindow(CenterPopupWindow popupWindow) {
        if (popupWindow != null) {
            isMouseInEditor = false;
            this.popupWindow = popupWindow;
        } else {
            this.popupWindow = null;
        }
    }

    public String getSimpleMapName() {
        return this.mapName.substring(0, this.mapName.length() - 4);
    }

    @Override
    public int getID() {
        return ZuluAssault.LEVEL_EDITOR;
    }

}
