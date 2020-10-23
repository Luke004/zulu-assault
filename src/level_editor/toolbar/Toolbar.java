package level_editor.toolbar;

import level_editor.toolbar.screens.ElementSelector;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;


public class Toolbar {

    private static final float TOOLBAR_WIDTH_PERCENTAGE = 0.2f;     // size in % of screen width

    private static float toolbarX;     // starting value of the toolbar's X coordinate
    private static float toolbarY;
    private static int toolbarWidth;     // absolute width of the toolbar

    // toolbar screens
    ElementSelector elementSelectorScreen;

    public Toolbar(int startingY, GameContainer gc) {
        toolbarY = startingY;
        toolbarX = gc.getWidth() - gc.getWidth() * TOOLBAR_WIDTH_PERCENTAGE;
        toolbarWidth = (int) (gc.getWidth() * TOOLBAR_WIDTH_PERCENTAGE);

        elementSelectorScreen = new ElementSelector(this);
    }

    public void draw(GameContainer gc, Graphics graphics) {
        // background
        graphics.setColor(Color.black);
        graphics.fillRect(toolbarX, toolbarY, toolbarWidth, gc.getHeight());
        graphics.setColor(Color.lightGray);
        graphics.drawLine(toolbarX, toolbarY, gc.getWidth() - toolbarWidth, gc.getHeight());


    }


    public int getWidth() {
        return toolbarWidth;
    }

    public float getX() {
        return toolbarX;
    }

}
