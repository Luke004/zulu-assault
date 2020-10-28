package level_editor.screens.windows.toolbars.right.screens;

import game.graphics.fonts.FontManager;
import level_editor.screens.windows.Window;
import level_editor.screens.windows.toolbars.right.RightToolbar;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;


public abstract class ToolbarScreen implements iToolbarScreens {

    protected String title_string;
    protected static TrueTypeFont title_string_drawer;

    protected int startX, startY;

    protected int titleStringX, titleStringY;
    private int lineY;

    protected RightToolbar rightToolbar;

    static {
        title_string_drawer = FontManager.getConsoleOutputFont(false);
    }

    public ToolbarScreen(RightToolbar rightToolbar, String title) {
        this.rightToolbar = rightToolbar;
        this.title_string = title;

        lineY = rightToolbar.getY() + title_string_drawer.getHeight(title_string) + Window.Props.DEFAULT_MARGIN;

        startX = rightToolbar.getX();
        startY = lineY + Window.Props.DEFAULT_MARGIN * 2;

        titleStringX = startX + rightToolbar.getWidth() / 2 - title_string_drawer.getWidth(title_string) / 2;
        titleStringY = (rightToolbar.getY() + (lineY - rightToolbar.getY()) / 2 - title_string_drawer.getHeight(title_string) / 2);
    }

    @Override
    public void render(GameContainer gameContainer, Graphics graphics) {
        title_string_drawer.drawString(titleStringX, titleStringY, title_string, Color.lightGray);
        graphics.drawLine(startX, lineY, gameContainer.getWidth(), lineY);
    }

}
