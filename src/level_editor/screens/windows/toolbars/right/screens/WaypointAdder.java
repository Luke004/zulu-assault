package level_editor.screens.windows.toolbars.right.screens;

import game.audio.MenuSounds;
import game.graphics.fonts.FontManager;
import level_editor.LevelEditor;
import level_editor.screens.elements.Button;
import level_editor.screens.windows.Window;
import level_editor.screens.windows.toolbars.right.RightToolbar;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;
import settings.UserSettings;


public class WaypointAdder extends ToolbarScreen {

    private static final String title = "ADD WAYPOINTS";
    LevelEditor levelEditor;
    private Button[] buttons;

    private int listInfoStringX, listInfoStringY;
    private int statusStringX, statusStringY;
    private static TrueTypeFont status_string_drawer;
    private String s_status;
    private Color statusColor;
    private static final Color green = new Color(70, 153, 79);
    private static final Color red = new Color(179, 21, 16);

    static {
        status_string_drawer = FontManager.getConsoleOutputFont(true);
    }

    public WaypointAdder(RightToolbar rightToolbar, LevelEditor levelEditor) {
        super(rightToolbar, title);
        this.levelEditor = levelEditor;

        final int BUTTON_AMOUNT = 3;
        // define the area that the buttons can spread on the y-axis
        final int BUTTON_AREA_HEIGHT = (int) (rightToolbar.getHeight() / 5.33f);

        int button_width_margin = Window.Props.calcMargin(rightToolbar.getWidth(), 0.2f, 1);
        int button_width = Window.Props.calcRectSize(rightToolbar.getWidth(), 0.2f, 1);
        int button_height_margin = Window.Props.calcMargin(BUTTON_AREA_HEIGHT, 0.4f, BUTTON_AMOUNT);
        int button_height = Window.Props.calcRectSize(BUTTON_AREA_HEIGHT, 0.4f, BUTTON_AMOUNT);

        final String s_currentList = "Current list:";
        listInfoStringX = (int) (startX + rightToolbar.getWidth() / 2.f - title_string_drawer.getWidth(s_currentList) / 2.f);
        listInfoStringY = startY;

        setStatus(Status.EMPTY);
        statusStringY = listInfoStringY + title_string_drawer.getLineHeight();

        startY = statusStringY + status_string_drawer.getLineHeight() + Window.Props.DEFAULT_MARGIN * 2;

        buttons = new Button[BUTTON_AMOUNT];
        buttons[0] = new Button("NEW",
                startX + button_width_margin,
                startY,
                button_width,
                button_height
        );

        buttons[1] = new Button("UNDO",
                startX + button_width_margin,
                startY + button_height + button_height_margin,
                button_width,
                button_height
        );

        buttons[2] = new Button("BACK",
                startX + button_width_margin,
                startY + (button_height + button_height_margin) * 3,
                button_width,
                button_height
        );

    }

    @Override
    public void render(GameContainer gc, Graphics graphics) {
        super.render(gc, graphics);
        for (Button button : buttons) {
            button.draw(graphics);
        }
        title_string_drawer.drawString(listInfoStringX, listInfoStringY, "Current List:", Color.lightGray);
        status_string_drawer.drawString(statusStringX, statusStringY, this.s_status, statusColor);
    }

    @Override
    public void update(GameContainer gc) {
        for (Button button : buttons) {
            button.update(gc);
        }
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY) {
        for (Button b : buttons) {
            if (b.isMouseOver(mouseX, mouseY)) {
                MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
                switch (b.getName()) {
                    case "UNDO":
                        levelEditor.removeLastWaypoint();
                        return;
                    case "NEW":
                        levelEditor.clearCurrentWaypointList();
                        return;
                    case "BACK":
                        levelEditor.setPlacingWaypoints(false);
                        rightToolbar.setScreen(RightToolbar.SCREEN_ADD_ELEMENT);
                        return;
                }
            }
        }
    }

    public void setStatus(Status status) {
        switch (status) {
            case EMPTY:
                this.s_status = "EMPTY";
                this.statusColor = Color.lightGray;
                break;
            case NOT_CONNECTED:
                this.s_status = "NOT CONNECTED";
                this.statusColor = red;
                break;
            case SAVED:
                this.s_status = "SAVED";
                this.statusColor = green;
                break;
        }
        statusStringX = (int) (startX + rightToolbar.getWidth() / 2.f - title_string_drawer.getWidth(this.s_status) / 2.f);
    }

    public enum Status {
        EMPTY, NOT_CONNECTED, SAVED
    }

}
