package menus;

import main.ZuluAssault;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class MainMenu extends BasicGameState {

    private String m_message;

    public MainMenu() {

    }

    @Override
    public int getID() {
        return ZuluAssault.MAIN_MENU;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        m_message = "Press ENTER to start level 1";
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) {
        graphics.drawString(m_message,
                gameContainer.getWidth() / 2.f,
                gameContainer.getHeight() / 2.f);
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) {
        if (gameContainer.getInput().isKeyPressed(Input.KEY_ENTER)) {
            stateBasedGame.enterState(ZuluAssault.LEVEL_1);
        }
    }
}
