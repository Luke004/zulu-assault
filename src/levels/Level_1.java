package levels;

import logic.WayPointManager;
import main.ZuluAssault;
import menus.UserSettings;
import models.interaction_circles.HealthCircle;
import models.interaction_circles.InteractionCircle;
import models.items.*;
import models.war_attenders.MovableWarAttender;
import models.war_attenders.planes.GreenEnemyPlane;
import models.war_attenders.planes.Plane;
import models.war_attenders.robots.PlasmaRobot;
import models.war_attenders.robots.Robot;
import models.war_attenders.robots.ShellRobot;
import models.war_attenders.soldiers.EnemySoldier;
import models.war_attenders.tanks.*;
import models.weapons.Shell;
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
        ++init_counter;
        if (init_counter < 2) return;

        reset();    // reset the level before init

        try {
            level_intro_sound = new Sound("audio/music/level_1_intro.ogg");
            level_music = new Music("audio/music/level_1.ogg");
        } catch (SlickException e) {
            e.printStackTrace();
        }

        // SETUP ENEMY WAR ATTENDERS
        List<Vector2f> wayPoints;
        MovableWarAttender enemy_tank_1_shell = new ShellTank(new Vector2f(2100.f, 2000.f), true, false);
        enemy_tank_1_shell.setMoving(true);
        enemy_tank_1_shell.base_image.setRotation(180);
        wayPoints = new ArrayList<>();
        wayPoints.add(new Vector2f(2100.f, 2100.f));
        wayPoints.add(new Vector2f(2100.f, 2500.f));
        wayPoints.add(new Vector2f(1600.f, 2500.f));
        wayPoints.add(new Vector2f(1600.f, 2100.f));
        enemy_tank_1_shell.addWayPoints(new WayPointManager(wayPoints, enemy_tank_1_shell.position, enemy_tank_1_shell.getRotation()));
        hostile_war_attenders.add(enemy_tank_1_shell);

        MovableWarAttender enemy_tank_2_shell = new ShellTank(new Vector2f(1600.f, 2600.f), true, false);
        enemy_tank_2_shell.setMoving(true);
        wayPoints = new ArrayList<>();
        wayPoints.add(new Vector2f(1600.f, 2500.f));
        wayPoints.add(new Vector2f(1600.f, 2100.f));
        wayPoints.add(new Vector2f(2100.f, 2100.f));
        wayPoints.add(new Vector2f(2100.f, 2500.f));
        enemy_tank_2_shell.addWayPoints(new WayPointManager(wayPoints, enemy_tank_2_shell.position, enemy_tank_2_shell.getRotation()));
        hostile_war_attenders.add(enemy_tank_2_shell);

/*
        MovableWarAttender enemy_plane_1 = new GreenEnemyPlane(new Vector2f(1000.f, 1000.f), true, false);
        enemy_plane_1.current_health = 1;
        wayPoints = new ArrayList<>();
        wayPoints.add(new Vector2f(1200.f, 1000.f));
        enemy_tank_2_shell.addWayPoints(new WayPointManager(wayPoints, enemy_plane_1.position, enemy_plane_1.getRotation()));
        hostile_war_attenders.add(enemy_plane_1);
 */


        MovableWarAttender enemy_soldier_1 = new EnemySoldier(new Vector2f(1370.f, 3150.f), true);
        hostile_war_attenders.add(enemy_soldier_1);

        MovableWarAttender enemy_soldier_2 = new EnemySoldier(new Vector2f(1473.f, 3198.f), true);
        hostile_war_attenders.add(enemy_soldier_2);

        MovableWarAttender enemy_soldier_3 = new EnemySoldier(new Vector2f(1266.f, 3206.f), true);
        hostile_war_attenders.add(enemy_soldier_3);

        MovableWarAttender enemy_tank_3_rocket = new RocketTank(new Vector2f(2970.f, 130.f), true, false);
        enemy_tank_3_rocket.base_image.setRotation(100);
        hostile_war_attenders.add(enemy_tank_3_rocket);

        MovableWarAttender enemy_tank_4_rocket = new RocketTank(new Vector2f(3250.f, 140.f), true, false);
        enemy_tank_4_rocket.base_image.setRotation(160);
        hostile_war_attenders.add(enemy_tank_4_rocket);

        MovableWarAttender enemy_tank_5_rocket = new RocketTank(new Vector2f(3460.f, 70.f), true, false);
        enemy_tank_5_rocket.base_image.setRotation(225);
        hostile_war_attenders.add(enemy_tank_5_rocket);

        MovableWarAttender enemy_soldier_4 = new EnemySoldier(new Vector2f(2565.f, 160.f), true);
        hostile_war_attenders.add(enemy_soldier_4);

        MovableWarAttender enemy_soldier_5 = new EnemySoldier(new Vector2f(2500.f, 240.f), true);
        hostile_war_attenders.add(enemy_soldier_5);

        MovableWarAttender enemy_soldier_6 = new EnemySoldier(new Vector2f(2460.f, 60.f), true);
        hostile_war_attenders.add(enemy_soldier_6);

        MovableWarAttender enemy_soldier_7 = new EnemySoldier(new Vector2f(2350.f, 100.f), true);
        hostile_war_attenders.add(enemy_soldier_7);

        MovableWarAttender enemy_soldier_8 = new EnemySoldier(new Vector2f(2340.f, 160.f), true);
        hostile_war_attenders.add(enemy_soldier_8);


        // SETUP PLAYER'S DRIVABLE WAR ATTENDERS
        MovableWarAttender player_drivable_robot_1 = new PlasmaRobot(new Vector2f(2465.f, 170.f), false, true);
        drivable_war_attenders.add(player_drivable_robot_1);


        // SETUP PLAYER'S FRIENDLY WAR ATTENDER ALLIES
        MovableWarAttender player_ally_1 = new NapalmTank(new Vector2f(1000.f, 300.f), false, false);
        player_ally_1.setMoving(true);
        wayPoints = new ArrayList<>();
        wayPoints.add(new Vector2f(1001.f, 3000.f));
        wayPoints.add(new Vector2f(1000.f, 300.f));
        player_ally_1.addWayPoints(new WayPointManager(wayPoints, player_ally_1.position, player_ally_1.getRotation()));
        friendly_war_attenders.add(player_ally_1);


        // SETUP INTERACTION CIRCLES
        InteractionCircle health_circle_1 = new HealthCircle(new Vector2f(2380.f, 340.f));
        interaction_circles.add(health_circle_1);

        // SETUP ITEMS
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

        // invincibility items
        Item invincibility_1 = new InvincibilityItem(new Vector2f(1780, 2240));
        items.add(invincibility_1);

        // golden wrenches
        Item golden_wrench_1 = new GoldenWrenchItem(new Vector2f(55, 3890));
        items.add(golden_wrench_1);

        Item golden_wrench_2 = new GoldenWrenchItem(new Vector2f(3520, 2715));
        items.add(golden_wrench_2);


        // tests
        Item item_1 = new InvincibilityItem(new Vector2f(1000.f, 800.f));
        items.add(item_1);

        Item item_2 = new MegaPulseItem(new Vector2f(1100.f, 1000.f));
        items.add(item_2);

        Item item_3 = new EMPItem(new Vector2f(1200.f, 1000.f));
        items.add(item_3);

        Item item_4 = new GoldenWrenchItem(new Vector2f(1400.f, 1000.f));
        items.add(item_4);


        // SETUP THE PLAYER START POSITION AND WAR ATTENDER
        Vector2f playerStartPos = new Vector2f(1500, 1000);
        //Tank tank = new NapalmTank(playerStartPos, false, true);
        //Tank tank = new ShellTank(playerStartPos, false, true);
        Tank tank = new CannonTank(playerStartPos, false, true);
        tank.current_health = 1;
        Plane plane = new GreenEnemyPlane(playerStartPos, false, true);
        Robot robot = new ShellRobot(playerStartPos, false, true);
        //MovableWarAttender soldier = new PlayerSoldier(playerStartPos, false);

        // DEFINE THE MAP
        map = new TiledMap("assets/maps/level_1.tmx");

        player.init(tank);

        super.init(gameContainer, stateBasedGame);
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) {
        super.render(gameContainer, stateBasedGame, graphics);
    }

    @Override
    public int getID() {
        return ZuluAssault.LEVEL_1;
    }
}
