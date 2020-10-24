package level_editor.toolbars.right.screens;

import graphics.fonts.FontManager;
import level_editor.toolbars.right.RightToolbar;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;

public abstract class ToolbarScreen implements iToolbarScreens {

    private static final int MARGIN = 5;

    private String title_string;
    private static TrueTypeFont title_string_drawer;

    protected int startX, startY;

    private int titleStringX;

    protected RightToolbar rightToolbar;

    static {
        title_string_drawer = FontManager.getConsoleOutputFont(false);
    }

    public ToolbarScreen(RightToolbar rightToolbar, String title) {
        this.rightToolbar = rightToolbar;
        this.title_string = title;

        startX = (int) rightToolbar.getX();
        startY = (int) rightToolbar.getY() + title_string_drawer.getHeight(title_string);

        titleStringX = startX + rightToolbar.getWidth() / 2 - title_string_drawer.getWidth(title_string) / 2;
    }

    @Override
    public void render(GameContainer gameContainer, Graphics graphics) {
        title_string_drawer.drawString(titleStringX, rightToolbar.getY() + MARGIN, title_string, Color.lightGray);
    }

}
