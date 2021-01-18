package level_editor;

import game.audio.MenuSounds;
import game.graphics.fonts.FontManager;
import game.logic.Camera;
import game.models.CollisionModel;
import game.models.Element;
import game.models.entities.Entity;
import game.models.entities.MovableEntity;
import game.models.entities.aircraft.*;
import game.models.interaction_circles.InteractionCircle;
import game.models.interaction_circles.TeleportCircle;
import game.models.items.*;
import game.util.LevelDataStorage;
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
import game.logic.TileMapData;
import settings.UserSettings;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.*;

public class LevelEditor extends BasicGameState {

    // for choosing the map
    public static final String CUSTOM_MAPS_FOLDER = "assets/maps_custom/";

    private String mapName;

    // elements
    private List<Element> elements;
    private List<TeleportCircle> teleportCircles;
    private Element elementToPlace, elementToModify;
    private EditorWaypointList currentWaypointList;
    private List<EditorWaypointList> allWayPointLists;
    private boolean isPlacingWaypoints;

    // player entity
    private Element playerEntity;
    private static final Color opacityBlue = new Color(16, 133, 199, 80);
    private static final Color teleportCircleConnectionLineColor = new Color(35, 67, 250, 70);
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
        teleportCircles = new ArrayList<>();
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

            if (lds != null) {
                setPlayerEntity(lds.getPlayerEntity(true));
                // add player
                this.elements.add(playerEntity);
                // add all items
                this.elements.addAll(lds.getAllItems());
                // add all circles
                List<InteractionCircle> interactionCircles = lds.getAllCircles();
                for (InteractionCircle interactionCircle : interactionCircles) {
                    if (interactionCircle instanceof TeleportCircle) {
                        teleportCircles.add((TeleportCircle) interactionCircle);
                    }
                }
                this.elements.addAll(lds.getAllCircles());
                // add all entities
                List<Entity> entities = lds.getAllEntities();
                for (Entity entity : entities) {
                    if (entity instanceof Aircraft) {
                        ((Aircraft) entity).hasLanded = true;   // so the shadow won't draw
                    }
                }
                this.elements.addAll(entities);
                // add all entities that have waypoints
                List<MovableEntity> waypointEntities = lds.getAllWaypointEntities();
                for (MovableEntity entity : waypointEntities) {
                    if (entity instanceof Aircraft) {
                        ((Aircraft) entity).hasLanded = true;   // so the shadow won't draw
                    }
                }
                this.elements.addAll(waypointEntities);
                EditorWaypointList.setEntityConnections(lds.getEntityConnections(waypointEntities));
                allWayPointLists.addAll(lds.getAllWaypointLists(this));
                // fill in the save-text-fields with the 'title', 'briefing' and 'debriefing' message
                this.bottomToolbar.fillSaveTextFields(lds.mission_title, lds.briefing_message, lds.debriefing_message, lds.musicIdx);

                // FOR MAP REBUILD:
            /*


            // ---------- SETUP ITEMS ----------
            Item silver_wrench_1 = new SilverWrenchItem(new Vector2f(2035, 913));
            this.elements.add(silver_wrench_1);

            Item silver_wrench_2 = new SilverWrenchItem(new Vector2f(3900, 59));
            this.elements.add(silver_wrench_2);

            Item silver_wrench_3 = new SilverWrenchItem(new Vector2f(3006, 46));
            this.elements.add(silver_wrench_3);

            Item silver_wrench_4 = new SilverWrenchItem(new Vector2f(2868, 50));
            this.elements.add(silver_wrench_4);

            Item silver_wrench_5 = new SilverWrenchItem(new Vector2f(2744, 50));
            this.elements.add(silver_wrench_5);

            Item silver_wrench_6 = new SilverWrenchItem(new Vector2f(2608, 46));
            this.elements.add(silver_wrench_6);

            Item silver_wrench_7 = new SilverWrenchItem(new Vector2f(2621, 273));
            this.elements.add(silver_wrench_7);

            Item silver_wrench_8 = new SilverWrenchItem(new Vector2f(2621, 273));
            this.elements.add(silver_wrench_8);

            Item silver_wrench_9 = new SilverWrenchItem(new Vector2f(1238, 415));
            this.elements.add(silver_wrench_9);

            Item silver_wrench_10 = new SilverWrenchItem(new Vector2f(1128, 415));
            this.elements.add(silver_wrench_10);

            Item silver_wrench_11 = new SilverWrenchItem(new Vector2f(963, 415));
            this.elements.add(silver_wrench_11);

            Item silver_wrench_12 = new SilverWrenchItem(new Vector2f(40, 2785));
            this.elements.add(silver_wrench_12);

            Item silver_wrench_13 = new SilverWrenchItem(new Vector2f(47, 2907));
            this.elements.add(silver_wrench_13);

            Item silver_wrench_14 = new SilverWrenchItem(new Vector2f(45, 3048));
            this.elements.add(silver_wrench_14);

            Item silver_wrench_15 = new SilverWrenchItem(new Vector2f(47, 3179));
            this.elements.add(silver_wrench_15);

            Item silver_wrench_16 = new SilverWrenchItem(new Vector2f(3068, 3912));
            this.elements.add(silver_wrench_16);

            Item silver_wrench_17 = new SilverWrenchItem(new Vector2f(3291, 3908));
            this.elements.add(silver_wrench_17);

            Item silver_wrench_18 = new SilverWrenchItem(new Vector2f(3249, 3540));
            this.elements.add(silver_wrench_18);

            Item silver_wrench_19 = new SilverWrenchItem(new Vector2f(3452, 3909));
            this.elements.add(silver_wrench_19);

            Item silver_wrench_20 = new SilverWrenchItem(new Vector2f(3543, 3242));
            this.elements.add(silver_wrench_20);

            Item silver_wrench_21 = new SilverWrenchItem(new Vector2f(3543, 3000));
            this.elements.add(silver_wrench_21);

            Item silver_wrench_22 = new SilverWrenchItem(new Vector2f(3667, 2037));
            this.elements.add(silver_wrench_22);

            Item silver_wrench_23 = new SilverWrenchItem(new Vector2f(3512, 2036));
            this.elements.add(silver_wrench_23);

            Item silver_wrench_24 = new SilverWrenchItem(new Vector2f(3359, 2034));
            this.elements.add(silver_wrench_24);

            Item silver_wrench_25 = new SilverWrenchItem(new Vector2f(3227, 2031));
            this.elements.add(silver_wrench_25);

            Item silver_wrench_26 = new SilverWrenchItem(new Vector2f(3263, 3244));
            this.elements.add(silver_wrench_26);

            Item silver_wrench_27 = new SilverWrenchItem(new Vector2f(2978, 3263));
            this.elements.add(silver_wrench_27);

            Item silver_wrench_28 = new SilverWrenchItem(new Vector2f(2979, 2980));
            this.elements.add(silver_wrench_28);

            Item silver_wrench_29 = new SilverWrenchItem(new Vector2f(3911, 1540));
            this.elements.add(silver_wrench_29);

            Item silver_wrench_30 = new SilverWrenchItem(new Vector2f(3905, 1397));
            this.elements.add(silver_wrench_30);

            Item silver_wrench_31 = new SilverWrenchItem(new Vector2f(3911, 1252));
            this.elements.add(silver_wrench_31);

            Item silver_wrench_32 = new SilverWrenchItem(new Vector2f(3910, 1080));
            this.elements.add(silver_wrench_32);

            Item silver_wrench_33 = new SilverWrenchItem(new Vector2f(3056, 998));
            this.elements.add(silver_wrench_33);

            Item silver_wrench_34 = new SilverWrenchItem(new Vector2f(2889, 1553));
            this.elements.add(silver_wrench_34);

            Item silver_wrench_35 = new SilverWrenchItem(new Vector2f(3107, 1704));
            this.elements.add(silver_wrench_35);

            Item silver_wrench_36 = new SilverWrenchItem(new Vector2f(2692, 864));
            this.elements.add(silver_wrench_36);

            Item silver_wrench_37 = new SilverWrenchItem(new Vector2f(2446, 305));
            this.elements.add(silver_wrench_37);

            // golden wrenches
            Item golden_wrench_1 = new GoldenWrenchItem(new Vector2f(724, 692));
            this.elements.add(golden_wrench_1);

            Item golden_wrench_2 = new GoldenWrenchItem(new Vector2f(841, 1548));
            this.elements.add(golden_wrench_2);



            // invincibility
            InvincibilityItem invincibility_1 = new InvincibilityItem(new Vector2f(2424, 2181));
            this.elements.add(invincibility_1);

            // EMP



            // ---------- SETUP TELEPORT CIRCLES ----------
            TeleportCircle teleport_circle_1 = new TeleportCircle(new Vector2f(1086, 1135));
            this.elements.add(teleport_circle_1);

            TeleportCircle teleport_circle_2 = new TeleportCircle(new Vector2f(3892, 3899));
            this.elements.add(teleport_circle_2);


            // ---------- SETUP HEALTH CIRCLES ----------
            HealthCircle health_circle_1 = new HealthCircle(new Vector2f(126, 3880));
            this.elements.add(health_circle_1);





            // ---------- SETUP ENEMY ENTITIES NOT MOVING ----------
            MovableEntity enemy_mega_pulse_tank_1 = new MegaPulseTank(new Vector2f(494, 432), true, false);
            enemy_mega_pulse_tank_1.setRotation(144);
            enemy_mega_pulse_tank_1.isMandatory = true;
            this.elements.add(enemy_mega_pulse_tank_1);

            MovableEntity enemy_mega_pulse_tank_2 = new MegaPulseTank(new Vector2f(1181, 720), true, false);
            enemy_mega_pulse_tank_2.setRotation(73);
            enemy_mega_pulse_tank_2.isMandatory = true;
            this.elements.add(enemy_mega_pulse_tank_2);

            MovableEntity enemy_mega_pulse_tank_3 = new MegaPulseTank(new Vector2f(663, 1006), true, false);
            enemy_mega_pulse_tank_3.setRotation(25);
            enemy_mega_pulse_tank_3.isMandatory = true;
            this.elements.add(enemy_mega_pulse_tank_3);

            MovableEntity enemy_mega_pulse_tank_4 = new MegaPulseTank(new Vector2f(415, 1280), true, false);
            enemy_mega_pulse_tank_4.isMandatory = true;
            this.elements.add(enemy_mega_pulse_tank_4);


            MovableEntity enemy_shell_tank_1 = new ShellTank(new Vector2f(1288, 1278), true, false);
            enemy_shell_tank_1.setRotation(90);
            enemy_shell_tank_1.isMandatory = true;
            this.elements.add(enemy_shell_tank_1);

            MovableEntity enemy_shell_tank_2 = new ShellTank(new Vector2f(654, 1560), true, false);
            enemy_shell_tank_2.setRotation(90);
            enemy_shell_tank_2.isMandatory = true;
            this.elements.add(enemy_shell_tank_2);

            MovableEntity enemy_rocket_tank_1 = new RocketTank(new Vector2f(3000, 3551), true, false);
            enemy_rocket_tank_1.setRotation(170);
            enemy_rocket_tank_1.isMandatory = true;
            this.elements.add(enemy_rocket_tank_1);

            MovableEntity enemy_rocket_tank_2 = new RocketTank(new Vector2f(3555, 3565), true, false);
            enemy_rocket_tank_2.setRotation(45);
            enemy_rocket_tank_2.isMandatory = true;
            this.elements.add(enemy_rocket_tank_2);

            MovableEntity enemy_rocket_tank_3 = new RocketTank(new Vector2f(3285, 3012), true, false);
            enemy_rocket_tank_3.setRotation(325);
            enemy_rocket_tank_3.isMandatory = true;
            this.elements.add(enemy_rocket_tank_3);






            // ---------- SETUP ENEMY ENTITIES THAT ARE MOVING ----------
            // moving napalm tanks way point list

            // the two moving shell tanks left side
            final List<Vector2f> wayPoints_shell_tanks = new ArrayList<>();
            wayPoints_shell_tanks.add(new Vector2f(1794, 850));
            wayPoints_shell_tanks.add(new Vector2f(1631, 144));
            wayPoints_shell_tanks.add(new Vector2f(961, 99));
            wayPoints_shell_tanks.add(new Vector2f(201, 99));
            wayPoints_shell_tanks.add(new Vector2f(157, 1083));
            wayPoints_shell_tanks.add(new Vector2f(157, 2209));
            wayPoints_shell_tanks.add(new Vector2f(686, 2268));
            wayPoints_shell_tanks.add(new Vector2f(958, 2451));
            wayPoints_shell_tanks.add(new Vector2f(1253, 2320));
            wayPoints_shell_tanks.add(new Vector2f(1408, 2203));
            wayPoints_shell_tanks.add(new Vector2f(1665, 2126));
            wayPoints_shell_tanks.add(new Vector2f(1691, 1401));

            EditorWaypointList editorWaypointList = new EditorWaypointList(this, wayPoints_shell_tanks);
            editorWaypointList.setAsFinished();
            allWayPointLists.add(editorWaypointList);

            // starting top middle
            MovableEntity enemy_moving_shell_tank_1 = new ShellTank(new Vector2f(961, 99), true, false);
            enemy_moving_shell_tank_1.setRotation(270);
            enemy_moving_shell_tank_1.addWayPoints(new WayPointManager(enemy_moving_shell_tank_1.getPosition(),
                    enemy_moving_shell_tank_1.getRotation(),
                    wayPoints_shell_tanks,
                    3));
            enemy_moving_shell_tank_1.isMandatory = true;
            this.elements.add(enemy_moving_shell_tank_1);

            // starting bottom left
            MovableEntity enemy_moving_shell_tank_2 = new ShellTank(new Vector2f(157, 2209), true, false);
            enemy_moving_shell_tank_2.setRotation(180);
            enemy_moving_shell_tank_2.addWayPoints(new WayPointManager(enemy_moving_shell_tank_2.getPosition(),
                    enemy_moving_shell_tank_2.getRotation(),
                    wayPoints_shell_tanks,
                    6));
            enemy_moving_shell_tank_2.isMandatory = true;
            this.elements.add(enemy_moving_shell_tank_2);


            // rocket robot bottom middle
            final List<Vector2f> wayPoints_rocket_robot = new ArrayList<>();
            wayPoints_rocket_robot.add(new Vector2f(1998, 2729));
            wayPoints_rocket_robot.add(new Vector2f(2056, 2767));
            wayPoints_rocket_robot.add(new Vector2f(2252, 2788));
            wayPoints_rocket_robot.add(new Vector2f(2607, 2744));
            wayPoints_rocket_robot.add(new Vector2f(2636, 2559));
            wayPoints_rocket_robot.add(new Vector2f(2693, 2388));
            wayPoints_rocket_robot.add(new Vector2f(2630, 2177));
            wayPoints_rocket_robot.add(new Vector2f(2568, 2076));
            wayPoints_rocket_robot.add(new Vector2f(2438, 2055));
            wayPoints_rocket_robot.add(new Vector2f(2138, 2107));
            wayPoints_rocket_robot.add(new Vector2f(2038, 2216));
            wayPoints_rocket_robot.add(new Vector2f(1989, 2414));


            editorWaypointList = new EditorWaypointList(this, wayPoints_rocket_robot);
            editorWaypointList.setAsFinished();
            allWayPointLists.add(editorWaypointList);


            MovableEntity enemy_moving_rocket_robot_1 = new RocketRobot(new Vector2f(2630, 2177), true, false);
            enemy_moving_rocket_robot_1.setRotation(270);
            enemy_moving_rocket_robot_1.addWayPoints(new WayPointManager(enemy_moving_rocket_robot_1.getPosition(),
                    enemy_moving_rocket_robot_1.getRotation(),
                    wayPoints_rocket_robot,
                    6));
            enemy_moving_rocket_robot_1.isMandatory = true;
            this.elements.add(enemy_moving_rocket_robot_1);







            // ---------- SETUP ENEMY PLANES ----------





            // ---------- SETUP THE ENEMY DRIVABLE ENTITIES ----------
            MovableEntity enemy_drivable_napalm_tank = new NapalmTank(new Vector2f(3699, 3137), true, true);
            this.elements.add(enemy_drivable_napalm_tank);

            MovableEntity enemy_drivable_rocket_tank = new RocketTank(new Vector2f(3702, 3417), true, true);
            this.elements.add(enemy_drivable_rocket_tank);

            MovableEntity enemy_drivable_cannon_tank_1 = new CannonTank(new Vector2f(3420, 3417), true, true);
            this.elements.add(enemy_drivable_cannon_tank_1);

            MovableEntity enemy_drivable_cannon_tank_2 = new CannonTank(new Vector2f(3420, 3134), true, true);
            this.elements.add(enemy_drivable_cannon_tank_2);


            // ---------- SETUP THE PLAYERS DRIVABLE ENTITIES ----------
            ShellRobot drivable_shell_robot = new ShellRobot(new Vector2f(2293, 2649), false, true);
            this.elements.add(drivable_shell_robot);




            // ADD PLAYER
            Vector2f playerStartPos = new Vector2f(3775, 220);
            PlasmaRobot plasmaRobot = new PlasmaRobot(playerStartPos, false, true);
            setPlayerEntity(plasmaRobot);
            this.elements.add(playerEntity);
            */


                //}

            } else {
                // no lds data found
                System.out.println("No level data found.");
            }
            //gc.setFullscreen(true);   // TODO: 26.10.2020 - don't forget to enable this for a build!

        } else {
            // USER CANCELLED
            sbg.enterState(ZuluAssault.MAIN_MENU, new FadeOutTransition(), new FadeInTransition());
        }
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

        // draw connections between teleport circles
        if (!teleportCircles.isEmpty()) {
            graphics.setLineWidth(2.f);
            graphics.setColor(teleportCircleConnectionLineColor);
            Iterator<TeleportCircle> i = teleportCircles.iterator();
            if ((teleportCircles.size() & 1) == 0) {
                // even number -> draw from one to next circle
                while (i.hasNext()) {
                    TeleportCircle from = i.next();
                    TeleportCircle to = i.next();
                    graphics.drawLine(from.getPosition().x, from.getPosition().y,
                            to.getPosition().x, to.getPosition().y);
                }
            } else {
                // uneven number -> draw connections between all teleport circles
                for (int idx = 0; idx < teleportCircles.size(); ++idx) {
                    for (int idx2 = idx + 1; idx2 < teleportCircles.size(); ++idx2) {
                        graphics.drawLine(teleportCircles.get(idx).getPosition().x, teleportCircles.get(idx).getPosition().y,
                                teleportCircles.get(idx2).getPosition().x, teleportCircles.get(idx2).getPosition().y);
                    }
                }
            }
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
                if (mapY - mouseSlideY < -TITLE_RECT_HEIGHT) {
                    mapY = -TITLE_RECT_HEIGHT;
                    mouseSlideY = 0;
                }
                if (mapY - mouseSlideY > mapHeight + bottomToolbar.getHeight() - gc.getHeight()) {
                    mapY = mapHeight + bottomToolbar.getHeight() - gc.getHeight();
                    mouseSlideY = 0;
                }
                if (mapX - mouseSlideX < 0) {
                    mapX = 0;
                    mouseSlideX = 0;
                }
                if (mapX - mouseSlideX > mapWidth - gc.getWidth() + rightToolbar.getWidth()) {
                    mapX = mapWidth - gc.getWidth() + rightToolbar.getWidth();
                    mouseSlideX = 0;
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
                                if (elementToPlace instanceof Aircraft) {
                                    ((Aircraft) entityCopy).hasLanded = true;   // dont't draw shadow
                                }
                            } else if (elementToPlace instanceof TeleportCircle) {
                                teleportCircles.add((TeleportCircle) copy);
                            }
                            elements.add(copy);
                        }
                    }
                } else if (rightToolbar.getState() == RightToolbar.STATE_MODIFY_ELEMENT) {
                    // first: special case -> move an already placed element
                    if (elementToPlace != null) {
                        Element copy = MapElements.getDeepCopy(elementToPlace, mapX, mapY);
                        if (copy instanceof Aircraft) {
                            ((Aircraft) copy).hasLanded = true;   // dont't draw shadow
                        }
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
            if (element instanceof Aircraft) {
                ((Aircraft) element).hasLanded = true;
            }
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
                if (elementToRemove instanceof TeleportCircle) teleportCircles.remove(elementToRemove);
                i.remove();
                this.elementToModify = null;
                break;
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
        if (elementToMove instanceof MovableEntity) {
            // remove waypoint connection on entity move
            for (EditorWaypointList editorWaypointList : allWayPointLists) {
                boolean success = editorWaypointList.removeConnection((MovableEntity) elementToMove);
                if (success) break;
            }
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
                if (!((Aircraft) element).isDrivable) {
                    if (element.equals(this.playerEntity)) continue;
                    if (!EditorWaypointList.getEntityConnections().containsKey(element)) return true;
                }
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
