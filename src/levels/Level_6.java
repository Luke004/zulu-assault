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
        MovableWarAttender player_friendly_tank_1 = new RocketTank(new Vector2f(1405, 2339), false, false);
        player_friendly_tank_1.setRotation(195);
        friendly_movable_war_attenders.add(player_friendly_tank_1);


        // SETUP ENEMY WAR ATTENDERS
        MovableWarAttender enemy_tank_1 = new NapalmTank(new Vector2f(1286, 1090), true, false);
        enemy_tank_1.setRotation(205);
        hostile_movable_war_attenders.add(enemy_tank_1);

        // SETUP THE PLAYER START POSITION AND WAR ATTENDER
        Vector2f playerStartPos = new Vector2f(3850, 150);
        CannonTank tank = new CannonTank(playerStartPos, false, true);

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
