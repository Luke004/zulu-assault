package level_editor.screens.windows.toolbars.right;

import level_editor.LevelEditor;
import level_editor.screens.windows.Window;
import level_editor.screens.windows.toolbars.right.screens.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;


public class RightToolbar extends Window {

    private static final float TOOLBAR_WIDTH_PERCENTAGE = 0.2f;     // size in % of screen width

    // ALL DIFFERENT SCREENS OF THE TOOLBAR
    private iToolbarScreens[] screens;

    public static final int SCREEN_ADD_ELEMENT = 0,
            SCREEN_SELECT_ITEM = 1,
            SCREEN_SELECT_ENTITY = 2,
            SCREEN_SELECT_CIRCLE = 3,
            SCREEN_MODIFY_ELEMENT = 4;

    private int current_screen, prev_screen;

    public RightToolbar(LevelEditor levelEditor, int startingY, GameContainer gc) {
        super((int) (gc.getWidth() - gc.getWidth() * TOOLBAR_WIDTH_PERCENTAGE),
                startingY,
                (int) (gc.getWidth() * TOOLBAR_WIDTH_PERCENTAGE),
                gc.getHeight()
        );

        // init all toolbar screens
        screens = new iToolbarScreens[5];
        screens[SCREEN_ADD_ELEMENT] = new SpecifyElement(this);
        screens[SCREEN_SELECT_ITEM] = new ItemSelector(this, levelEditor);
        screens[SCREEN_SELECT_ENTITY] = new EntitySelector(this, levelEditor);
        screens[SCREEN_SELECT_CIRCLE] = new CircleSelector(this, levelEditor);
        screens[SCREEN_MODIFY_ELEMENT] = new ElementModifier(this);

        setScreen(SCREEN_ADD_ELEMENT);
    }

    @Override
    public void onMouseClick(int button, int mouseX, int mouseY) {
        if (button == Input.MOUSE_LEFT_BUTTON) {
            screens[current_screen].onMouseClick(mouseX, mouseY);
        }
    }

    @Override
    public void draw(GameContainer gc, Graphics graphics) {
        super.draw(gc, graphics);
        graphics.setColor(Color.lightGray);
        graphics.drawLine(windowX, windowY, gc.getWidth() - windowWidth, windowHeight);

        screens[current_screen].render(gc, graphics);
    }

    @Override
    public void update(GameContainer gc) {
        screens[current_screen].update(gc);
    }

    public void setScreen(int stateID) {
        prev_screen = current_screen;
        current_screen = stateID;
    }

    public void goToLastScreen() {
        current_screen = prev_screen;
    }

}
