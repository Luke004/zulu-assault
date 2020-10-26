package levels;

import level_editor.util.WayPointManager;
import main.ZuluAssault;
import game.models.entities.MovableEntity;
import game.models.entities.robots.PlasmaRobot;
import game.models.entities.robots.RocketRobot;
import game.models.entities.robots.ShellRobot;
import game.models.entities.tanks.*;
import game.models.interaction_circles.HealthCircle;
import game.models.interaction_circles.TeleportCircle;
import game.models.items.GoldenWrenchItem;
import game.models.items.InvincibilityItem;
import game.models.items.Item;
import game.models.items.SilverWrenchItem;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import java.util.ArrayList;
import java.util.List;

public class Level_9 extends AbstractLevel implements GameState {

    public Level_9() {
        super();
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        if (!calledOnce) {
            mission_title = "Time to kill";
            briefing_message = "Intelligence reports some units are dug in at an orchard a few clicks away. You know" +
                    " what to do.";
            debriefing_message = "";
            calledOnce = true;
            return;
        }

        resetLevel();    // reset the level before init

        /* ---------- SETUP ITEMS ---------- */

        // silver wrenches
        Item silver_wrench_1 = new SilverWrenchItem(new Vector2f(2035, 913));
        items.add(silver_wrench_1);

        Item silver_wrench_2 = new SilverWrenchItem(new Vector2f(3900, 59));
        items.add(silver_wrench_2);

        Item silver_wrench_3 = new SilverWrenchItem(new Vector2f(3006, 46));
        items.add(silver_wrench_3);

        Item silver_wrench_4 = new SilverWrenchItem(new Vector2f(2868, 50));
        items.add(silver_wrench_4);

        Item silver_wrench_5 = new SilverWrenchItem(new Vector2f(2744, 50));
        items.add(silver_wrench_5);

        Item silver_wrench_6 = new SilverWrenchItem(new Vector2f(2608, 46));
        items.add(silver_wrench_6);

        Item silver_wrench_7 = new SilverWrenchItem(new Vector2f(2621, 273));
        items.add(silver_wrench_7);

        Item silver_wrench_8 = new SilverWrenchItem(new Vector2f(2621, 273));
        items.add(silver_wrench_8);

        Item silver_wrench_9 = new SilverWrenchItem(new Vector2f(1238, 415));
        items.add(silver_wrench_9);

        Item silver_wrench_10 = new SilverWrenchItem(new Vector2f(1128, 415));
        items.add(silver_wrench_10);

        Item silver_wrench_11 = new SilverWrenchItem(new Vector2f(963, 415));
        items.add(silver_wrench_11);

        Item silver_wrench_12 = new SilverWrenchItem(new Vector2f(40, 2785));
        items.add(silver_wrench_12);

        Item silver_wrench_13 = new SilverWrenchItem(new Vector2f(47, 2907));
        items.add(silver_wrench_13);

        Item silver_wrench_14 = new SilverWrenchItem(new Vector2f(45, 3048));
        items.add(silver_wrench_14);

        Item silver_wrench_15 = new SilverWrenchItem(new Vector2f(47, 3179));
        items.add(silver_wrench_15);

        Item silver_wrench_16 = new SilverWrenchItem(new Vector2f(3068, 3912));
        items.add(silver_wrench_16);

        Item silver_wrench_17 = new SilverWrenchItem(new Vector2f(3291, 3908));
        items.add(silver_wrench_17);

        Item silver_wrench_18 = new SilverWrenchItem(new Vector2f(3249, 3540));
        items.add(silver_wrench_18);

        Item silver_wrench_19 = new SilverWrenchItem(new Vector2f(3452, 3909));
        items.add(silver_wrench_19);

        Item silver_wrench_20 = new SilverWrenchItem(new Vector2f(3543, 3242));
        items.add(silver_wrench_20);

        Item silver_wrench_21 = new SilverWrenchItem(new Vector2f(3543, 3000));
        items.add(silver_wrench_21);

        Item silver_wrench_22 = new SilverWrenchItem(new Vector2f(3667, 2037));
        items.add(silver_wrench_22);

        Item silver_wrench_23 = new SilverWrenchItem(new Vector2f(3512, 2036));
        items.add(silver_wrench_23);

        Item silver_wrench_24 = new SilverWrenchItem(new Vector2f(3359, 2034));
        items.add(silver_wrench_24);

        Item silver_wrench_25 = new SilverWrenchItem(new Vector2f(3227, 2031));
        items.add(silver_wrench_25);

        Item silver_wrench_26 = new SilverWrenchItem(new Vector2f(3263, 3244));
        items.add(silver_wrench_26);

        Item silver_wrench_27 = new SilverWrenchItem(new Vector2f(2978, 3263));
        items.add(silver_wrench_27);

        Item silver_wrench_28 = new SilverWrenchItem(new Vector2f(2979, 2980));
        items.add(silver_wrench_28);

        Item silver_wrench_29 = new SilverWrenchItem(new Vector2f(3911, 1540));
        items.add(silver_wrench_29);

        Item silver_wrench_30 = new SilverWrenchItem(new Vector2f(3905, 1397));
        items.add(silver_wrench_30);

        Item silver_wrench_31 = new SilverWrenchItem(new Vector2f(3911, 1252));
        items.add(silver_wrench_31);

        Item silver_wrench_32 = new SilverWrenchItem(new Vector2f(3910, 1080));
        items.add(silver_wrench_32);

        Item silver_wrench_33 = new SilverWrenchItem(new Vector2f(3056, 998));
        items.add(silver_wrench_33);

        Item silver_wrench_34 = new SilverWrenchItem(new Vector2f(2889, 1553));
        items.add(silver_wrench_34);

        Item silver_wrench_35 = new SilverWrenchItem(new Vector2f(3107, 1704));
        items.add(silver_wrench_35);

        Item silver_wrench_36 = new SilverWrenchItem(new Vector2f(2692, 864));
        items.add(silver_wrench_36);

        Item silver_wrench_37 = new SilverWrenchItem(new Vector2f(2446, 305));
        items.add(silver_wrench_37);

        // golden wrenches
        Item golden_wrench_1 = new GoldenWrenchItem(new Vector2f(724, 692));
        items.add(golden_wrench_1);

        Item golden_wrench_2 = new GoldenWrenchItem(new Vector2f(841, 1548));
        items.add(golden_wrench_2);


        // mega pulse
        /* --- none --- */

        // invincibility
        InvincibilityItem invincibility_1 = new InvincibilityItem(new Vector2f(2424, 2181));
        items.add(invincibility_1);

        // EMP
        /* --- none --- */


        /* ---------- SETUP TELEPORT CIRCLES ---------- */
        TeleportCircle teleport_circle_1 = new TeleportCircle(new Vector2f(1086, 1135));
        teleport_circles.add(teleport_circle_1);

        TeleportCircle teleport_circle_2 = new TeleportCircle(new Vector2f(3892, 3899));
        teleport_circles.add(teleport_circle_2);


        /* ---------- SETUP HEALTH CIRCLES ---------- */
        HealthCircle health_circle_1 = new HealthCircle(new Vector2f(126, 3880));
        health_circles.add(health_circle_1);


        /* ---------- SETUP ENEMY ENTITIES NOT MOVING ---------- */
        MovableEntity enemy_mega_pulse_tank_1 = new MegaPulseTank(new Vector2f(494, 432), true, false);
        enemy_mega_pulse_tank_1.setRotation(144);
        enemy_mega_pulse_tank_1.setAsMandatory();
        all_hostile_entities.add(enemy_mega_pulse_tank_1);

        MovableEntity enemy_mega_pulse_tank_2 = new MegaPulseTank(new Vector2f(1181, 720), true, false);
        enemy_mega_pulse_tank_2.setRotation(73);
        enemy_mega_pulse_tank_2.setAsMandatory();
        all_hostile_entities.add(enemy_mega_pulse_tank_2);

        MovableEntity enemy_mega_pulse_tank_3 = new MegaPulseTank(new Vector2f(663, 1006), true, false);
        enemy_mega_pulse_tank_3.setRotation(25);
        enemy_mega_pulse_tank_3.setAsMandatory();
        all_hostile_entities.add(enemy_mega_pulse_tank_3);

        MovableEntity enemy_mega_pulse_tank_4 = new MegaPulseTank(new Vector2f(415, 1280), true, false);
        enemy_mega_pulse_tank_4.setAsMandatory();
        all_hostile_entities.add(enemy_mega_pulse_tank_4);


        MovableEntity enemy_shell_tank_1 = new ShellTank(new Vector2f(1288, 1278), true, false);
        enemy_shell_tank_1.setRotation(90);
        enemy_shell_tank_1.setAsMandatory();
        all_hostile_entities.add(enemy_shell_tank_1);

        MovableEntity enemy_shell_tank_2 = new ShellTank(new Vector2f(654, 1560), true, false);
        enemy_shell_tank_2.setRotation(90);
        enemy_shell_tank_2.setAsMandatory();
        all_hostile_entities.add(enemy_shell_tank_2);

        MovableEntity enemy_rocket_tank_1 = new RocketTank(new Vector2f(3000, 3551), true, false);
        enemy_rocket_tank_1.setRotation(170);
        enemy_rocket_tank_1.setAsMandatory();
        all_hostile_entities.add(enemy_rocket_tank_1);

        MovableEntity enemy_rocket_tank_2 = new RocketTank(new Vector2f(3555, 3565), true, false);
        enemy_rocket_tank_2.setRotation(45);
        enemy_rocket_tank_2.setAsMandatory();
        all_hostile_entities.add(enemy_rocket_tank_2);

        MovableEntity enemy_rocket_tank_3 = new RocketTank(new Vector2f(3285, 3012), true, false);
        enemy_rocket_tank_3.setRotation(325);
        enemy_rocket_tank_3.setAsMandatory();
        all_hostile_entities.add(enemy_rocket_tank_3);


        /* ---------- SETUP ENEMY ENTITIES THAT ARE MOVING ---------- */
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

        // starting top middle
        MovableEntity enemy_moving_shell_tank_1 = new ShellTank(new Vector2f(961, 99), true, false);
        enemy_moving_shell_tank_1.setRotation(270);
        enemy_moving_shell_tank_1.addWayPoints(new WayPointManager(enemy_moving_shell_tank_1.getPosition(),
                enemy_moving_shell_tank_1.getRotation(),
                wayPoints_shell_tanks,
                3));
        enemy_moving_shell_tank_1.setAsMandatory();
        all_hostile_entities.add(enemy_moving_shell_tank_1);

        // starting bottom left
        MovableEntity enemy_moving_shell_tank_2 = new ShellTank(new Vector2f(157, 2209), true, false);
        enemy_moving_shell_tank_2.setRotation(180);
        enemy_moving_shell_tank_2.addWayPoints(new WayPointManager(enemy_moving_shell_tank_2.getPosition(),
                enemy_moving_shell_tank_2.getRotation(),
                wayPoints_shell_tanks,
                6));
        enemy_moving_shell_tank_2.setAsMandatory();
        all_hostile_entities.add(enemy_moving_shell_tank_2);


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

        MovableEntity enemy_moving_rocket_robot_1 = new RocketRobot(new Vector2f(2630, 2177), true, false);
        enemy_moving_rocket_robot_1.setRotation(270);
        enemy_moving_rocket_robot_1.addWayPoints(new WayPointManager(enemy_moving_rocket_robot_1.getPosition(),
                enemy_moving_rocket_robot_1.getRotation(),
                wayPoints_rocket_robot,
                6));
        enemy_moving_rocket_robot_1.setAsMandatory();
        all_hostile_entities.add(enemy_moving_rocket_robot_1);


        /* ---------- SETUP ENEMY PLANES ---------- */
        /* --- none --- */


        /* ---------- SETUP THE PLAYERS ALLIED ENTITIES ---------- */
        /* --- none --- */


        /* ---------- SETUP THE ENEMIES DRIVABLE ENTITIES ---------- */
        // (this is just story and decoration in this map, they can't actually use it)
        MovableEntity enemy_drivable_napalm_tank = new NapalmTank(new Vector2f(3699, 3137), true, true);
        drivable_entities.add(enemy_drivable_napalm_tank);

        MovableEntity enemy_drivable_rocket_tank = new RocketTank(new Vector2f(3702, 3417), true, true);
        drivable_entities.add(enemy_drivable_rocket_tank);

        MovableEntity enemy_drivable_cannon_tank_1 = new CannonTank(new Vector2f(3420, 3417), true, true);
        drivable_entities.add(enemy_drivable_cannon_tank_1);

        MovableEntity enemy_drivable_cannon_tank_2 = new CannonTank(new Vector2f(3420, 3134), true, true);
        drivable_entities.add(enemy_drivable_cannon_tank_2);


        /* ---------- SETUP THE PLAYERS DRIVABLE ENTITIES ---------- */
        ShellRobot drivable_shell_robot = new ShellRobot(new Vector2f(2293, 2649), false, true);
        drivable_entities.add(drivable_shell_robot);


        /* ---------- SETUP THE PLAYER START POSITION AND ENTITY ---------- */
        Vector2f playerStartPos = new Vector2f(3775, 220);
        PlasmaRobot plasmaRobot = new PlasmaRobot(playerStartPos, false, true);


        /* ---------- DEFINE THE MAP ---------- */
        map = new TiledMap("assets/maps/level_9.tmx");


        player.init(plasmaRobot);
        super.init(gameContainer, stateBasedGame);
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) {
        super.render(gameContainer, stateBasedGame, graphics);
    }

    @Override
    public int getCombatMusicIdx() {
        return 3;   // uses the same combat background music as level 4
    }

    @Override
    public int getID() {
        return ZuluAssault.LEVEL_9;
    }
}
