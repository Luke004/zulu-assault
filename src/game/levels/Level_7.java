package levels;

import level_editor.util.WayPointManager;
import main.ZuluAssault;
import game.models.entities.MovableEntity;
import game.models.entities.aircraft.ScoutJet;
import game.models.entities.robots.PlasmaRobot;
import game.models.entities.robots.RocketRobot;
import game.models.entities.tanks.CannonTank;
import game.models.entities.tanks.NapalmTank;
import game.models.entities.tanks.RocketTank;
import game.models.entities.tanks.ShellTank;
import game.models.interaction_circles.HealthCircle;
import game.models.interaction_circles.TeleportCircle;
import game.models.items.*;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import java.util.ArrayList;
import java.util.List;

public class Level_7 extends AbstractLevel implements GameState {

    public Level_7() {
        super();
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        if (!calledOnce) {
            mission_title = "X-Roads";
            briefing_message = "A complex transporter setup has allowed the enemy units to do a switchback on us. " +
                    "Your job is to find a way through the crossroads and destroy the enemy.";
            debriefing_message = "We're back on track. Keep it up and yu might get promoted to corporal.";
            calledOnce = true;
            return;
        }

        resetLevel();    // reset the level before init

        /* ---------- SETUP ITEMS ---------- */

        // silver wrenches
        Item silver_wrench_1 = new SilverWrenchItem(new Vector2f(0, 2520));
        items.add(silver_wrench_1);

        Item silver_wrench_2 = new SilverWrenchItem(new Vector2f(75, 2522));
        items.add(silver_wrench_2);

        Item silver_wrench_3 = new SilverWrenchItem(new Vector2f(158, 2522));
        items.add(silver_wrench_3);

        Item silver_wrench_4 = new SilverWrenchItem(new Vector2f(402, 2518));
        items.add(silver_wrench_4);

        Item silver_wrench_5 = new SilverWrenchItem(new Vector2f(485, 2517));
        items.add(silver_wrench_5);

        Item silver_wrench_6 = new SilverWrenchItem(new Vector2f(1358, 3910));
        items.add(silver_wrench_6);

        Item silver_wrench_7 = new SilverWrenchItem(new Vector2f(1458, 3912));
        items.add(silver_wrench_7);

        Item silver_wrench_8 = new SilverWrenchItem(new Vector2f(1534, 3910));
        items.add(silver_wrench_8);

        Item silver_wrench_9 = new SilverWrenchItem(new Vector2f(1730, 3925));
        items.add(silver_wrench_9);

        Item silver_wrench_10 = new SilverWrenchItem(new Vector2f(1730, 3838));
        items.add(silver_wrench_10);

        Item silver_wrench_11 = new SilverWrenchItem(new Vector2f(1729, 3729));
        items.add(silver_wrench_11);

        Item silver_wrench_12 = new SilverWrenchItem(new Vector2f(2150, 3715));
        items.add(silver_wrench_12);

        Item silver_wrench_13 = new SilverWrenchItem(new Vector2f(2150, 3833));
        items.add(silver_wrench_13);

        Item silver_wrench_14 = new SilverWrenchItem(new Vector2f(2152, 3932));
        items.add(silver_wrench_14);

        // golden wrenches
        /* --- none --- */

        // mega pulse
        Item mega_pulse_1 = new MegaPulseItem(new Vector2f(45, 2655));
        items.add(mega_pulse_1);

        Item mega_pulse_2 = new MegaPulseItem(new Vector2f(3887, 1745));
        items.add(mega_pulse_2);

        Item mega_pulse_3 = new MegaPulseItem(new Vector2f(1940, 40));
        items.add(mega_pulse_3);

        // invincibility
        Item invincibility_1 = new InvincibilityItem(new Vector2f(2588, 3854));
        items.add(invincibility_1);

        // EMP
        Item emp_1 = new EMPItem(new Vector2f(1940, 3887));
        items.add(emp_1);


        /* ---------- SETUP TELEPORT CIRCLES ---------- */
        TeleportCircle teleport_circle_1 = new TeleportCircle(new Vector2f(2760, 765));
        teleport_circles.add(teleport_circle_1);

        TeleportCircle teleport_circle_2 = new TeleportCircle(new Vector2f(3820, 3832));
        teleport_circles.add(teleport_circle_2);

        TeleportCircle teleport_circle_3 = new TeleportCircle(new Vector2f(2280, 2080));
        teleport_circles.add(teleport_circle_3);

        TeleportCircle teleport_circle_4 = new TeleportCircle(new Vector2f(180, 172));
        teleport_circles.add(teleport_circle_4);


        /* ---------- SETUP HEALTH CIRCLES ---------- */
        HealthCircle health_circle_1 = new HealthCircle(new Vector2f(3795, 160));
        health_circles.add(health_circle_1);


        /* ---------- SETUP ENEMY ENTITIES NOT MOVING ---------- */
        MovableEntity enemy_cannon_tank_1 = new CannonTank(new Vector2f(1760, 2050), true, false);
        enemy_cannon_tank_1.setRotation(225);
        enemy_cannon_tank_1.setAsMandatory();
        all_hostile_entities.add(enemy_cannon_tank_1);

        MovableEntity enemy_cannon_tank_2 = new CannonTank(new Vector2f(1710, 2000), true, false);
        enemy_cannon_tank_2.setRotation(225);
        enemy_cannon_tank_2.setAsMandatory();
        all_hostile_entities.add(enemy_cannon_tank_2);

        MovableEntity enemy_rocket_tank_1 = new RocketTank(new Vector2f(2585, 1991), true, false);
        all_hostile_entities.add(enemy_rocket_tank_1);

        MovableEntity enemy_rocket_robot_1 = new RocketRobot(new Vector2f(2694, 557), true, false);
        enemy_rocket_robot_1.setRotation(110);
        enemy_rocket_robot_1.setAsMandatory();
        all_hostile_entities.add(enemy_rocket_robot_1);

        MovableEntity enemy_rocket_robot_2 = new RocketRobot(new Vector2f(2805, 550), true, false);
        enemy_rocket_robot_2.setRotation(30);
        enemy_rocket_robot_2.setAsMandatory();
        all_hostile_entities.add(enemy_rocket_robot_2);


        /* ---------- SETUP ENEMY ENTITIES THAT ARE MOVING ---------- */

        // moving rocket tanks

        // TOP LEFT
        // create the waypoint list for the 2 rocket tanks that move counter-clockwise
        final List<Vector2f> wayPoints_rocket_tank_counter_clockwise = new ArrayList<>();
        wayPoints_rocket_tank_counter_clockwise.add(new Vector2f(407, 328));
        wayPoints_rocket_tank_counter_clockwise.add(new Vector2f(269, 579));
        wayPoints_rocket_tank_counter_clockwise.add(new Vector2f(269, 1008));
        wayPoints_rocket_tank_counter_clockwise.add(new Vector2f(283, 1346));
        wayPoints_rocket_tank_counter_clockwise.add(new Vector2f(850, 1346));
        wayPoints_rocket_tank_counter_clockwise.add(new Vector2f(1298, 1170));
        wayPoints_rocket_tank_counter_clockwise.add(new Vector2f(1550, 973));
        wayPoints_rocket_tank_counter_clockwise.add(new Vector2f(1500, 345));
        wayPoints_rocket_tank_counter_clockwise.add(new Vector2f(1362, 180));
        wayPoints_rocket_tank_counter_clockwise.add(new Vector2f(688, 180));

        MovableEntity enemy_moving_rocket_tank_1 = new RocketTank(new Vector2f(600, 200), true, false);
        enemy_moving_rocket_tank_1.setRotation(270);
        enemy_moving_rocket_tank_1.addWayPoints(new WayPointManager(enemy_moving_rocket_tank_1.getPosition(),
                enemy_moving_rocket_tank_1.getRotation(),
                wayPoints_rocket_tank_counter_clockwise,
                0));
        enemy_moving_rocket_tank_1.setAsMandatory();
        all_hostile_entities.add(enemy_moving_rocket_tank_1);

        MovableEntity enemy_moving_rocket_tank_2 = new RocketTank(new Vector2f(283, 1326), true, false);
        enemy_moving_rocket_tank_2.setRotation(180);
        enemy_moving_rocket_tank_2.addWayPoints(new WayPointManager(enemy_moving_rocket_tank_2.getPosition(),
                enemy_moving_rocket_tank_2.getRotation(),
                wayPoints_rocket_tank_counter_clockwise,
                3));
        enemy_moving_rocket_tank_2.setAsMandatory();
        all_hostile_entities.add(enemy_moving_rocket_tank_2);

        // create the waypoint list for the 4 rocket tanks that move clockwise
        final List<Vector2f> wayPoints_rocket_tank_clockwise = new ArrayList<>();
        wayPoints_rocket_tank_clockwise.add(new Vector2f(839, 372));
        wayPoints_rocket_tank_clockwise.add(new Vector2f(1046, 448));
        wayPoints_rocket_tank_clockwise.add(new Vector2f(1200, 695));
        wayPoints_rocket_tank_clockwise.add(new Vector2f(1043, 978));
        wayPoints_rocket_tank_clockwise.add(new Vector2f(694, 1059));
        wayPoints_rocket_tank_clockwise.add(new Vector2f(483, 852));
        wayPoints_rocket_tank_clockwise.add(new Vector2f(515, 487));

        MovableEntity enemy_moving_rocket_tank_3 = new RocketTank(new Vector2f(710, 1059), true, false);
        enemy_moving_rocket_tank_3.setRotation(270);
        enemy_moving_rocket_tank_3.addWayPoints(new WayPointManager(enemy_moving_rocket_tank_3.getPosition(),
                enemy_moving_rocket_tank_3.getRotation(),
                wayPoints_rocket_tank_clockwise,
                4));
        enemy_moving_rocket_tank_3.setAsMandatory();
        all_hostile_entities.add(enemy_moving_rocket_tank_3);

        MovableEntity enemy_moving_rocket_tank_4 = new RocketTank(new Vector2f(483, 865), true, false);
        enemy_moving_rocket_tank_4.addWayPoints(new WayPointManager(enemy_moving_rocket_tank_4.getPosition(),
                enemy_moving_rocket_tank_4.getRotation(),
                wayPoints_rocket_tank_clockwise,
                5));
        enemy_moving_rocket_tank_4.setAsMandatory();
        all_hostile_entities.add(enemy_moving_rocket_tank_4);

        MovableEntity enemy_moving_rocket_tank_5 = new RocketTank(new Vector2f(500, 487), true, false);
        enemy_moving_rocket_tank_3.setRotation(90);
        enemy_moving_rocket_tank_5.addWayPoints(new WayPointManager(enemy_moving_rocket_tank_5.getPosition(),
                enemy_moving_rocket_tank_5.getRotation(),
                wayPoints_rocket_tank_clockwise,
                6));
        enemy_moving_rocket_tank_5.setAsMandatory();
        all_hostile_entities.add(enemy_moving_rocket_tank_5);

        MovableEntity enemy_moving_rocket_tank_6 = new RocketTank(new Vector2f(1200, 680), true, false);
        enemy_moving_rocket_tank_6.setRotation(180);
        enemy_moving_rocket_tank_6.addWayPoints(new WayPointManager(enemy_moving_rocket_tank_6.getPosition(),
                enemy_moving_rocket_tank_6.getRotation(),
                wayPoints_rocket_tank_clockwise,
                2));
        enemy_moving_rocket_tank_6.setAsMandatory();
        all_hostile_entities.add(enemy_moving_rocket_tank_6);

        // TOP RIGHT
        // create the waypoint list for the shell tank
        final List<Vector2f> wayPoints_shell_tank = new ArrayList<>();
        wayPoints_shell_tank.add(new Vector2f(3157, 559));
        wayPoints_shell_tank.add(new Vector2f(3263, 764));
        wayPoints_shell_tank.add(new Vector2f(3506, 810));
        wayPoints_shell_tank.add(new Vector2f(3596, 934));
        wayPoints_shell_tank.add(new Vector2f(3517, 1031));
        wayPoints_shell_tank.add(new Vector2f(3439, 1107));
        wayPoints_shell_tank.add(new Vector2f(2637, 1107));
        wayPoints_shell_tank.add(new Vector2f(2456, 1039));
        wayPoints_shell_tank.add(new Vector2f(2216, 653));
        wayPoints_shell_tank.add(new Vector2f(2321, 487));
        wayPoints_shell_tank.add(new Vector2f(2362, 374));
        wayPoints_shell_tank.add(new Vector2f(2277, 259));
        wayPoints_shell_tank.add(new Vector2f(2418, 135));
        wayPoints_shell_tank.add(new Vector2f(2527, 161));
        wayPoints_shell_tank.add(new Vector2f(2740, 313));


        MovableEntity enemy_moving_shell_tank_1 = new ShellTank(new Vector2f(3130, 559), true, false);
        enemy_moving_shell_tank_1.setRotation(90);
        enemy_moving_shell_tank_1.addWayPoints(new WayPointManager(enemy_moving_shell_tank_1.getPosition(),
                enemy_moving_shell_tank_1.getRotation(),
                wayPoints_shell_tank,
                0));
        enemy_moving_shell_tank_1.setAsMandatory();
        all_hostile_entities.add(enemy_moving_shell_tank_1);

        // BOTTOM LEFT
        // create the waypoint list for the plasma robot
        final List<Vector2f> wayPoints_plasma_robot = new ArrayList<>();
        wayPoints_plasma_robot.add(new Vector2f(915, 2331));
        wayPoints_plasma_robot.add(new Vector2f(1000, 3024));
        wayPoints_plasma_robot.add(new Vector2f(1265, 3259));
        wayPoints_plasma_robot.add(new Vector2f(1471, 3220));
        wayPoints_plasma_robot.add(new Vector2f(1588, 3056));
        wayPoints_plasma_robot.add(new Vector2f(1592, 2448));
        wayPoints_plasma_robot.add(new Vector2f(1316, 2107));
        wayPoints_plasma_robot.add(new Vector2f(1032, 2083));

        MovableEntity enemy_moving_plasma_robot_1 = new PlasmaRobot(new Vector2f(1592, 2648), true, false);
        enemy_moving_plasma_robot_1.addWayPoints(new WayPointManager(enemy_moving_plasma_robot_1.getPosition(),
                enemy_moving_plasma_robot_1.getRotation(),
                wayPoints_plasma_robot,
                5));
        enemy_moving_plasma_robot_1.setAsMandatory();
        all_hostile_entities.add(enemy_moving_plasma_robot_1);


        /* ---------- SETUP ENEMY PLANES ---------- */

        // covering top right
        MovableEntity enemy_plane_1 = new ScoutJet(new Vector2f(2120, 250), true, false);
        enemy_plane_1.setRotation(90);
        enemy_plane_1.setAsMandatory();
        List<Vector2f> wayPoints_1 = new ArrayList<>();
        wayPoints_1.add(new Vector2f(2520, 250));
        wayPoints_1.add(new Vector2f(3320, 250));
        wayPoints_1.add(new Vector2f(3420, 350));
        wayPoints_1.add(new Vector2f(3420, 1150));
        wayPoints_1.add(new Vector2f(3320, 1250));
        wayPoints_1.add(new Vector2f(2520, 1250));
        wayPoints_1.add(new Vector2f(2420, 1150));
        wayPoints_1.add(new Vector2f(2420, 350));

        enemy_plane_1.addWayPoints(new WayPointManager(enemy_plane_1.getPosition(),
                enemy_plane_1.getRotation(),
                wayPoints_1));
        all_hostile_entities.add(enemy_plane_1);

        // covering top left
        MovableEntity enemy_plane_2 = new ScoutJet(new Vector2f(80, 50), true, false);
        enemy_plane_2.setRotation(90);
        enemy_plane_2.setAsMandatory();
        List<Vector2f> wayPoints_2 = new ArrayList<>();
        wayPoints_2.add(new Vector2f(380, 50));
        wayPoints_2.add(new Vector2f(1180, 50));
        wayPoints_2.add(new Vector2f(1280, 150));
        wayPoints_2.add(new Vector2f(1280, 950));
        wayPoints_2.add(new Vector2f(1180, 1050));
        wayPoints_2.add(new Vector2f(380, 1050));
        wayPoints_2.add(new Vector2f(280, 950));
        wayPoints_2.add(new Vector2f(280, 150));

        enemy_plane_2.addWayPoints(new WayPointManager(enemy_plane_2.getPosition(),
                enemy_plane_2.getRotation(),
                wayPoints_2));
        all_hostile_entities.add(enemy_plane_2);

        // covering bottom left
        MovableEntity enemy_plane_3 = new ScoutJet(new Vector2f(480, 2020), true, false);
        enemy_plane_3.setRotation(90);
        enemy_plane_3.setAsMandatory();
        List<Vector2f> wayPoints_3 = new ArrayList<>();
        wayPoints_3.add(new Vector2f(580, 2020));
        wayPoints_3.add(new Vector2f(1380, 2020));
        wayPoints_3.add(new Vector2f(1480, 2120));
        wayPoints_3.add(new Vector2f(1480, 2920));
        wayPoints_3.add(new Vector2f(1380, 3020));
        wayPoints_3.add(new Vector2f(580, 3020));
        wayPoints_3.add(new Vector2f(480, 2920));
        wayPoints_3.add(new Vector2f(480, 2120));

        enemy_plane_3.addWayPoints(new WayPointManager(enemy_plane_3.getPosition(),
                enemy_plane_3.getRotation(),
                wayPoints_3));
        all_hostile_entities.add(enemy_plane_3);

        // covering bottom right
        MovableEntity enemy_plane_4 = new ScoutJet(new Vector2f(2220, 2020), true, false);
        enemy_plane_4.setRotation(90);
        enemy_plane_4.setAsMandatory();
        List<Vector2f> wayPoints_4 = new ArrayList<>();
        wayPoints_4.add(new Vector2f(2520, 2020));
        wayPoints_4.add(new Vector2f(3320, 2020));
        wayPoints_4.add(new Vector2f(3420, 2120));
        wayPoints_4.add(new Vector2f(3420, 2920));
        wayPoints_4.add(new Vector2f(3320, 3020));
        wayPoints_4.add(new Vector2f(2520, 3020));
        wayPoints_4.add(new Vector2f(2420, 2920));
        wayPoints_4.add(new Vector2f(2420, 2120));

        enemy_plane_4.addWayPoints(new WayPointManager(enemy_plane_4.getPosition(),
                enemy_plane_4.getRotation(),
                wayPoints_4));
        all_hostile_entities.add(enemy_plane_4);


        /* ---------- SETUP THE PLAYERS ALLIED ENTITIES ---------- */
        /* --- none --- */


        /* ---------- SETUP THE PLAYERS DRIVABLE ENTITIES ---------- */
        /* --- none --- */


        /* ---------- SETUP THE PLAYER START POSITION AND ENTITY ---------- */
        Vector2f playerStartPos = new Vector2f(130, 3860);
        NapalmTank tank = new NapalmTank(playerStartPos, false, true);


        /* ---------- DEFINE THE MAP ---------- */
        map = new TiledMap("assets/maps/level_7.tmx");


        player.init(tank);
        super.init(gameContainer, stateBasedGame);
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) {
        super.render(gameContainer, stateBasedGame, graphics);
    }

    @Override
    public int getCombatMusicIdx() {
        return 1;   // uses the same combat background music as level 2
    }

    @Override
    public int getID() {
        return ZuluAssault.LEVEL_7;
    }
}
