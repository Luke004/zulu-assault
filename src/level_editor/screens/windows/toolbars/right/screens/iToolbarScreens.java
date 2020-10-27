package level_editor.screens.windows.toolbars.right.screens;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public interface iToolbarScreens {

    void render(GameContainer gameContainer, Graphics graphics);

    void update(GameContainer gameContainer);

    void onMouseClick(int mouseX, int mouseY);

}
