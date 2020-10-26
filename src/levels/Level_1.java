package levels;

import level_editor.util.WayPointManager;
import main.ZuluAssault;
import models.entities.windmills.Windmill;
import models.entities.windmills.WindmillGrey;
import models.interaction_circles.HealthCircle;
import models.items.*;
import models.entities.MovableEntity;
import models.entities.aircraft.WarPlane;
import models.entities.robots.PlasmaRobot;
import models.entities.soldiers.UziSoldier;
import models.entities.tanks.*;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import java.util.ArrayList;
import java.util.List;

public class Level_1 extends AbstractLevel implements GameState {

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        // this makes sure we don't init on the first two calls, since this would be a waste of cpu power
        if (!calledOnce) {    // call this only one time because this is static information
            mission_title = "Take the airfield";
            briefing_message = "A key enemy airfield has been located in the northwest quadrant. Alpha team will attack " +
                    "the airfield from the southwest. Provide supporting firepower for Alpha team.";
            debriefing_message = "The airfield has been secured. Good job.";
            calledOnce = true;
            return;
        }

        resetLevel();    // reset the level before init

        /* ---------- SETUP ITEMS ---------- */

        // silver wrenches
        Item silver_wrench_1 = new SilverWrenchItem(new Vector2f(160.f, 100.f));
        items.add(silver_wrench_1);

        Item silver_wrench_2 = new SilverWrenchItem(new Vector2f(220.f, 100.f));
        items.add(silver_wrench_2);

        Item silver_wrench_3 = new SilverWrenchItem(new Vector2f(227.f, 45.f));
        items.add(silver_wrench_3);

        Item silver_wrench_4 = new SilverWrenchItem(new Vector2f(158.f, 45.f));
        items.add(silver_wrench_4);

        Item silver_wrench_5 = new SilverWrenchItem(new Vector2f(1215, 2315));
        items.add(silver_wrench_5);

        Item silver_wrench_6 = new SilverWrenchItem(new Vector2f(1220, 2410));
        items.add(silver_wrench_6);

        Item silver_wrench_7 = new SilverWrenchItem(new Vector2f(1222, 2610));
        items.add(silver_wrench_7);

        Item silver_wrench_8 = new SilverWrenchItem(new Vector2f(1220, 2680));
        items.add(silver_wrench_8);

        Item silver_wrench_9 = new SilverWrenchItem(new Vector2f(25, 3720));
        items.add(silver_wrench_9);

        Item silver_wrench_10 = new SilverWrenchItem(new Vector2f(100, 3780));
        items.add(silver_wrench_10);

        Item silver_wrench_11 = new SilverWrenchItem(new Vector2f(185, 3840));
        items.add(silver_wrench_11);

        Item silver_wrench_12 = new SilverWrenchItem(new Vector2f(190, 3940));
        items.add(silver_wrench_12);

        Item silver_wrench_13 = new SilverWrenchItem(new Vector2f(3278, 3760));
        items.add(silver_wrench_13);

        Item silver_wrench_14 = new SilverWrenchItem(new Vector2f(3280, 3905));
        items.add(silver_wrench_14);

        Item silver_wrench_15 = new SilverWrenchItem(new Vector2f(1250, 14));
        items.add(silver_wrench_15);

        Item silver_wrench_16 = new SilverWrenchItem(new Vector2f(1610, 16));
        items.add(silver_wrench_16);

        Item silver_wrench_17 = new SilverWrenchItem(new Vector2f(1625, 110));
        items.add(silver_wrench_17);

        Item silver_wrench_18 = new SilverWrenchItem(new Vector2f(1845, 595));
        items.add(silver_wrench_18);

        Item silver_wrench_19 = new SilverWrenchItem(new Vector2f(2278, 735));
        items.add(silver_wrench_19);

        Item silver_wrench_20 = new SilverWrenchItem(new Vector2f(2172, 185));
        items.add(silver_wrench_20);

        Item silver_wrench_21 = new SilverWrenchItem(new Vector2f(2145, 85));
        items.add(silver_wrench_21);

        Item silver_wrench_22 = new SilverWrenchItem(new Vector2f(2090, 25));
        items.add(silver_wrench_22);

        Item silver_wrench_23 = new SilverWrenchItem(new Vector2f(3725, 400));
        items.add(silver_wrench_23);

        Item silver_wrench_24 = new SilverWrenchItem(new Vector2f(3775, 475));
        items.add(silver_wrench_24);

        Item silver_wrench_25 = new SilverWrenchItem(new Vector2f(3625, 900));
        items.add(silver_wrench_25);

        Item silver_wrench_26 = new SilverWrenchItem(new Vector2f(3577, 1225));
        items.add(silver_wrench_26);

        Item silver_wrench_27 = new SilverWrenchItem(new Vector2f(3645, 1320));
        items.add(silver_wrench_27);

        Item silver_wrench_28 = new SilverWrenchItem(new Vector2f(3340, 2040));
        items.add(silver_wrench_28);

        Item silver_wrench_29 = new SilverWrenchItem(new Vector2f(3475, 2040));
        items.add(silver_wrench_29);

        Item silver_wrench_30 = new SilverWrenchItem(new Vector2f(3650, 2040));
        items.add(silver_wrench_30);

        Item silver_wrench_31 = new SilverWrenchItem(new Vector2f(3800, 3650));
        items.add(silver_wrench_31);

        Item silver_wrench_32 = new SilverWrenchItem(new Vector2f(3800, 3740));
        items.add(silver_wrench_32);

        // golden wrenches
        Item golden_wrench_1 = new GoldenWrenchItem(new Vector2f(55, 3890));
        items.add(golden_wrench_1);

        Item golden_wrench_2 = new GoldenWrenchItem(new Vector2f(3520, 2715));
        items.add(golden_wrench_2);

        // mega pulse
        /* --- none --- */

        // invincibility
        Item invincibility_1 = new InvincibilityItem(new Vector2f(1780, 2240));
        items.add(invincibility_1);

        // EMP
        /* --- none --- */


        /* ---------- SETUP TELEPORT CIRCLES ---------- */
        /* --- none --- */


        /* ---------- SETUP HEALTH CIRCLES ---------- */
        HealthCircle health_circle_1 = new HealthCircle(new Vector2f(2380.f, 340.f));
        health_circles.add(health_circle_1);


        /* ---------- SETUP ENEMY ENTITIES NOT MOVING ---------- */
        MovableEntity enemy_cannon_tank = new CannonTank(new Vector2f(3055, 3110), true, false);
        all_hostile_entities.add(enemy_cannon_tank);

        MovableEntity enemy_meme_car = new MemeCar(new Vector2f(2110, 1960), true, false);
        enemy_meme_car.setRotation(15);
        all_hostile_entities.add(enemy_meme_car);

        MovableEntity enemy_soldier_1 = new UziSoldier(new Vector2f(1370.f, 3150.f), true);
        all_hostile_entities.add(enemy_soldier_1);

        MovableEntity enemy_soldier_2 = new UziSoldier(new Vector2f(1473.f, 3198.f), true);
        all_hostile_entities.add(enemy_soldier_2);

        MovableEntity enemy_soldier_3 = new UziSoldier(new Vector2f(1266.f, 3206.f), true);
        all_hostile_entities.add(enemy_soldier_3);

        MovableEntity enemy_tank_3_rocket = new RocketTank(new Vector2f(2970.f, 130.f), true, false);
        enemy_tank_3_rocket.setRotation(100);
        all_hostile_entities.add(enemy_tank_3_rocket);

        MovableEntity enemy_tank_4_rocket = new RocketTank(new Vector2f(3250.f, 140.f), true, false);
        enemy_tank_4_rocket.setRotation(160);
        all_hostile_entities.add(enemy_tank_4_rocket);

        MovableEntity enemy_tank_5_rocket = new RocketTank(new Vector2f(3460.f, 70.f), true, false);
        enemy_tank_5_rocket.setRotation(225);
        all_hostile_entities.add(enemy_tank_5_rocket);

        MovableEntity enemy_soldier_4 = new UziSoldier(new Vector2f(2565.f, 160.f), true);
        all_hostile_entities.add(enemy_soldier_4);

        MovableEntity enemy_soldier_5 = new UziSoldier(new Vector2f(2500.f, 240.f), true);
        all_hostile_entities.add(enemy_soldier_5);

        MovableEntity enemy_soldier_6 = new UziSoldier(new Vector2f(2460.f, 60.f), true);
        all_hostile_entities.add(enemy_soldier_6);

        MovableEntity enemy_soldier_7 = new UziSoldier(new Vector2f(2350.f, 100.f), true);
        all_hostile_entities.add(enemy_soldier_7);

        MovableEntity enemy_soldier_8 = new UziSoldier(new Vector2f(2340.f, 160.f), true);
        all_hostile_entities.add(enemy_soldier_8);

        // TODO: 25.10.2020  remove this windmill
        Windmill enemy_windmill = new WindmillGrey(new Vector2f(850, 800), true);
        all_hostile_entities.add(enemy_windmill);


        /* ---------- SETUP ENEMY ENTITIES THAT ARE MOVING ---------- */
        // create the way points for the enemy shell tanks on the airfield in the middle
        List<Vector2f> wayPoints;

        MovableEntity enemy_shell_tank_1 = new ShellTank(new Vector2f(2100.f, 2050.f), true, false);
        enemy_shell_tank_1.setAsMandatory();
        enemy_shell_tank_1.setMoving(true);
        enemy_shell_tank_1.getBaseImage().setRotation(180);
        wayPoints = new ArrayList<>();
        wayPoints.add(new Vector2f(2100.f, 2100.f));
        wayPoints.add(new Vector2f(2100.f, 2500.f));
        wayPoints.add(new Vector2f(1600.f, 2500.f));
        wayPoints.add(new Vector2f(1600.f, 2100.f));
        enemy_shell_tank_1.addWayPoints(new WayPointManager(enemy_shell_tank_1.getPosition(), enemy_shell_tank_1.getRotation(),
                wayPoints));
        all_hostile_entities.add(enemy_shell_tank_1);

        MovableEntity enemy_shell_tank_2 = new ShellTank(new Vector2f(1600.f, 2600.f), true, false);
        enemy_shell_tank_2.setAsMandatory();
        enemy_shell_tank_2.setMoving(true);
        wayPoints = new ArrayList<>();
        wayPoints.add(new Vector2f(1600.f, 2500.f));
        wayPoints.add(new Vector2f(1600.f, 2100.f));
        wayPoints.add(new Vector2f(2100.f, 2100.f));
        wayPoints.add(new Vector2f(2100.f, 2500.f));
        enemy_shell_tank_2.addWayPoints(new WayPointManager(enemy_shell_tank_2.getPosition(),
                enemy_shell_tank_2.getRotation(),
                wayPoints));
        all_hostile_entities.add(enemy_shell_tank_2);


        /* ---------- SETUP ENEMY PLANES ---------- */
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
        all_hostile_entities.add(enemy_plane_1);

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
        all_hostile_entities.add(enemy_plane_2);

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
        all_hostile_entities.add(enemy_plane_3);


        /* ---------- SETUP THE PLAYERS ALLIED ENTITIES ---------- */
        List<List<Vector2f>> tankWayPointLists = createTankWayPointLists(); // create custom way point list

        MovableEntity player_ally_1 = new NapalmTank(new Vector2f(925, 740), false, false);
        player_ally_1.setRotation(180);
        player_ally_1.setMoving(true);
        player_ally_1.addWayPoints(new WayPointManager(tankWayPointLists,
                player_ally_1.getPosition(),
                player_ally_1.getRotation()));
        all_friendly_entities.add(player_ally_1);

        MovableEntity player_ally_2 = new NapalmTank(new Vector2f(900, -2200), false, false);
        player_ally_2.setRotation(180);
        player_ally_2.setMoving(true);
        player_ally_2.addWayPoints(new WayPointManager(tankWayPointLists,
                player_ally_2.getPosition(),
                player_ally_2.getRotation()));
        all_friendly_entities.add(player_ally_2);

        MovableEntity player_ally_3 = new NapalmTank(new Vector2f(910, -600), false, false);
        player_ally_3.setRotation(180);
        player_ally_3.setMoving(true);
        player_ally_3.addWayPoints(new WayPointManager(tankWayPointLists,
                player_ally_3.getPosition(),
                player_ally_3.getRotation()));
        all_friendly_entities.add(player_ally_3);


        /* ---------- SETUP THE PLAYERS DRIVABLE ENTITIES ---------- */
        MovableEntity player_drivable_robot_1 = new PlasmaRobot(new Vector2f(2465.f, 170.f), false, true);
        drivable_entities.add(player_drivable_robot_1);


        /* ---------- SETUP THE PLAYER START POSITION AND ENTITY ---------- */
        Vector2f playerStartPos = new Vector2f(750, 800);
        CannonTank tank = new CannonTank(playerStartPos, false, true);
        tank.setRotation(190);


        /* ---------- DEFINE THE MAP ---------- */
        map = new TiledMap("assets/maps/level_1.tmx");


        player.init(tank);
        super.init(gameContainer, stateBasedGame);
    }

    private List<List<Vector2f>> createTankWayPointLists() {
        List<List<Vector2f>> wayPointLists = new ArrayList<>();
        List<Vector2f> wayPointList_1 = new ArrayList<>();
        List<Vector2f> wayPointList_2 = new ArrayList<>();
        List<Vector2f> wayPointList_3 = new ArrayList<>();
        List<Vector2f> wayPointList_4 = new ArrayList<>();
        wayPointLists.add(wayPointList_1);
        wayPointLists.add(wayPointList_2);
        wayPointLists.add(wayPointList_3);
        wayPointLists.add(wayPointList_4);

        // create a temp list to hold all way points that are the same between all lists
        List<Vector2f> tempList = new ArrayList<>();

        tempList.add(new Vector2f(1005, 1000));
        tempList.add(new Vector2f(806, 1277));
        tempList.add(new Vector2f(600, 1726));
        tempList.add(new Vector2f(770, 1883));

        for (List<Vector2f> wayPointList : wayPointLists) {
            wayPointList.addAll(tempList);
        }
        tempList.clear();

        // individual wayPointList_1 points
        wayPointList_1.add(new Vector2f(1084, 1665));
        wayPointList_1.add(new Vector2f(1596, 1342));

        // points equal for list 2, 3 and 4
        tempList.add(new Vector2f(789, 2769));
        tempList.add(new Vector2f(845, 3380));
        tempList.add(new Vector2f(1290, 3465));
        tempList.add(new Vector2f(1580, 3363));
        wayPointList_2.addAll(tempList);
        wayPointList_3.addAll(tempList);
        wayPointList_4.addAll(tempList);
        tempList.clear();

        // individual wayPointList_2 points
        wayPointList_2.add(new Vector2f(1582, 3153));
        wayPointList_2.add(new Vector2f(1893, 2840));

        // individual wayPointList_3 points
        wayPointList_3.add(new Vector2f(1939, 3271));
        wayPointList_3.add(new Vector2f(1848, 2890));

        // points equal for list 2 and 3
        tempList.add(new Vector2f(2335, 2840));
        tempList.add(new Vector2f(2693, 2530));
        wayPointList_2.addAll(tempList);
        wayPointList_3.addAll(tempList);
        tempList.clear();

        // individual wayPointList_4 points
        wayPointList_4.add(new Vector2f(1930, 3260));
        wayPointList_4.add(new Vector2f(2380, 3327));
        wayPointList_4.add(new Vector2f(3120, 3350));
        wayPointList_4.add(new Vector2f(3288, 3187));

        // points equal for list 2 , 3 and 4
        tempList.add(new Vector2f(2863, 2169));
        tempList.add(new Vector2f(2645, 1661));
        tempList.add(new Vector2f(1656, 1361));
        wayPointList_2.addAll(tempList);
        wayPointList_3.addAll(tempList);
        wayPointList_4.addAll(tempList);
        tempList.clear();


        // last two points are the same
        tempList.add(new Vector2f(1651, 1079));
        tempList.add(new Vector2f(1494, 872));

        for (List<Vector2f> wayPointList : wayPointLists) {
            wayPointList.addAll(tempList);
        }
        tempList.clear();

        return wayPointLists;
    }

    @Override
    public int getCombatMusicIdx() {
        return 0;
    }

    @Override
    public int getID() {
        return ZuluAssault.LEVEL_1;
    }
}
