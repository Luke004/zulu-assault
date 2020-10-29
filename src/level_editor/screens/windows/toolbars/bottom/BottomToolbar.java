package level_editor.screens.windows.toolbars.bottom;

import game.audio.MenuSounds;
import level_editor.LevelEditor;
import level_editor.screens.windows.Window;
import level_editor.screens.elements.Button;
import level_editor.screens.windows.center.ErrorPopupWindow;
import level_editor.screens.windows.center.SaveLevelPopupWindow;
import level_editor.screens.windows.toolbars.right.RightToolbar;
import main.ZuluAssault;
import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import settings.UserSettings;


public class BottomToolbar extends Window {

    private LevelEditor levelEditor;
    private RightToolbar rightToolbar;
    private SaveLevelPopupWindow saveLevelPopupWindow;
    private ErrorPopupWindow errorPopupWindow;

    // buttons
    private Button[] buttons;

    // to go back to main menu
    private StateBasedGame sbg;


    public BottomToolbar(LevelEditor levelEditor, RightToolbar rightToolbar, GameContainer gc, StateBasedGame sbg) {
        super(0, gc.getHeight() - gc.getHeight() / 17, gc.getWidth(), gc.getHeight() / 17);
        this.levelEditor = levelEditor;
        this.rightToolbar = rightToolbar;
        this.sbg = sbg;
        this.saveLevelPopupWindow = new SaveLevelPopupWindow(gc, levelEditor);
        this.errorPopupWindow = new ErrorPopupWindow(gc, levelEditor);

        // create the four buttons
        final int BUTTON_SIZE = 4;

        buttons = new Button[BUTTON_SIZE];
        int button_margin_x = Window.Props.calcMargin(windowWidth, Button.RELATIVE_MARGIN_X, BUTTON_SIZE);
        int button_margin_y = Window.Props.calcMargin(windowHeight, Button.RELATIVE_MARGIN_Y, 1);
        int button_width = Window.Props.calcRectSize(windowWidth, Button.RELATIVE_MARGIN_X, BUTTON_SIZE);
        int button_height = Window.Props.calcRectSize(windowHeight, Button.RELATIVE_MARGIN_Y, 1);
        int buttonY = windowY + button_margin_y;

        buttons[0] = new Button("ADD",
                button_margin_x,
                buttonY,
                button_width,
                button_height);

        buttons[1] = new Button("MODIFY",
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
        graphics.drawLine(windowX, windowY, gc.getWidth(), windowY);

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
                    case "MODIFY":
                        rightToolbar.setScreen(RightToolbar.SCREEN_MODIFY_ELEMENT);
                        levelEditor.setElementToPlace(null);
                        break;
                    case "SAVE":
                        if (levelEditor.getPlayerEntity() == null) {
                            this.errorPopupWindow.setTitle("NO PLAYER");
                            this.errorPopupWindow.setMessage("No player defined.");
                            this.errorPopupWindow.show();
                            levelEditor.setPopupWindow(errorPopupWindow);
                        } else {
                            saveLevelPopupWindow.show();
                            levelEditor.setPopupWindow(saveLevelPopupWindow);
                        }
                        break;
                    case "EXIT":
                        sbg.enterState(ZuluAssault.MAIN_MENU, new FadeOutTransition(), new FadeInTransition());
                        break;
                }
                break;
            }
        }
    }


    public int getHeight() {
        return windowHeight;
    }

}
