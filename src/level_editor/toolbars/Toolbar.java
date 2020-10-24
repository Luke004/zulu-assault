package level_editor.toolbars;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public abstract class Toolbar {

    protected int toolbarX;     // starting value of the toolbar's X coordinate
    protected int toolbarY;
    protected int toolbarHeight;  // absolute height of the toolbar
    protected int toolbarWidth;

    protected void draw(GameContainer gc, Graphics graphics) {
        // background
        graphics.setColor(Color.black);
        graphics.fillRect(toolbarX, toolbarY, toolbarWidth, toolbarHeight);
    }

    public abstract void onMouseClick(int button, int mouseX, int mouseY);

    public int getWidth() {
        return toolbarWidth;
    }

    public int getHeight() {
        return toolbarHeight;
    }

    public float getX() {
        return toolbarX;
    }

    public float getY() {
        return toolbarY;
    }

}
