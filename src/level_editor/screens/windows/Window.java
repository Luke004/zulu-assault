package level_editor.screens.windows;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public abstract class Window {

    protected int windowX;     // starting value of the window's X coordinate
    protected int windowY;
    protected int windowHeight;  // absolute height of the window
    protected int windowWidth;

    public Window(int windowX, int windowY, int windowWidth, int windowHeight) {
        this.windowX = windowX;
        this.windowY = windowY;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
    }

    /* for windows that have their height based on how much elements it has */
    public Window(int windowX, int windowWidth) {
        this.windowX = windowX;
        this.windowWidth = windowWidth;
    }

    public void draw(GameContainer gc, Graphics graphics) {
        // background
        graphics.setColor(Color.black);
        graphics.fillRect(windowX, windowY, windowWidth, windowHeight);
    }

    public abstract void update(GameContainer gc);

    public boolean isMouseOver(int mouseX, int mouseY) {
        return (mouseX > windowX && mouseX < windowX + windowWidth && mouseY > windowY && mouseY < windowY + windowHeight);
    }

    public abstract void onMouseClick(int button, int mouseX, int mouseY);

    public int getWidth() {
        return windowWidth;
    }

    public int getHeight() {
        return windowHeight;
    }

    public int getX() {
        return windowX;
    }

    public int getY() {
        return windowY;
    }


    // screen properties
    public static class Props {

        public static int DEFAULT_MARGIN;

        public static int calcRectSize(int size, float relative_margin, int amount_of_rects) {
            int absoluteMargin = calcMargin(size, relative_margin, amount_of_rects);
            return ((size - (amount_of_rects + 1) * absoluteMargin) / amount_of_rects);
        }

        public static int calcMargin(int size, float relative_margin, int amount_of_rects) {
            return (int) (size / (amount_of_rects + 1) * relative_margin);
        }

        /* this procedure creates a responsive default margin for all windows */
        public static void initMargin(int size) {
            DEFAULT_MARGIN = (int) (size * 0.01f);
        }

    }

}
