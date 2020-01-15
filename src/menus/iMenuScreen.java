package menus;

import menus.menu_elements.Arrow;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

public interface iMenuScreen {

    void render(GameContainer gameContainer);

    void onUpKeyPress(GameContainer gameContainer, StateBasedGame stateBasedGame);

    void onDownKeyPress(GameContainer gameContainer, StateBasedGame stateBasedGame);

    void onEnterKeyPress(GameContainer gameContainer, StateBasedGame stateBasedGame);

    void onLeftKeyPress(GameContainer gameContainer, StateBasedGame stateBasedGame);

    void onRightKeyPress(GameContainer gameContainer, StateBasedGame stateBasedGame);
}
