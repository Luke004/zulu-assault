package levels;

import logic.WayPointManager;
import main.ZuluAssault;
import models.interaction_circles.TeleportCircle;
import models.items.GoldenWrenchItem;
import models.items.Item;
import models.items.SilverWrenchItem;
import models.war_attenders.MovableWarAttender;
import models.war_attenders.planes.GreenEnemyPlane;
import models.war_attenders.planes.Helicopter;
import models.war_attenders.robots.RocketRobot;
import models.war_attenders.tanks.CannonTank;
import models.war_attenders.tanks.NapalmTank;
import models.war_attenders.tanks.RocketTank;
import models.war_attenders.tanks.ShellTank;
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
            mission_title = "Fortress";
            briefing_message = "The enemy has built a virtual mountain of defenses around its base. Your job is" +
                    " simple - go in and clean them out. Watch out for enemy A10s.";
            debriefing_message = "";
            calledOnce = true;
            return;
        }

        resetLevel();    // reset the level before init

        // SETUP ITEMS
        // silver wrenches
        Item silver_wrench_1 = new SilverWrenchItem(new Vector2f(53, 3215));
        items.add(silver_wrench_1);


        // golden wrenches
        Item golden_wrench_1 = new GoldenWrenchItem(new Vector2f(1768, 3765));
        items.add(golden_wrench_1);


        // SETUP TELEPORT CIRCLES
        TeleportCircle teleport_circle_1 = new TeleportCircle(new Vector2f(168, 1826));
        teleport_circles.add(teleport_circle_1);

        TeleportCircle teleport_circle_2 = new TeleportCircle(new Vector2f(1829, 101));
        teleport_circles.add(teleport_circle_2);


        // SETUP FRIENDLY WAR ATTENDERS
        MovableWarAttender player_friendly_tank_1 = new ShellTank(new Vector2f(3490, 345), false, false);
        player_friendly_tank_1.setRotation(225);
        friendly_movable_war_attenders.add(player_friendly_tank_1);

        MovableWarAttender player_friendly_tank_2 = new ShellTank(new Vector2f(3883, 413), false, false);
        friendly_movable_war_attenders.add(player_friendly_tank_2);


        // SETUP ENEMY WAR ATTENDERS
        MovableWarAttender enemy_tank_1 = new CannonTank(new Vector2f(1660, 1171), true, false);
        hostile_movable_war_attenders.add(enemy_tank_1);

        MovableWarAttender enemy_tank_2 = new CannonTank(new Vector2f(2521, 1112), true, false);
        hostile_movable_war_attenders.add(enemy_tank_2);

        MovableWarAttender enemy_tank_3 = new CannonTank(new Vector2f(1260, 1665), true, false);
        hostile_movable_war_attenders.add(enemy_tank_3);

        MovableWarAttender enemy_tank_4 = new CannonTank(new Vector2f(1318, 2370), true, false);
        hostile_movable_war_attenders.add(enemy_tank_4);

        MovableWarAttender enemy_tank_5 = new CannonTank(new Vector2f(1588, 2858), true, false);
        hostile_movable_war_attenders.add(enemy_tank_5);

        MovableWarAttender enemy_tank_6 = new CannonTank(new Vector2f(2140, 2825), true, false);
        hostile_movable_war_attenders.add(enemy_tank_6);

        MovableWarAttender enemy_tank_7 = new CannonTank(new Vector2f(2863, 2076), true, false);
        hostile_movable_war_attenders.add(enemy_tank_7);


        // SETUP PLAYER'S DRIVABLE WAR ATTENDERS
        MovableWarAttender player_drivable_helicopter_1 = new Helicopter(new Vector2f(3697, 182), false, true);
        drivable_war_attenders.add(player_drivable_helicopter_1);

        // SETUP THE PLAYER START POSITION AND WAR ATTENDER
        //Vector2f playerStartPos = new Vector2f(3590, 415);
        //CannonTank tank = new CannonTank(playerStartPos, false, true);

        Vector2f playerStartPos = new Vector2f(2826, 2082);
        Helicopter tank = new Helicopter(playerStartPos, false, true);
        tank.setStarting();

        // DEFINE THE MAP
        map = new TiledMap("assets/maps/level_6.tmx");

        player.init(tank);

        super.init(gameContainer, stateBasedGame);
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) {
        super.render(gameContainer, stateBasedGame, graphics);
    }

    @Override
    public int getCombatMusicIdx() {
        return 0;   // uses the same combat background music as level 1
    }

    @Override
    public int getID() {
        return ZuluAssault.LEVEL_6;
    }
}
