package levels;

import main.ZuluAssault;
import models.war_attenders.robots.Robot;
import models.war_attenders.robots.ShellRobot;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
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
        // SETUP THE PLAYER START POSITION AND WAR ATTENDER
        Vector2f playerStartPos = new Vector2f(1000, 1000);

        Robot robot = new ShellRobot(playerStartPos, false, true);

        // DEFINE THE MAP
        map = new TiledMap("assets/maps/level_2.tmx");

        player.init(robot);

        super.init(gameContainer, stateBasedGame);
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) {
        super.render(gameContainer, stateBasedGame, graphics);
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) {
        super.update(gameContainer, stateBasedGame, i);

        if (gameContainer.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
            stateBasedGame.enterState(ZuluAssault.LEVEL_1);
        }
    }

    @Override
    public int getID() {
        return ZuluAssault.LEVEL_2;
    }
}
