package level_editor.toolbars.right;

import level_editor.LevelEditor;
import level_editor.toolbars.Toolbar;
import level_editor.toolbars.right.screens.ElementModifier;
import level_editor.toolbars.right.screens.ElementSelector;
import level_editor.toolbars.right.screens.iToolbarScreens;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;


public class RightToolbar extends Toolbar {

    private static final float TOOLBAR_WIDTH_PERCENTAGE = 0.2f;     // size in % of screen width

    // ALL DIFFERENT SCREENS OF THE TOOLBAR
    private iToolbarScreens[] screens;

    public static final int SCREEN_SELECT_ELEMENT = 0,
            SCREEN_MODIFY_ELEMENT = 1;

    private int current_screen, prev_screen;

    public RightToolbar(LevelEditor levelEditor, int startingY, GameContainer gc) {
        toolbarY = startingY;
        toolbarX = (int) (gc.getWidth() - gc.getWidth() * TOOLBAR_WIDTH_PERCENTAGE);
        toolbarWidth = (int) (gc.getWidth() * TOOLBAR_WIDTH_PERCENTAGE);
        toolbarHeight = gc.getHeight();

        // init all toolbar screens
        screens = new iToolbarScreens[2];
        screens[SCREEN_SELECT_ELEMENT] = new ElementSelector(this, levelEditor);
        screens[SCREEN_MODIFY_ELEMENT] = new ElementModifier(this);

        setState(SCREEN_SELECT_ELEMENT);
    }

    @Override
    public void onMouseClick(int button, int mouseX, int mouseY) {
        if (button == Input.MOUSE_LEFT_BUTTON) {
            screens[current_screen].onMouseClick(mouseX, mouseY);
        }
    }

    public void draw(GameContainer gc, Graphics graphics) {
        super.draw(gc, graphics);
        graphics.setColor(Color.lightGray);
        graphics.drawLine(toolbarX, toolbarY, gc.getWidth() - toolbarWidth, toolbarHeight);

        screens[current_screen].render(gc, graphics);
    }

    private void setState(int stateID) {
        prev_screen = current_screen;
        current_screen = stateID;
    }

}
