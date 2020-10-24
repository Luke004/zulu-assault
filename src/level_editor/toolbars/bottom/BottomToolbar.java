package level_editor.toolbars.bottom;

import audio.MenuSounds;
import level_editor.toolbars.Toolbar;
import level_editor.toolbars.elements.Button;
import level_editor.toolbars.right.RightToolbar;
import org.newdawn.slick.*;
import settings.UserSettings;


public class BottomToolbar extends Toolbar {

    private RightToolbar rightToolbar;

    // buttons
    private Button[] buttons;

    public BottomToolbar(RightToolbar rightToolbar, GameContainer gc) {
        this.rightToolbar = rightToolbar;
        toolbarHeight = gc.getHeight() / 17;
        toolbarWidth = gc.getWidth();
        toolbarX = 0;
        toolbarY = gc.getHeight() - toolbarHeight;

        // create the four buttons
        final int BUTTON_SIZE = 4;

        buttons = new Button[BUTTON_SIZE];
        int button_margin_x = Toolbar.Props.calcMargin(toolbarWidth, Button.RELATIVE_MARGIN_X, BUTTON_SIZE);
        int button_margin_y = Toolbar.Props.calcMargin(toolbarHeight, Button.RELATIVE_MARGIN_Y, 1);
        int button_width = Toolbar.Props.calcRectSize(toolbarWidth, Button.RELATIVE_MARGIN_X, BUTTON_SIZE);
        int button_height = Toolbar.Props.calcRectSize(toolbarHeight, Button.RELATIVE_MARGIN_Y, 1);
        int buttonY = toolbarY + button_margin_y;

        buttons[0] = new Button("ADD",
                button_margin_x,
                buttonY,
                button_width,
                button_height);

        buttons[1] = new Button("SELECT",
                button_width + button_margin_x * 2,
                buttonY,
                button_width,
                button_height);

        buttons[2] = new Button("SAVE",
                2 * button_width + button_margin_x * 3,
                buttonY,
                button_width,
                button_height);

        buttons[3] = new Button("EXIT",
                3 * button_width + button_margin_x * 4,
                buttonY,
                button_width,
                button_height);
    }

    @Override
    public void draw(GameContainer gc, Graphics graphics) {
        super.draw(gc, graphics);
        graphics.setColor(Color.lightGray);
        graphics.drawLine(toolbarX, toolbarY, gc.getWidth(), toolbarY);

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
    public void onMouseClick(int button, int mouseX, int mouseY) {
        for (Button b : buttons) {
            if (b.isMouseOver(mouseX, mouseY)) {
                MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
                switch (b.getName()) {
                    case "ADD":
                        rightToolbar.setScreen(RightToolbar.SCREEN_ADD_ELEMENT);
                        break;
                    case "SELECT":
                        rightToolbar.setScreen(RightToolbar.SCREEN_MODIFY_ELEMENT);
                        break;
                }
                break;
            }
        }
    }


    public int getHeight() {
        return toolbarHeight;
    }

}
