package level_editor.toolbar;

import level_editor.LevelEditor;
import level_editor.toolbar.screens.ElementModifier;
import level_editor.toolbar.screens.ElementSelector;
import level_editor.toolbar.screens.iToolbarState;
import menu.screens.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;


public class Toolbar {

    private static final float TOOLBAR_WIDTH_PERCENTAGE = 0.2f;     // size in % of screen width

    private float toolbarX;     // starting value of the toolbar's X coordinate
    private float toolbarY;
    private int toolbarWidth;     // absolute width of the toolbar

    // ALL DIFFERENT STATES OF THE TOOLBAR
    private iToolbarState[] toolbars;

    public static final int STATE_SELECT_ELEMENT = 0,
            STATE_MODIFY_ELEMENT = 1;

    private int current_state, prev_state;

    public Toolbar(LevelEditor levelEditor, int startingY, GameContainer gc) {
        toolbarY = startingY;
        toolbarX = gc.getWidth() - gc.getWidth() * TOOLBAR_WIDTH_PERCENTAGE;
        toolbarWidth = (int) (gc.getWidth() * TOOLBAR_WIDTH_PERCENTAGE);

        // init all toolbar screens
        toolbars = new iToolbarState[2];
        toolbars[STATE_SELECT_ELEMENT] = new ElementSelector(this, levelEditor);
        toolbars[STATE_MODIFY_ELEMENT] = new ElementModifier(this);

        setState(STATE_SELECT_ELEMENT);
    }

    public void update(GameContainer gc, StateBasedGame sbg, int dt) {
        if (gc.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
            toolbars[current_state].onMouseClick(gc, sbg, gc.getInput().getMouseX(), gc.getInput().getMouseY());
        }
    }

    public void draw(GameContainer gc, Graphics graphics) {
        // background
        graphics.setColor(Color.black);
        graphics.fillRect(toolbarX, toolbarY, toolbarWidth, gc.getHeight());
        graphics.setColor(Color.lightGray);
        graphics.drawLine(toolbarX, toolbarY, gc.getWidth() - toolbarWidth, gc.getHeight());

        toolbars[current_state].render(gc, graphics);

    }

    private void setState(int stateID) {
        prev_state = current_state;
        current_state = stateID;
    }


    public int getWidth() {
        return toolbarWidth;
    }

    public float getX() {
        return toolbarX;
    }

    public float getY() {
        return toolbarY;
    }

}
