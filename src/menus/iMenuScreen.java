package menus;

import menus.menu_elements.Arrow;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

public interface iMenuScreen {

    void render(GameContainer gameContainer);

    Arrow getArrow();

    void onEnterKeyPress(GameContainer gameContainer, StateBasedGame stateBasedGame);

    void onLeftKeyPress(GameContainer gameContainer, StateBasedGame stateBasedGame);

    void onRightKeyPress(GameContainer gameContainer, StateBasedGame stateBasedGame);
}
