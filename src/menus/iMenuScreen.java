package menus;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

public interface iMenuScreen {

    void render(GameContainer gameContainer);

    void update(GameContainer gameContainer, StateBasedGame stateBasedGame);

    void onUpKeyPress(GameContainer gameContainer);

    void onDownKeyPress(GameContainer gameContainer);

    void onEnterKeyPress(GameContainer gameContainer, StateBasedGame stateBasedGame);

    void onExitKeyPress(GameContainer gameContainer, StateBasedGame stateBasedGame);

    void onMouseClick(GameContainer gameContainer, StateBasedGame stateBasedGame, int mouseX, int mouseY);

    void onEnterState(GameContainer gameContainer);

    void onLeaveState(GameContainer gameContainer);
}
