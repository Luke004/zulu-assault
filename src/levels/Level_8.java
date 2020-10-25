package levels;

import logic.WayPointManager;
import main.ZuluAssault;
import models.entities.MovableEntity;
import models.entities.robots.RocketRobot2;
import models.entities.robots.ShellRobot;
import models.entities.tanks.*;
import models.interaction_circles.TeleportCircle;
import models.items.*;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import java.util.ArrayList;
import java.util.List;

public class Level_8 extends AbstractLevel implements GameState {

    public Level_8() {
        super();
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        if (!calledOnce) {
            mission_title = "X-Tank";
            briefing_message = "We've caught up with the retreating units and found out why they're moving so slow. " +
                    "Their secret weapon, named the X-Tank by our intelligence, moves about 5 mph but is armed to the " +
                    "teeth. Your job is to destroy this weapon.";
            debriefing_message = "The X-Tank is destroyed.";
            calledOnce = true;
            return;
        }

        resetLevel();    // reset the level before init

        /* ---------- SETUP ITEMS ---------- */

        // silver wrenches
        Item silver_wrench_1 = new SilverWrenchItem(new Vector2f(475, 3845));
        items.add(silver_wrench_1);

        Item silver_wrench_2 = new SilverWrenchItem(new Vector2f(475, 3930));
        items.add(silver_wrench_2);

        Item silver_wrench_3 = new SilverWrenchItem(new Vector2f(590, 3930));
        items.add(silver_wrench_3);

        Item silver_wrench_4 = new SilverWrenchItem(new Vector2f(740, 3930));
        items.add(silver_wrench_4);

        Item silver_wrench_5 = new SilverWrenchItem(new Vector2f(55, 3400));
        items.add(silver_wrench_5);

        Item silver_wrench_6 = new SilverWrenchItem(new Vector2f(50, 3255));
        items.add(silver_wrench_6);

        Item silver_wrench_7 = new SilverWrenchItem(new Vector2f(55, 3130));
        items.add(silver_wrench_7);

        Item silver_wrench_8 = new SilverWrenchItem(new Vector2f(99, 2766));
        items.add(silver_wrench_8);

        Item silver_wrench_9 = new SilverWrenchItem(new Vector2f(86, 2669));
        items.add(silver_wrench_9);

        Item silver_wrench_10 = new SilverWrenchItem(new Vector2f(96, 476));
        items.add(silver_wrench_10);

        Item silver_wrench_11 = new SilverWrenchItem(new Vector2f(93, 662));
        items.add(silver_wrench_11);

        Item silver_wrench_12 = new SilverWrenchItem(new Vector2f(558, 31));
        items.add(silver_wrench_12);

        Item silver_wrench_13 = new SilverWrenchItem(new Vector2f(687, 31));
        items.add(silver_wrench_13);

        Item silver_wrench_14 = new SilverWrenchItem(new Vector2f(856, 26));
        items.add(silver_wrench_14);

        Item silver_wrench_15 = new SilverWrenchItem(new Vector2f(1469, 20));
        items.add(silver_wrench_15);

        Item silver_wrench_16 = new SilverWrenchItem(new Vector2f(1883, 384));
        items.add(silver_wrench_16);

        Item silver_wrench_17 = new SilverWrenchItem(new Vector2f(1894, 453));
        items.add(silver_wrench_17);

        Item silver_wrench_18 = new SilverWrenchItem(new Vector2f(1888, 573));
        items.add(silver_wrench_18);

        Item silver_wrench_19 = new SilverWrenchItem(new Vector2f(1888, 695));
        items.add(silver_wrench_19);


        // golden wrenches
        /* --- none --- */

        // mega pulse
        /* --- none --- */

        // invincibility
        /* --- none --- */

        // EMP
        /* --- none --- */


        /* ---------- SETUP TELEPORT CIRCLES ---------- */
        TeleportCircle teleport_circle_1 = new TeleportCircle(new Vector2f(90, 90));
        teleport_circles.add(teleport_circle_1);

        TeleportCircle teleport_circle_2 = new TeleportCircle(new Vector2f(1892, 84));
        teleport_circles.add(teleport_circle_2);

        TeleportCircle teleport_circle_3 = new TeleportCircle(new Vector2f(1814, 3867));
        teleport_circles.add(teleport_circle_3);


        /* ---------- SETUP HEALTH CIRCLES ---------- */
        /* --- none --- */


        /* ---------- SETUP ENEMY ENTITIES NOT MOVING ---------- */
        MovableEntity enemy_shell_tank = new ShellTank(new Vector2f(1085, 153), true, false);
        enemy_shell_tank.setAsMandatory();
        all_hostile_entities.add(enemy_shell_tank);

        // the "boss"
        MovableEntity enemy_x_tank = new XTank(new Vector2f(903, 2044), true, false);
        enemy_x_tank.setAsMandatory();
        all_hostile_entities.add(enemy_x_tank);


        /* ---------- SETUP ENEMY ENTITIES THAT ARE MOVING ---------- */

        // moving napalm tanks way point list
        final List<Vector2f> wayPoints_napalm_tanks = new ArrayList<>();
        wayPoints_napalm_tanks.add(new Vector2f(1317, 3206));
        wayPoints_napalm_tanks.add(new Vector2f(1832, 2619));
        wayPoints_napalm_tanks.add(new Vector2f(1832, 1214));
        wayPoints_napalm_tanks.add(new Vector2f(1232, 672));
        wayPoints_napalm_tanks.add(new Vector2f(1004, 663));
        wayPoints_napalm_tanks.add(new Vector2f(569, 915));
        wayPoints_napalm_tanks.add(new Vector2f(243, 1678));
        wayPoints_napalm_tanks.add(new Vector2f(410, 3330));


        MovableEntity enemy_napalm_tank_1 = new NapalmTank(new Vector2f(410, 3330), true, false);
        enemy_napalm_tank_1.setRotation(90);
        enemy_napalm_tank_1.addWayPoints(new WayPointManager(enemy_napalm_tank_1.getPosition(),
                enemy_napalm_tank_1.getRotation(),
                wayPoints_napalm_tanks,
                0));
        enemy_napalm_tank_1.setAsMandatory();
        all_hostile_entities.add(enemy_napalm_tank_1);

        MovableEntity enemy_napalm_tank_2 = new NapalmTank(new Vector2f(1317, 3206), true, false);
        enemy_napalm_tank_2.setRotation(90);
        enemy_napalm_tank_2.addWayPoints(new WayPointManager(enemy_napalm_tank_2.getPosition(),
                enemy_napalm_tank_2.getRotation(),
                wayPoints_napalm_tanks,
                1));
        enemy_napalm_tank_2.setAsMandatory();
        all_hostile_entities.add(enemy_napalm_tank_2);

        MovableEntity enemy_napalm_tank_3 = new NapalmTank(new Vector2f(1832, 2619), true, false);
        enemy_napalm_tank_3.addWayPoints(new WayPointManager(enemy_napalm_tank_3.getPosition(),
                enemy_napalm_tank_3.getRotation(),
                wayPoints_napalm_tanks,
                2));
        enemy_napalm_tank_3.setAsMandatory();
        all_hostile_entities.add(enemy_napalm_tank_3);

        MovableEntity enemy_napalm_tank_4 = new NapalmTank(new Vector2f(1832, 1214), true, false);
        enemy_napalm_tank_4.setRotation(270);
        enemy_napalm_tank_4.addWayPoints(new WayPointManager(enemy_napalm_tank_4.getPosition(),
                enemy_napalm_tank_4.getRotation(),
                wayPoints_napalm_tanks,
                3));
        enemy_napalm_tank_4.setAsMandatory();
        all_hostile_entities.add(enemy_napalm_tank_4);

        MovableEntity enemy_napalm_tank_5 = new NapalmTank(new Vector2f(1004, 663), true, false);
        enemy_napalm_tank_5.setRotation(270);
        enemy_napalm_tank_5.addWayPoints(new WayPointManager(enemy_napalm_tank_5.getPosition(),
                enemy_napalm_tank_5.getRotation(),
                wayPoints_napalm_tanks,
                5));
        enemy_napalm_tank_5.setAsMandatory();
        all_hostile_entities.add(enemy_napalm_tank_5);

        MovableEntity enemy_napalm_tank_6 = new NapalmTank(new Vector2f(243, 1678), true, false);
        enemy_napalm_tank_6.setRotation(180);
        enemy_napalm_tank_6.addWayPoints(new WayPointManager(enemy_napalm_tank_6.getPosition(),
                enemy_napalm_tank_6.getRotation(),
                wayPoints_napalm_tanks,
                7));
        enemy_napalm_tank_6.setAsMandatory();
        all_hostile_entities.add(enemy_napalm_tank_6);


        /* ---------- SETUP ENEMY PLANES ---------- */
        /* --- none --- */


        /* ---------- SETUP THE PLAYERS ALLIED ENTITIES ---------- */
        /* --- none --- */


        /* ---------- SETUP THE PLAYERS DRIVABLE ENTITIES ---------- */
        RocketRobot2 drivable_rocket_robot = new RocketRobot2(new Vector2f(67, 259), false, true);
        drivable_entities.add(drivable_rocket_robot);

        ShellTank drivable_shell_tank = new ShellTank(new Vector2f(1778, 3688), false, true);
        drivable_entities.add(drivable_shell_tank);


        /* ---------- SETUP THE PLAYER START POSITION AND ENTITY ---------- */
        Vector2f playerStartPos = new Vector2f(134, 3863);
        ShellRobot shellRobot = new ShellRobot(playerStartPos, false, true);


        /* ---------- DEFINE THE MAP ---------- */
        map = new TiledMap("assets/maps/level_8.tmx");


        player.init(shellRobot);
        super.init(gameContainer, stateBasedGame);
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) {
        super.render(gameContainer, stateBasedGame, graphics);
    }

    @Override
    public int getCombatMusicIdx() {
        return 2;   // uses the same combat background music as level 3
    }

    @Override
    public int getID() {
        return ZuluAssault.LEVEL_8;
    }
}
