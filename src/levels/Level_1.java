package levels;

import logic.WayPointManager;
import main.ZuluAssault;
import models.interaction_circles.HealthCircle;
import models.interaction_circles.InteractionCircle;
import models.items.*;
import models.war_attenders.MovableWarAttender;
import models.war_attenders.planes.GreenEnemyPlane;
import models.war_attenders.robots.PlasmaRobot;
import models.war_attenders.soldiers.EnemySoldier;
import models.war_attenders.tanks.*;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import java.util.ArrayList;
import java.util.List;

public class Level_1 extends AbstractLevel implements GameState {

    public Level_1() {
        super();
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
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
        InteractionCircle health_circle_1 = new HealthCircle(new Vector2f(2420.f, 340.f));
        interaction_circles.add(health_circle_1);

        // SETUP ITEMS
        Item item_1 = new InvincibilityItem(new Vector2f(1000.f, 800.f));
        items.add(item_1);

        Item item_2 = new MegaPulseItem(new Vector2f(1100.f, 1000.f));
        items.add(item_2);

        Item item_3 = new EMPItem(new Vector2f(1200.f, 1000.f));
        items.add(item_3);

        Item item_4 = new GoldenWrenchItem(new Vector2f(1400.f, 1000.f));
        items.add(item_4);


        // SETUP THE PLAYER START POSITION AND WAR ATTENDER
        Vector2f playerStartPos = new Vector2f(1000, 1000);
        //Tank tank = new NapalmTank(playerStartPos, false, true);
        Tank tank = new MachineGunTank(playerStartPos, false, true);
        //Plane plane = new GreenEnemyPlane(playerStartPos, false, true);
        //Robot robot = new ShellRobot(playerStartPos, false, true);
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
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) {
        super.update(gameContainer, stateBasedGame, i);
    }

    @Override
    public int getID() {
        return ZuluAssault.LEVEL_1;
    }
}
