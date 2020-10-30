package level_editor.screens.windows.toolbars.right.screens;

import game.audio.MenuSounds;
import level_editor.LevelEditor;
import level_editor.screens.windows.Window;
import level_editor.screens.elements.Button;
import level_editor.screens.windows.toolbars.right.RightToolbar;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import settings.UserSettings;

public class SpecifyElement extends ToolbarScreen {

    private static final String title = "SPECIFY ELEMENT";
    private LevelEditor levelEditor;
    private Button[] buttons;

    public SpecifyElement(RightToolbar rightToolbar, LevelEditor levelEditor) {
        super(rightToolbar, title);
        this.levelEditor = levelEditor;

        final int BUTTON_AMOUNT = 4;
        // define the area that the buttons can spread on the y-axis
        final int BUTTON_AREA_HEIGHT = rightToolbar.getHeight() / 4;

        int button_width_margin = Window.Props.calcMargin(rightToolbar.getWidth(), 0.2f, 1);
        int button_width = Window.Props.calcRectSize(rightToolbar.getWidth(), 0.2f, 1);
        int button_height_margin = Window.Props.calcMargin(BUTTON_AREA_HEIGHT, 0.4f, BUTTON_AMOUNT);
        int button_height = Window.Props.calcRectSize(BUTTON_AREA_HEIGHT, 0.4f, BUTTON_AMOUNT);

        buttons = new Button[BUTTON_AMOUNT];
        buttons[0] = new Button("ITEM",
                startX + button_width_margin,
                startY,
                button_width,
                button_height);
        buttons[1] = new Button("ENTITY",
                startX + button_width_margin,
                startY + button_height + button_height_margin,
                button_width,
                button_height);
        buttons[2] = new Button("CIRCLE",
                startX + button_width_margin,
                startY + (button_height + button_height_margin) * 2,
                button_width,
                button_height);
        buttons[3] = new Button("WAYPOINT",
                startX + button_width_margin,
                startY + (button_height + button_height_margin) * 3,
                button_width,
                button_height);
    }

    @Override
    public void render(GameContainer gc, Graphics graphics) {
        super.render(gc, graphics);
        for (Button b : buttons) {
            b.draw(graphics);
        }
    }

    @Override
    public void update(GameContainer gc) {
        for (Button b : buttons) {
            b.update(gc);
        }
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY) {
        for (Button b : buttons) {
            if (b.isMouseOver(mouseX, mouseY)) {
                MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
                switch (b.getName()) {
                    case "ITEM":
                        rightToolbar.setScreen(RightToolbar.SCREEN_ADD_ITEM);
                        break;
                    case "ENTITY":
                        rightToolbar.setScreen(RightToolbar.SCREEN_ADD_ENTITY);
                        break;
                    case "CIRCLE":
                        rightToolbar.setScreen(RightToolbar.SCREEN_ADD_CIRCLE);
                        break;
                    case "WAYPOINT":
                        rightToolbar.setScreen(RightToolbar.SCREEN_ADD_WAYPOINT);
                        levelEditor.setPlacingWaypoints(true);
                        break;
                }
                break;
            }
        }
    }
}
