package levels;

import main.ZuluAssault;
import models.war_attenders.robots.Robot;
import models.war_attenders.robots.ShellRobot;
import models.war_attenders.tanks.CannonTank;
import models.war_attenders.tanks.Tank;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

public class Level_2 extends AbstractLevel implements GameState {

    public Level_2() {
        super();
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        ++init_counter;
        if (init_counter < 2) return;

        reset();    // reset the level before init

        try {
            level_intro_sound = new Sound("audio/music/level_2_intro.ogg");
            level_music = new Music("audio/music/level_2.ogg");
        } catch (SlickException e) {
            e.printStackTrace();
        }

        // SETUP THE PLAYER START POSITION AND WAR ATTENDER
        Vector2f playerStartPos = new Vector2f(3800, 3800);

        Tank tank = new CannonTank(playerStartPos, false, true);

        // DEFINE THE MAP
        map = new TiledMap("assets/maps/level_2.tmx");

        player.init(tank);

        super.init(gameContainer, stateBasedGame);
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) {
        super.render(gameContainer, stateBasedGame, graphics);
    }

    @Override
    public int getID() {
        return ZuluAssault.LEVEL_2;
    }
}
