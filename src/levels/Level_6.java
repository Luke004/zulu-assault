package levels;

import logic.WayPointManager;
import main.ZuluAssault;
import models.interaction_circles.TeleportCircle;
import models.items.GoldenWrenchItem;
import models.items.Item;
import models.items.SilverWrenchItem;
import models.war_attenders.MovableWarAttender;
import models.war_attenders.planes.GreenEnemyPlane;
import models.war_attenders.robots.RocketRobot;
import models.war_attenders.tanks.CannonTank;
import models.war_attenders.tanks.NapalmTank;
import models.war_attenders.tanks.RocketTank;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import java.util.ArrayList;
import java.util.List;

public class Level_6 extends AbstractLevel implements GameState {

    public Level_6() {
        super();
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        if (!calledOnce) {
            mission_title = "";
            briefing_message = "";
            debriefing_message = "";
            calledOnce = true;
            return;
        }

        resetLevel();    // reset the level before init

        combatBackgroundMusic.setIdx(0);

        // SETUP ITEMS
        // silver wrenches
        Item silver_wrench_1 = new SilverWrenchItem(new Vector2f(53, 3215));
        items.add(silver_wrench_1);

        Item silver_wrench_2 = new SilverWrenchItem(new Vector2f(53, 3095));
        items.add(silver_wrench_2);

        Item silver_wrench_3 = new SilverWrenchItem(new Vector2f(53, 3000));
        items.add(silver_wrench_3);

        Item silver_wrench_4 = new SilverWrenchItem(new Vector2f(53, 2890));
        items.add(silver_wrench_4);

        Item silver_wrench_5 = new SilverWrenchItem(new Vector2f(1840, 1965));
        items.add(silver_wrench_5);

        Item silver_wrench_6 = new SilverWrenchItem(new Vector2f(60, 1325));
        items.add(silver_wrench_6);

        Item silver_wrench_7 = new SilverWrenchItem(new Vector2f(1000, 50));
        items.add(silver_wrench_7);

        Item silver_wrench_8 = new SilverWrenchItem(new Vector2f(675, 40));
        items.add(silver_wrench_8);

        Item silver_wrench_9 = new SilverWrenchItem(new Vector2f(90, 90));
        items.add(silver_wrench_9);

        // golden wrenches
        Item golden_wrench_1 = new GoldenWrenchItem(new Vector2f(1768, 3765));
        items.add(golden_wrench_1);

        Item golden_wrench_2 = new GoldenWrenchItem(new Vector2f(672, 1755));
        items.add(golden_wrench_2);

        Item golden_wrench_3 = new GoldenWrenchItem(new Vector2f(672, 1825));
        items.add(golden_wrench_3);

        Item golden_wrench_4 = new GoldenWrenchItem(new Vector2f(1908, 436));
        items.add(golden_wrench_4);


        // SETUP TELEPORT CIRCLES
        TeleportCircle teleport_circle_1 = new TeleportCircle(new Vector2f(168, 1826));
        teleport_circles.add(teleport_circle_1);

        TeleportCircle teleport_circle_2 = new TeleportCircle(new Vector2f(1829, 101));
        teleport_circles.add(teleport_circle_2);


        // SETUP FRIENDLY WAR ATTENDERS
        MovableWarAttender player_friendly_tank_1 = new RocketTank(new Vector2f(1405, 2339), false, false);
        player_friendly_tank_1.setRotation(195);
        friendly_movable_war_attenders.add(player_friendly_tank_1);


        // SETUP ENEMY WAR ATTENDERS
        MovableWarAttender enemy_tank_1 = new NapalmTank(new Vector2f(1286, 1090), true, false);
        enemy_tank_1.setRotation(205);
        hostile_movable_war_attenders.add(enemy_tank_1);

        MovableWarAttender enemy_tank_2 = new RocketTank(new Vector2f(402, 1389), true, false);
        enemy_tank_2.setRotation(110);
        hostile_movable_war_attenders.add(enemy_tank_2);

        MovableWarAttender enemy_tank_3 = new RocketTank(new Vector2f(1375, 410), true, false);
        enemy_tank_3.setRotation(120);
        enemy_tank_3.setAsMandatory();
        hostile_movable_war_attenders.add(enemy_tank_3);

        MovableWarAttender enemy_tank_4 = new RocketTank(new Vector2f(1390, 665), true, false);
        enemy_tank_4.setRotation(45);
        enemy_tank_4.setAsMandatory();
        hostile_movable_war_attenders.add(enemy_tank_4);
        // top left
        MovableWarAttender enemy_tank_5 = new CannonTank(new Vector2f(565, 330), true, false);
        enemy_tank_5.setRotation(305);
        enemy_tank_5.setAsMandatory();
        hostile_movable_war_attenders.add(enemy_tank_5);

        MovableWarAttender enemy_tank_6 = new RocketTank(new Vector2f(315, 342), true, false);
        enemy_tank_6.setRotation(45);
        enemy_tank_6.setAsMandatory();
        hostile_movable_war_attenders.add(enemy_tank_6);

        // create the waypoint list for the 3 rocket tanks
        List<Vector2f> wayPoints_rocket_tanks = new ArrayList<>();
        wayPoints_rocket_tanks.add(new Vector2f(1140, 2620));
        wayPoints_rocket_tanks.add(new Vector2f(292, 2455));
        wayPoints_rocket_tanks.add(new Vector2f(209, 2398));
        wayPoints_rocket_tanks.add(new Vector2f(324, 2255));
        wayPoints_rocket_tanks.add(new Vector2f(840, 2270));
        wayPoints_rocket_tanks.add(new Vector2f(1110, 2105));
        wayPoints_rocket_tanks.add(new Vector2f(1252, 2043));
        wayPoints_rocket_tanks.add(new Vector2f(1345, 2039));
        wayPoints_rocket_tanks.add(new Vector2f(1486, 2047));
        wayPoints_rocket_tanks.add(new Vector2f(1565, 2130));
        wayPoints_rocket_tanks.add(new Vector2f(1744, 2346));
        wayPoints_rocket_tanks.add(new Vector2f(1673, 2571));

        MovableWarAttender enemy_tank_7 = new RocketTank(new Vector2f(1744, 2250), true, false);
        enemy_tank_7.setRotation(180);
        enemy_tank_7.addWayPoints(new WayPointManager(enemy_tank_7.getPosition(),
                enemy_tank_7.getRotation(),
                wayPoints_rocket_tanks,
                0));
        hostile_movable_war_attenders.add(enemy_tank_7);

        MovableWarAttender enemy_tank_8 = new RocketTank(new Vector2f(315, 2255), true, false);
        enemy_tank_8.setRotation(90);
        enemy_tank_8.addWayPoints(new WayPointManager(enemy_tank_8.getPosition(),
                enemy_tank_8.getRotation(),
                wayPoints_rocket_tanks,
                3));
        hostile_movable_war_attenders.add(enemy_tank_8);

        MovableWarAttender enemy_tank_9 = new RocketTank(new Vector2f(830, 2270), true, false);
        enemy_tank_9.setRotation(90);
        enemy_tank_9.addWayPoints(new WayPointManager(enemy_tank_9.getPosition(),
                enemy_tank_9.getRotation(),
                wayPoints_rocket_tanks,
                4));
        hostile_movable_war_attenders.add(enemy_tank_9);


        // enemy planes
        MovableWarAttender enemy_plane_1 = new GreenEnemyPlane(new Vector2f(90, 2000), true, false);
        enemy_plane_1.setRotation(90);
        List<Vector2f> wayPoints = new ArrayList<>();
        wayPoints.add(new Vector2f(100, 2000));
        wayPoints.add(new Vector2f(900, 2000));
        wayPoints.add(new Vector2f(1000, 2100));
        wayPoints.add(new Vector2f(1000, 2900));
        wayPoints.add(new Vector2f(900, 3000));
        wayPoints.add(new Vector2f(100, 3000));
        wayPoints.add(new Vector2f(20, 2900));
        wayPoints.add(new Vector2f(20, 2100));
        enemy_plane_1.addWayPoints(new WayPointManager(enemy_plane_1.getPosition(),
                enemy_plane_1.getRotation(),
                wayPoints));
        hostile_movable_war_attenders.add(enemy_plane_1);

        MovableWarAttender enemy_plane_2 = new GreenEnemyPlane(new Vector2f(950, 10), true, false);
        enemy_plane_2.setRotation(90);
        wayPoints = new ArrayList<>();
        wayPoints.add(new Vector2f(1350, 10));
        wayPoints.add(new Vector2f(1450, 110));
        wayPoints.add(new Vector2f(1450, 900));
        wayPoints.add(new Vector2f(1350, 1000));
        wayPoints.add(new Vector2f(550, 1000));
        wayPoints.add(new Vector2f(450, 900));
        wayPoints.add(new Vector2f(450, 110));
        wayPoints.add(new Vector2f(550, 10));
        enemy_plane_2.addWayPoints(new WayPointManager(enemy_plane_2.getPosition(),
                enemy_plane_2.getRotation(),
                wayPoints));
        hostile_movable_war_attenders.add(enemy_plane_2);

        MovableWarAttender enemy_plane_3 = new GreenEnemyPlane(new Vector2f(1400, 1170), true, false);
        enemy_plane_3.setRotation(90);
        wayPoints = new ArrayList<>();
        wayPoints.add(new Vector2f(1800, 1170));            // top right
        wayPoints.add(new Vector2f(1900, 1270));
        wayPoints.add(new Vector2f(1900, 2070));            // bottom right
        wayPoints.add(new Vector2f(1800, 2170));
        wayPoints.add(new Vector2f(1000, 2170));            // bottom left
        wayPoints.add(new Vector2f(900, 2070));
        wayPoints.add(new Vector2f(900, 1270));             // top left
        wayPoints.add(new Vector2f(1000, 1170));
        enemy_plane_3.addWayPoints(new WayPointManager(enemy_plane_3.getPosition(),
                enemy_plane_3.getRotation(),
                wayPoints));
        hostile_movable_war_attenders.add(enemy_plane_3);

        MovableWarAttender enemy_plane_4 = new GreenEnemyPlane(new Vector2f(530, 1260), true, false);
        enemy_plane_4.setRotation(90);
        wayPoints = new ArrayList<>();
        wayPoints.add(new Vector2f(930, 1260));          // top right
        wayPoints.add(new Vector2f(1030, 1360));
        wayPoints.add(new Vector2f(1030, 2160));          // bottom right
        wayPoints.add(new Vector2f(930, 2260));
        wayPoints.add(new Vector2f(130, 2260));            // bottom left
        wayPoints.add(new Vector2f(30, 2160));
        wayPoints.add(new Vector2f(30, 1360));            // top left
        wayPoints.add(new Vector2f(130, 1260));
        enemy_plane_4.addWayPoints(new WayPointManager(enemy_plane_4.getPosition(),
                enemy_plane_4.getRotation(),
                wayPoints));
        hostile_movable_war_attenders.add(enemy_plane_4);

        MovableWarAttender enemy_plane_5 = new GreenEnemyPlane(new Vector2f(1376, 2590), true, false);
        enemy_plane_5.setRotation(90);
        wayPoints = new ArrayList<>();
        wayPoints.add(new Vector2f(1776, 2590));          // top right
        wayPoints.add(new Vector2f(1876, 2690));
        wayPoints.add(new Vector2f(1876, 3490));          // bottom right
        wayPoints.add(new Vector2f(1776, 3590));
        wayPoints.add(new Vector2f(976, 3590));            // bottom left
        wayPoints.add(new Vector2f(876, 3490));
        wayPoints.add(new Vector2f(876, 2690));            // top left
        wayPoints.add(new Vector2f(976, 2590));
        enemy_plane_5.addWayPoints(new WayPointManager(enemy_plane_5.getPosition(),
                enemy_plane_5.getRotation(),
                wayPoints));
        hostile_movable_war_attenders.add(enemy_plane_5);


        // SETUP THE PLAYER START POSITION AND WAR ATTENDER
        Vector2f playerStartPos = new Vector2f(703, 3831);
        RocketRobot robot = new RocketRobot(playerStartPos, false, true);

        // DEFINE THE MAP
        map = new TiledMap("assets/maps/level_5.tmx");

        player.init(robot);

        super.init(gameContainer, stateBasedGame);
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) {
        super.render(gameContainer, stateBasedGame, graphics);
    }

    @Override
    public int getID() {
        return ZuluAssault.LEVEL_5;
    }
}
