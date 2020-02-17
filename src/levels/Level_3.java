package levels;

import logic.WayPointManager;
import main.ZuluAssault;
import models.interaction_circles.HealthCircle;
import models.interaction_circles.InteractionCircle;
import models.items.GoldenWrenchItem;
import models.items.InvincibilityItem;
import models.items.Item;
import models.items.SilverWrenchItem;
import models.war_attenders.MovableWarAttender;
import models.war_attenders.WarAttender;
import models.war_attenders.planes.GreenEnemyPlane;
import models.war_attenders.planes.Jet;
import models.war_attenders.robots.PlasmaRobot;
import models.war_attenders.robots.Robot;
import models.war_attenders.robots.RocketRobot;
import models.war_attenders.robots.ShellRobot;
import models.war_attenders.soldiers.EnemySoldier;
import models.war_attenders.soldiers.RocketSoldier;
import models.war_attenders.tanks.*;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import java.util.ArrayList;
import java.util.List;

public class Level_3 extends AbstractLevel implements GameState {

    private static Sound level2_intro_sound;
    private static Music level2_music;

    public Level_3() {
        super();
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        if (!calledOnce) {
            mission_title = "Kill the Operative";
            briefing_message = "We've got the defecting cowards convoy surrounded. Hit the convoy hard and don't " +
                    "leave any survivors.";
            debriefing_message = "We got the mechs back. Over and Out.";
            calledOnce = true;
            return;
        }

        reset();    // reset the level before init

        level_intro_sound = level2_intro_sound;
        level_music = level2_music;


        // SETUP ITEMS
        // silver wrenches
        Item silver_wrench_1 = new SilverWrenchItem(new Vector2f(3280, 2220));
        items.add(silver_wrench_1);

        Item silver_wrench_2 = new SilverWrenchItem(new Vector2f(3200, 2260));
        items.add(silver_wrench_2);

        Item silver_wrench_3 = new SilverWrenchItem(new Vector2f(2145, 2750));
        items.add(silver_wrench_3);

        Item silver_wrench_4 = new SilverWrenchItem(new Vector2f(2095, 2805));
        items.add(silver_wrench_4);

        Item silver_wrench_5 = new SilverWrenchItem(new Vector2f(2010, 2880));
        items.add(silver_wrench_5);

        Item silver_wrench_6 = new SilverWrenchItem(new Vector2f(45, 3820));
        items.add(silver_wrench_6);

        Item silver_wrench_7 = new SilverWrenchItem(new Vector2f(175, 3820));
        items.add(silver_wrench_7);

        Item silver_wrench_8 = new SilverWrenchItem(new Vector2f(170, 3900));
        items.add(silver_wrench_8);


        // golden wrenches
        Item golden_wrench_1 = new GoldenWrenchItem(new Vector2f(380, 2630));
        items.add(golden_wrench_1);

        Item golden_wrench_2 = new GoldenWrenchItem(new Vector2f(450, 2625));
        items.add(golden_wrench_2);


        // invincibility
        Item invincibility_item_1 = new InvincibilityItem(new Vector2f(1175, 1685));
        items.add(invincibility_item_1);


        // SETUP ENEMY WAR ATTENDERS
        MovableWarAttender enemy_tank_1 = new NapalmTank(new Vector2f(1703, 103), true, false);
        enemy_tank_1.setRotation(90);
        enemy_tank_1.setAsMandatory();
        hostile_movable_war_attenders.add(enemy_tank_1);

        MovableWarAttender enemy_tank_2 = new NapalmTank(new Vector2f(1850, 103), true, false);
        enemy_tank_2.setRotation(90);
        enemy_tank_2.setAsMandatory();
        hostile_movable_war_attenders.add(enemy_tank_2);

        MovableWarAttender enemy_tank_3 = new NapalmTank(new Vector2f(1965, 108), true, false);
        enemy_tank_3.setRotation(90);
        enemy_tank_3.setAsMandatory();
        hostile_movable_war_attenders.add(enemy_tank_3);

        MovableWarAttender enemy_robot_1 = new RocketRobot(new Vector2f(1850, 300), true, false);
        enemy_robot_1.setRotation(270);
        enemy_robot_1.setAsMandatory();
        hostile_movable_war_attenders.add(enemy_robot_1);

        MovableWarAttender enemy_soldier_1 = new RocketSoldier(new Vector2f(2200, 147), true);
        enemy_soldier_1.setAsMandatory();
        hostile_movable_war_attenders.add(enemy_soldier_1);

        // enemy plane
        MovableWarAttender enemy_plane_1 = new GreenEnemyPlane(new Vector2f(1368, 913), true, false);
        enemy_plane_1.setRotation(90);

        List<Vector2f> wayPoints = new ArrayList<>();
        wayPoints.add(new Vector2f(1468, 913));
        wayPoints.add(new Vector2f(1568, 1013));
        wayPoints.add(new Vector2f(1568, 1813));
        wayPoints.add(new Vector2f(1468, 1913));
        wayPoints.add(new Vector2f(668, 1913));
        wayPoints.add(new Vector2f(568, 1813));
        wayPoints.add(new Vector2f(568, 1013));
        wayPoints.add(new Vector2f(668, 913));
        enemy_plane_1.addWayPoints(new WayPointManager(enemy_plane_1.getPosition(),
                enemy_plane_1.getRotation(),
                wayPoints));
        hostile_movable_war_attenders.add(enemy_plane_1);


        // SETUP PLAYER'S DRIVABLE WAR ATTENDERS
        MovableWarAttender player_drivable_jet_1 = new Jet(new Vector2f(278, 2360), false, true);
        drivable_war_attenders.add(player_drivable_jet_1);

        MovableWarAttender player_drivable_jet_2 = new Jet(new Vector2f(372, 2360), false, true);
        drivable_war_attenders.add(player_drivable_jet_2);


        // SETUP FRIENDLY WAR ATTENDERS
        MovableWarAttender player_friendly_tank_1 = new NapalmTank(new Vector2f(3521, 3694), false, false);
        player_friendly_tank_1.setRotation(295);
        friendly_movable_war_attenders.add(player_friendly_tank_1);

        MovableWarAttender player_friendly_tank_2 = new NapalmTank(new Vector2f(3653, 3541), false, false);
        friendly_movable_war_attenders.add(player_friendly_tank_2);

        // top of map
        MovableWarAttender player_friendly_tank_3 = new RocketTank(new Vector2f(900, 40), false, false);
        player_friendly_tank_3.setRotation(110);
        friendly_movable_war_attenders.add(player_friendly_tank_3);

        MovableWarAttender player_friendly_tank_4 = new RocketTank(new Vector2f(1000, 210), false, false);
        player_friendly_tank_4.setRotation(45);
        friendly_movable_war_attenders.add(player_friendly_tank_4);

        MovableWarAttender player_friendly_tank_5 = new RocketTank(new Vector2f(2975, 200), false, false);
        player_friendly_tank_5.setRotation(300);
        friendly_movable_war_attenders.add(player_friendly_tank_5);


        // SETUP THE PLAYER START POSITION AND WAR ATTENDER
        Vector2f playerStartPos = new Vector2f(3782, 3750);

        MovableWarAttender robot = new RocketRobot(playerStartPos, false, true);

        // DEFINE THE MAP
        map = new TiledMap("assets/maps/level_3.tmx");

        player.init(robot);

        super.init(gameContainer, stateBasedGame);
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) {
        super.render(gameContainer, stateBasedGame, graphics);
    }

    @Override
    public void loadLevelMusic() {
        if (level2_intro_sound == null) {
            try {
                level2_intro_sound = new Sound("audio/music/level_3_intro.ogg");
                level2_music = new Music("audio/music/level_3.ogg");
            } catch (SlickException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getID() {
        return ZuluAssault.LEVEL_3;
    }
}
