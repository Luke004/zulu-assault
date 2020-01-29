package menus;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public abstract class AbstractMenuScreen implements iMenuScreen {

    protected BasicGameState gameState;

    public AbstractMenuScreen(BasicGameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        if (gameContainer.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
            onMouseClick(gameContainer, stateBasedGame,
                    gameContainer.getInput().getMouseX(),
                    gameContainer.getInput().getMouseY());
        }
    }
}
