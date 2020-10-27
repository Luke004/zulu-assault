package level_editor.screens.windows;

import game.graphics.fonts.FontManager;
import org.newdawn.slick.*;

/* An abstract class which draws a black window with a title in the middle of the screen */
public abstract class CenterPopupWindow extends Window {

    // config sizes
    private static final float INLINE_LINE_WIDTH = 1.f;
    private static final float OUTLINE_LINE_WIDTH = 3.f;
    private static final float RELATIVE_WIDTH = 0.5f;    // percentage of screen
    private static final float RELATIVE_HEIGHT = 0.5f;
    private static final float RELATIVE_TITLE_HEIGHT = 0.1f;    // percentage of window height
    private int absoluteTitleHeight;

    protected int startYSuper;  // tell all child classes at which y coordinate they can start drawing

    // text drawing
    private static TrueTypeFont title_string_drawer;
    private int xTitleString, yTitleString;

    private String s_title;

    protected boolean isActive;

    static {
        title_string_drawer = FontManager.getStencilMediumFont();
    }

    public CenterPopupWindow(String title, GameContainer gc) {
        super((int) (gc.getWidth() * RELATIVE_WIDTH - (gc.getWidth() * RELATIVE_WIDTH) / 2),
                (int) (gc.getHeight() * RELATIVE_HEIGHT - (gc.getHeight() * RELATIVE_HEIGHT) / 2),
                (int) (gc.getWidth() * RELATIVE_WIDTH),
                (int) (gc.getHeight() * RELATIVE_HEIGHT));
        this.s_title = title;
        // init sizes
        absoluteTitleHeight = (int) (RELATIVE_TITLE_HEIGHT * windowHeight);
        xTitleString = (int) (windowX + windowWidth / 2.f - title_string_drawer.getWidth(s_title) / 2.f);
        int yTitleStringMargin = Props.calcMargin(absoluteTitleHeight, 0.2f, 1);
        yTitleString = (int) (windowY + yTitleStringMargin + absoluteTitleHeight / 2.f - title_string_drawer.getHeight(s_title) / 2.f);
        startYSuper = (int) (windowY + absoluteTitleHeight + INLINE_LINE_WIDTH + yTitleStringMargin);
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
                windowY + absoluteTitleHeight,
                windowX + windowWidth,
                windowY + absoluteTitleHeight);
    }

    public void show() {
        this.isActive = true;
    }

}
