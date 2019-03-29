package levels;

import logic.WaypointManager;
import models.interaction_circles.HealthCircle;
import models.interaction_circles.InteractionCircle;
import models.war_attenders.MovableWarAttender;
import models.war_attenders.robots.PlasmaRobot;
import models.war_attenders.robots.ShellRobot;
import models.war_attenders.soldiers.EnemySoldier;
import models.war_attenders.soldiers.PlayerSoldier;
import models.war_attenders.soldiers.RocketSoldier;
import models.war_attenders.tanks.*;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

import java.util.ArrayList;
import java.util.List;

public class Level_1 extends AbstractLevel {

    List<Vector2f> waypoints_tank_1;
    public Level_1(String title) {
        super(title);
    }

    @Override
    public void init(GameContainer gameContainer) throws SlickException {
        // SETUP ENEMY WAR ATTENDERS
        MovableWarAttender enemy_tank_1 = new ShellTank(new Vector2f(100.f, 100.f), true);
        waypoints_tank_1 = new ArrayList<>();
        waypoints_tank_1.add(new Vector2f(500.f, 500.f));
        waypoints_tank_1.add(new Vector2f(1000.f, 500.f));
        waypoints_tank_1.add(new Vector2f(1000.f, 1000.f));
        waypoints_tank_1.add(new Vector2f(500.f, 1000.f));
        enemy_tank_1.addWaypoints(new WaypointManager(waypoints_tank_1, enemy_tank_1.position, enemy_tank_1.getRotation()));
        hostile_war_attenders.add(enemy_tank_1);


        MovableWarAttender enemy_soldier_2 = new EnemySoldier(new Vector2f(2000.f, 2500.f), true);
        hostile_war_attenders.add(enemy_soldier_2);

        // SETUP PLAYER'S DRIVABLE WAR ATTENDERS
        MovableWarAttender player_drivable_tank_1 = new ShellTank(new Vector2f(700.f, 300.f), false);
        friendly_war_attenders.add(player_drivable_tank_1);

        // SETUP INTERACTION CIRCLES
        InteractionCircle health_circle_1 = new HealthCircle(new Vector2f(500.f, 500.f));
        interaction_circles.add(health_circle_1);

        // SETUP THE PLAYER START POSITION AND WAR ATTENDER
        Vector2f playerStartPos = new Vector2f(450, 250);
        MovableWarAttender tank = new MachineGunTank(playerStartPos, false);
        //MovableWarAttender soldier = new PlayerSoldier(playerStartPos, false);
        player.init(tank);

        // DEFINE THE MAP
        map = new TiledMap("assets/maps/level_1.tmx");

        super.init(gameContainer);
    }
}
