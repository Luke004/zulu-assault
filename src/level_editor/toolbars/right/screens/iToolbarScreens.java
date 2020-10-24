package level_editor.toolbars.right.screens;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

public interface iToolbarScreens {

    void render(GameContainer gameContainer, Graphics graphics);

    void update(GameContainer gameContainer, StateBasedGame stateBasedGame);

    void onMouseClick(int mouseX, int mouseY);

}
