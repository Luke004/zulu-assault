package level_editor.toolbar.screens;

import audio.MenuSounds;
import graphics.fonts.FontManager;
import level_editor.toolbar.Toolbar;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.StateBasedGame;
import settings.UserSettings;

public abstract class ToolbarScreen implements iToolbarState {

    private static final int MARGIN = 5;

    private String title_string;
    private static TrueTypeFont title_string_drawer;

    protected int startX, startY;

    private int titleStringX;

    protected Toolbar toolbar;

    static {
        title_string_drawer = FontManager.getConsoleOutputFont();
    }

    public ToolbarScreen(Toolbar toolbar, String title) {
        this.toolbar = toolbar;
        this.title_string = title;

        startX = (int) toolbar.getX();
        startY = (int) toolbar.getY() + title_string_drawer.getHeight(title_string);

        titleStringX = startX + toolbar.getWidth() / 2 - title_string_drawer.getWidth(title_string) / 2;
    }

    @Override
    public void render(GameContainer gameContainer, Graphics graphics) {
        title_string_drawer.drawString(titleStringX, toolbar.getY() + MARGIN, title_string, Color.lightGray);
    }

}
