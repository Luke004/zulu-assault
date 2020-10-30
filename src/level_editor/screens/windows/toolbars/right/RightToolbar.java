package level_editor.screens.windows.toolbars.right;

import game.models.Element;
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
            SCREEN_ADD_ITEM = 1,
            SCREEN_ADD_ENTITY = 2,
            SCREEN_ADD_CIRCLE = 3,
            SCREEN_ADD_WAYPOINT = 4,
            SCREEN_MODIFY_ELEMENT = 5;

    public static final int STATE_ADD_ELEMENT = -1, STATE_ADD_WAYPOINT = -2, STATE_MODIFY_ELEMENT = -3;
    private int state;

    private int current_screen, prev_screen;

    public RightToolbar(LevelEditor levelEditor, int startingY, GameContainer gc) {
        super((int) (gc.getWidth() - gc.getWidth() * TOOLBAR_WIDTH_PERCENTAGE),
                startingY,
                (int) (gc.getWidth() * TOOLBAR_WIDTH_PERCENTAGE),
                gc.getHeight()
        );

        // init all toolbar screens
        screens = new iToolbarScreens[6];
        screens[SCREEN_ADD_ELEMENT] = new SpecifyElement(this, levelEditor);
        screens[SCREEN_ADD_ITEM] = new ItemAdder(this, levelEditor);
        screens[SCREEN_ADD_ENTITY] = new EntityAdder(this, levelEditor);
        screens[SCREEN_ADD_CIRCLE] = new CircleAdder(this, levelEditor);
        screens[SCREEN_ADD_WAYPOINT] = new WaypointAdder(this, levelEditor);
        screens[SCREEN_MODIFY_ELEMENT] = new ElementModifier(this, levelEditor);

        setScreen(SCREEN_MODIFY_ELEMENT);
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

    public void setScreen(int screenID) {
        prev_screen = current_screen;
        current_screen = screenID;

        // set the state
        if (current_screen == SCREEN_MODIFY_ELEMENT) {
            state = STATE_MODIFY_ELEMENT;
        } else if (current_screen == SCREEN_ADD_WAYPOINT) {
            state = STATE_ADD_WAYPOINT;
            setWaypointListStatus(WaypointAdder.Status.EMPTY);
        } else {
            state = STATE_ADD_ELEMENT;
        }
    }

    public int getState() {
        return state;
    }

    public void goToLastScreen() {
        current_screen = prev_screen;
    }

    public void notifyForModification(Element element) {
        ((ElementModifier) screens[current_screen]).setupElement(element);
    }

    public void setWaypointListStatus(WaypointAdder.Status status) {
        ((WaypointAdder) screens[current_screen]).setStatus(status);
    }

}
