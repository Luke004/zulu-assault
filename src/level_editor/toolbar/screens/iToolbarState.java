package level_editor.toolbar.screens;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

public interface iToolbarState {

    void render(GameContainer gameContainer, Graphics graphics);

    void update(GameContainer gameContainer, StateBasedGame stateBasedGame);

    void onMouseClick(GameContainer gameContainer, StateBasedGame stateBasedGame, int mouseX, int mouseY);

}
