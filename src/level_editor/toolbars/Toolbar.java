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

    public abstract void update(GameContainer gc);

    public abstract void onMouseClick(int button, int mouseX, int mouseY);

    public int getWidth() {
        return toolbarWidth;
    }

    public int getHeight() {
        return toolbarHeight;
    }

    public int getX() {
        return toolbarX;
    }

    public int getY() {
        return toolbarY;
    }


    //toolbar properties
    public static class Props {

        public static int DEFAULT_MARGIN;

        public static int calcRectSize(int size, float relative_margin, int amount_of_rects) {
            int absoluteMargin = calcMargin(size, relative_margin, amount_of_rects);
            return ((size - (amount_of_rects + 1) * absoluteMargin) / amount_of_rects);
        }

        public static int calcMargin(int size, float relative_margin, int amount_of_rects) {
            return (int) (size / (amount_of_rects + 1) * relative_margin);
        }

        /* this procedure creates a responsive default margin for all toolbars */
        public static void initMargin(int size) {
            DEFAULT_MARGIN = (int) (size * 0.01f);
        }

    }

}
