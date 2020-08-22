package levels;

import logic.WayPointManager;
import main.ZuluAssault;
import models.interaction_circles.HealthCircle;
import models.items.GoldenWrenchItem;
import models.items.InvincibilityItem;
import models.items.Item;
import models.items.SilverWrenchItem;
import models.war_attenders.MovableWarAttender;
import models.war_attenders.planes.GreenEnemyPlane;
import models.war_attenders.robots.PlasmaRobot;
import models.war_attenders.robots.ShellRobot;
import models.war_attenders.soldiers.EnemySoldier;
import models.war_attenders.soldiers.RocketSoldier;
import models.war_attenders.tanks.CannonTank;
import models.war_attenders.tanks.ShellTank;
import models.war_attenders.tanks.Tank;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import java.util.ArrayList;
import java.util.List;

public class Level_2 extends AbstractLevel implements GameState {

    public Level_2() {
        super();
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        if (!calledOnce) {
            mission_title = "Recapture Mechs";
            briefing_message = "One of our operatives has defective along with two mechs. These mechs are guarded " +
                    "lightly by a small enemy force. We must recapture the mechs before the enemy can strengthen its " +
                    "forces.";
            debriefing_message = "We got the mechs back. Over and Out.";
            calledOnce = true;
            return;
        }

        resetLevel();    // reset the level before init

        combatBackgroundMusic.setIdx(1);

        // SETUP ITEMS
        // silver wrenches
        Item silver_wrench_1 = new SilverWrenchItem(new Vector2f(3935, 1350));
        items.add(silver_wrench_1);

        Item silver_wrench_2 = new SilverWrenchItem(new Vector2f(3930, 1290));
        items.add(silver_wrench_2);

        Item silver_wrench_3 = new SilverWrenchItem(new Vector2f(3930, 1190));
        items.add(silver_wrench_3);

        Item silver_wrench_4 = new SilverWrenchItem(new Vector2f(3930, 1090));
        items.add(silver_wrench_4);

        Item silver_wrench_5 = new SilverWrenchItem(new Vector2f(2820, 2435));
        items.add(silver_wrench_5);

        Item silver_wrench_6 = new SilverWrenchItem(new Vector2f(2815, 2481));
        items.add(silver_wrench_6);

        Item silver_wrench_7 = new SilverWrenchItem(new Vector2f(2940, 2610));
        items.add(silver_wrench_7);

        Item silver_wrench_8 = new SilverWrenchItem(new Vector2f(2940, 2670));
        items.add(silver_wrench_8);

        Item silver_wrench_9 = new SilverWrenchItem(new Vector2f(2940, 2745));
        items.add(silver_wrench_9);

        Item silver_wrench_10 = new SilverWrenchItem(new Vector2f(2885, 2790));
        items.add(silver_wrench_10);

        Item silver_wrench_11 = new SilverWrenchItem(new Vector2f(2810, 2790));
        items.add(silver_wrench_11);

        Item silver_wrench_12 = new SilverWrenchItem(new Vector2f(1345, 3620));
        items.add(silver_wrench_12);

        Item silver_wrench_13 = new SilverWrenchItem(new Vector2f(1185, 3635));
        items.add(silver_wrench_13);

        Item silver_wrench_14 = new SilverWrenchItem(new Vector2f(625, 3740));
        items.add(silver_wrench_14);

        Item silver_wrench_15 = new SilverWrenchItem(new Vector2f(490, 3740));
        items.add(silver_wrench_15);

        Item silver_wrench_16 = new SilverWrenchItem(new Vector2f(250, 2990));
        items.add(silver_wrench_16);

        Item silver_wrench_17 = new SilverWrenchItem(new Vector2f(950, 705));
        items.add(silver_wrench_17);

        Item silver_wrench_18 = new SilverWrenchItem(new Vector2f(935, 790));
        items.add(silver_wrench_18);

        Item silver_wrench_19 = new SilverWrenchItem(new Vector2f(910, 865));
        items.add(silver_wrench_19);

        Item silver_wrench_20 = new SilverWrenchItem(new Vector2f(1325, 215));
        items.add(silver_wrench_20);

        Item silver_wrench_21 = new SilverWrenchItem(new Vector2f(1310, 80));
        items.add(silver_wrench_21);

        Item silver_wrench_22 = new SilverWrenchItem(new Vector2f(1505, 20));
        items.add(silver_wrench_22);

        Item silver_wrench_23 = new SilverWrenchItem(new Vector2f(1640, 25));
        items.add(silver_wrench_23);

        Item silver_wrench_24 = new SilverWrenchItem(new Vector2f(1735, 20));
        items.add(silver_wrench_24);

        Item silver_wrench_25 = new SilverWrenchItem(new Vector2f(1930, 25));
        items.add(silver_wrench_25);

        Item silver_wrench_26 = new SilverWrenchItem(new Vector2f(2990, 30));
        items.add(silver_wrench_26);

        Item silver_wrench_27 = new SilverWrenchItem(new Vector2f(3020, 110));
        items.add(silver_wrench_27);

        Item silver_wrench_28 = new SilverWrenchItem(new Vector2f(3150, 170));
        items.add(silver_wrench_28);

        Item silver_wrench_29 = new SilverWrenchItem(new Vector2f(3285, 260));
        items.add(silver_wrench_29);

        Item silver_wrench_30 = new SilverWrenchItem(new Vector2f(2170, 667));
        items.add(silver_wrench_30);

        Item silver_wrench_31 = new SilverWrenchItem(new Vector2f(2350, 690));
        items.add(silver_wrench_31);

        Item silver_wrench_32 = new SilverWrenchItem(new Vector2f(2220, 840));
        items.add(silver_wrench_32);

        Item silver_wrench_33 = new SilverWrenchItem(new Vector2f(2550, 810));
        items.add(silver_wrench_33);

        // golden wrenches
        Item golden_wrench_1 = new GoldenWrenchItem(new Vector2f(3620, 2020));
        items.add(golden_wrench_1);

        Item golden_wrench_2 = new GoldenWrenchItem(new Vector2f(2180, 3785));
        items.add(golden_wrench_2);

        Item golden_wrench_3 = new GoldenWrenchItem(new Vector2f(2415, 2395));
        items.add(golden_wrench_3);

        Item golden_wrench_4 = new GoldenWrenchItem(new Vector2f(450, 3575));
        items.add(golden_wrench_4);

        // invincibility
        Item invincibility_item_1 = new InvincibilityItem(new Vector2f(3620, 10));
        items.add(invincibility_item_1);

        Item invincibility_item_2 = new InvincibilityItem(new Vector2f(1380, 2940));
        items.add(invincibility_item_2);

        // SETUP HEALTH CIRCLES
        HealthCircle health_circle_1 = new HealthCircle(new Vector2f(147.f, 2612.f));
        health_circles.add(health_circle_1);

        // SETUP ENEMY WAR ATTENDERS
        // near health circle
        MovableWarAttender enemy_shell_robot = new ShellRobot(new Vector2f(386, 2424), true, false);
        hostile_movable_war_attenders.add(enemy_shell_robot);

        MovableWarAttender enemy_soldier_1 = new RocketSoldier(new Vector2f(490, 2310), true);
        hostile_movable_war_attenders.add(enemy_soldier_1);

        MovableWarAttender enemy_soldier_2 = new RocketSoldier(new Vector2f(460, 2380), true);
        hostile_movable_war_attenders.add(enemy_soldier_2);

        MovableWarAttender enemy_soldier_3 = new RocketSoldier(new Vector2f(475, 2470), true);
        hostile_movable_war_attenders.add(enemy_soldier_3);

        MovableWarAttender enemy_soldier_4 = new RocketSoldier(new Vector2f(540, 2490), true);
        hostile_movable_war_attenders.add(enemy_soldier_4);

        // near spawn
        MovableWarAttender enemy_soldier_5 = new EnemySoldier(new Vector2f(3620, 2690), true);
        hostile_movable_war_attenders.add(enemy_soldier_5);

        MovableWarAttender enemy_soldier_6 = new EnemySoldier(new Vector2f(3658, 2703), true);
        hostile_movable_war_attenders.add(enemy_soldier_6);

        MovableWarAttender enemy_soldier_7 = new EnemySoldier(new Vector2f(3830, 2215), true);
        hostile_movable_war_attenders.add(enemy_soldier_7);

        MovableWarAttender enemy_soldier_8 = new EnemySoldier(new Vector2f(3420, 2200), true);
        hostile_movable_war_attenders.add(enemy_soldier_8);

        // top left
        MovableWarAttender enemy_tank_1 = new CannonTank(new Vector2f(170, 430), true, false);
        enemy_tank_1.setAsMandatory();
        hostile_movable_war_attenders.add(enemy_tank_1);

        MovableWarAttender enemy_tank_2 = new CannonTank(new Vector2f(360, 460), true, false);
        enemy_tank_2.setAsMandatory();
        hostile_movable_war_attenders.add(enemy_tank_2);

        MovableWarAttender enemy_tank_3 = new CannonTank(new Vector2f(510, 360), true, false);
        enemy_tank_3.setAsMandatory();
        hostile_movable_war_attenders.add(enemy_tank_3);

        MovableWarAttender enemy_tank_4 = new CannonTank(new Vector2f(690, 245), true, false);
        enemy_tank_4.setAsMandatory();
        enemy_tank_4.setRotation(-45);
        hostile_movable_war_attenders.add(enemy_tank_4);

        MovableWarAttender enemy_soldier_9 = new EnemySoldier(new Vector2f(100, 1060), true);
        hostile_movable_war_attenders.add(enemy_soldier_9);

        // SETUP PLAYER'S DRIVABLE WAR ATTENDERS
        MovableWarAttender player_drivable_tank_1 = new ShellTank(new Vector2f(2243, 2520), false, true);
        drivable_war_attenders.add(player_drivable_tank_1);

        MovableWarAttender player_drivable_robot_1 = new PlasmaRobot(new Vector2f(150, 165), false, true);
        drivable_war_attenders.add(player_drivable_robot_1);

        MovableWarAttender player_drivable_robot_2 = new PlasmaRobot(new Vector2f(320, 165), false, true);
        drivable_war_attenders.add(player_drivable_robot_2);

        // middle

        // create the waypoint list for the 3 cannon tanks
        List<Vector2f> wayPoints_cannon_tanks = new ArrayList<>();
        wayPoints_cannon_tanks.add(new Vector2f(2454, 160));
        wayPoints_cannon_tanks.add(new Vector2f(1868, 543));
        wayPoints_cannon_tanks.add(new Vector2f(1735, 904));
        wayPoints_cannon_tanks.add(new Vector2f(1821, 1279));
        wayPoints_cannon_tanks.add(new Vector2f(2103, 1658));
        wayPoints_cannon_tanks.add(new Vector2f(2467, 1692));
        wayPoints_cannon_tanks.add(new Vector2f(2962, 1515));
        wayPoints_cannon_tanks.add(new Vector2f(3148, 1118));
        wayPoints_cannon_tanks.add(new Vector2f(3196, 645));
        wayPoints_cannon_tanks.add(new Vector2f(3000, 400));

        List<Vector2f> wayPoints;
        MovableWarAttender enemy_tank_5 = new CannonTank(new Vector2f(2454, 140), true, false);
        enemy_tank_5.setMoving(true);
        enemy_tank_5.base_image.setRotation(180);
        enemy_tank_5.addWayPoints(new WayPointManager(enemy_tank_5.getPosition(), enemy_tank_5.getRotation(),
                wayPoints_cannon_tanks));
        hostile_movable_war_attenders.add(enemy_tank_5);

        MovableWarAttender enemy_tank_6 = new CannonTank(new Vector2f(2713, 271), true, false);
        enemy_tank_6.setMoving(true);
        enemy_tank_6.base_image.setRotation(310);
        // has same way points as 'enemy_tank_5'
        enemy_tank_6.addWayPoints(new WayPointManager(enemy_tank_6.getPosition(), enemy_tank_6.getRotation(),
                wayPoints_cannon_tanks));
        hostile_movable_war_attenders.add(enemy_tank_6);

        MovableWarAttender enemy_tank_7 = new CannonTank(new Vector2f(2400, 1692), true, false);
        enemy_tank_7.setMoving(true);
        enemy_tank_7.base_image.setRotation(90);
        enemy_tank_7.addWayPoints(new WayPointManager(enemy_tank_7.getPosition(), enemy_tank_7.getRotation(),
                wayPoints_cannon_tanks,
                5));
        hostile_movable_war_attenders.add(enemy_tank_7);

        MovableWarAttender enemy_drivable_tank_1 = new ShellTank(new Vector2f(2363, 2520), true, true);
        hostile_movable_war_attenders.add(enemy_drivable_tank_1);

        // planes
        MovableWarAttender enemy_plane_1 = new GreenEnemyPlane(new Vector2f(1900, 960), true, false);
        enemy_plane_1.setRotation(90);
        wayPoints = new ArrayList<>();
        wayPoints.add(new Vector2f(2060, 960));
        wayPoints.add(new Vector2f(2860, 960));
        wayPoints.add(new Vector2f(2960, 1060));
        wayPoints.add(new Vector2f(2960, 1860));
        wayPoints.add(new Vector2f(2860, 1960));
        wayPoints.add(new Vector2f(2060, 1960));
        wayPoints.add(new Vector2f(1960, 1860));
        wayPoints.add(new Vector2f(1960, 1060));
        enemy_plane_1.addWayPoints(new WayPointManager(enemy_plane_1.getPosition(),
                enemy_plane_1.getRotation(),
                wayPoints));
        hostile_movable_war_attenders.add(enemy_plane_1);

        MovableWarAttender enemy_plane_2 = new GreenEnemyPlane(new Vector2f(3700, 680), true, false);
        enemy_plane_2.setRotation(90);
        wayPoints = new ArrayList<>();
        wayPoints.add(new Vector2f(3820, 680));
        wayPoints.add(new Vector2f(3920, 780));
        wayPoints.add(new Vector2f(3920, 1580));
        wayPoints.add(new Vector2f(3820, 1680));
        wayPoints.add(new Vector2f(3020, 1680));
        wayPoints.add(new Vector2f(2920, 1580));
        wayPoints.add(new Vector2f(2920, 780));
        wayPoints.add(new Vector2f(3020, 680));
        enemy_plane_2.addWayPoints(new WayPointManager(enemy_plane_2.getPosition(),
                enemy_plane_2.getRotation(),
                wayPoints));
        hostile_movable_war_attenders.add(enemy_plane_2);

        MovableWarAttender enemy_plane_3 = new GreenEnemyPlane(new Vector2f(1600, 1250), true, false);
        enemy_plane_3.setRotation(270);
        wayPoints = new ArrayList<>();
        wayPoints.add(new Vector2f(1510, 1250));
        wayPoints.add(new Vector2f(1410, 1150));
        wayPoints.add(new Vector2f(1410, 350));
        wayPoints.add(new Vector2f(1510, 250));
        wayPoints.add(new Vector2f(2310, 250));
        wayPoints.add(new Vector2f(2410, 350));
        wayPoints.add(new Vector2f(2410, 1150));
        wayPoints.add(new Vector2f(2310, 1250));
        enemy_plane_3.addWayPoints(new WayPointManager(enemy_plane_3.getPosition(),
                enemy_plane_3.getRotation(),
                wayPoints));
        hostile_movable_war_attenders.add(enemy_plane_3);


        // SETUP THE PLAYER START POSITION AND WAR ATTENDER
        Vector2f playerStartPos = new Vector2f(3848, 3888);

        CannonTank tank = new CannonTank(playerStartPos, false, true);

        // DEFINE THE MAP
        map = new TiledMap("assets/maps/level_2.tmx");

        player.init(tank);

        super.init(gameContainer, stateBasedGame);
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) {
        super.render(gameContainer, stateBasedGame, graphics);
    }

    @Override
    public int getID() {
        return ZuluAssault.LEVEL_2;
    }
}
