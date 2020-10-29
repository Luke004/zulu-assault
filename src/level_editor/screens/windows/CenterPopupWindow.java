package level_editor.screens.windows;

import game.graphics.fonts.FontManager;
import level_editor.LevelEditor;
import org.newdawn.slick.*;

/* An abstract class which draws a black window with a title in the middle of the screen */
public abstract class CenterPopupWindow extends Window {

    protected LevelEditor levelEditor;

    // config sizes
    private static final float INLINE_LINE_WIDTH = 1.f;
    private static final float OUTLINE_LINE_WIDTH = 3.f;
    private static final float RELATIVE_WIDTH = 0.5f;    // percentage of screen
    private static final float RELATIVE_HEIGHT = 0.5f;
    private static final float RELATIVE_TITLE_HEIGHT = 0.1f;    // percentage of window height
    private int titleHeight, screenHeight;

    protected int startYSuper;  // tell all child classes at which y coordinate they can start drawing

    // text drawing
    private static TrueTypeFont title_string_drawer;
    protected static TrueTypeFont text_drawer;
    private int xTitleString, yTitleString;

    protected String s_title;

    protected boolean isActive;

    static {
        title_string_drawer = FontManager.getStencilMediumFont();
        text_drawer = FontManager.getConsoleOutputFont(false);
    }

    /* This is for child classes that have a fixed height. */
    public CenterPopupWindow(String title, GameContainer gc, LevelEditor levelEditor) {
        super((int) (gc.getWidth() * RELATIVE_WIDTH - (gc.getWidth() * RELATIVE_WIDTH) / 2),
                (int) (gc.getHeight() * RELATIVE_HEIGHT - (gc.getHeight() * RELATIVE_HEIGHT) / 2),
                (int) (gc.getWidth() * RELATIVE_WIDTH),
                (int) (gc.getHeight() * RELATIVE_HEIGHT));
        this.levelEditor = levelEditor;
        this.s_title = title;
        // init sizes
        screenHeight = gc.getHeight();
        // title
        s_title = title;
        titleHeight = (int) (RELATIVE_TITLE_HEIGHT * gc.getHeight() / 2);
        xTitleString = (int) (windowX + windowWidth / 2.f - title_string_drawer.getWidth(s_title) / 2.f);
        setTitle(title);
    }

    /* This is for child classes that have a height based on the height of their elements.
    * 'initHeight(int childHeight)' must be called from the child class that uses this constructor */
    public CenterPopupWindow(GameContainer gc, LevelEditor levelEditor) {
        this("", gc, levelEditor);
    }

    protected void initHeight(int parentHeight) {
        this.windowHeight = parentHeight + titleHeight;
        this.windowY = screenHeight / 2 - this.windowHeight / 2;
        setTitle(s_title);
    }

    public void setTitle(String title) {
        s_title = title;
        xTitleString = (int) (windowX + windowWidth / 2.f - title_string_drawer.getWidth(s_title) / 2.f);
        int yTitleStringMargin = Props.calcMargin(titleHeight, 0.2f, 1);
        yTitleString = (int) (windowY + yTitleStringMargin + titleHeight / 2.f - title_string_drawer.getHeight(s_title) / 2.f);
        startYSuper = (int) (windowY + titleHeight + INLINE_LINE_WIDTH + yTitleStringMargin);
    }

    @Override
    public void draw(GameContainer gc, Graphics graphics) {
        if (!this.isActive) return;
        super.draw(gc, graphics);
        // draw screen outline
        graphics.setLineWidth(OUTLINE_LINE_WIDTH);
        graphics.setColor(Color.lightGray);
        graphics.drawLine(windowX, windowY, windowX + windowWidth, windowY);
        graphics.drawLine(windowX, windowY + windowHeight, windowX + windowWidth, windowY + windowHeight);
        graphics.drawLine(windowX, windowY, windowX, windowY + windowHeight);
        graphics.drawLine(windowX + windowWidth, windowY, windowX + windowWidth, windowY + windowHeight);
        // draw title string
        title_string_drawer.drawString(xTitleString,
                yTitleString,
                s_title,
                Color.lightGray);
        // draw line below title string
        graphics.setLineWidth(INLINE_LINE_WIDTH);
        graphics.drawLine(windowX,
                windowY + titleHeight,
                windowX + windowWidth,
                windowY + titleHeight);
    }

    public void show() {
        this.isActive = true;
    }

}
