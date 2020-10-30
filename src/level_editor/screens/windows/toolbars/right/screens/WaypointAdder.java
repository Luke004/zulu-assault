package level_editor.screens.windows.toolbars.right.screens;

import level_editor.LevelEditor;
import level_editor.screens.elements.Button;
import level_editor.screens.windows.Window;
import level_editor.screens.windows.toolbars.right.RightToolbar;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;


public class WaypointAdder extends ToolbarScreen {

    private static final String title = "ADD WAYPOINTS";
    LevelEditor levelEditor;
    private Button[] buttons;

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

        // TODO: 30.10.2020 draw current waypoint status (CLOSED, NOT_CLOSED)

        buttons = new Button[BUTTON_AMOUNT];
        buttons[0] = new Button("HELP",
                startX + button_width_margin,
                startY,
                button_width,
                button_height
        );

        buttons[1] = new Button("FINISH",
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
                switch (b.getName()) {
                    case "HELP":
                        // TODO: 30.10.2020 draw a help popup window
                        return;
                    case "FINISH":
                        //levelEditor.finishCurrentWaypointList();
                        // TODO: 30.10.2020 remove this button
                        return;
                    case "BACK":
                        // TODO: 30.10.2020 delete current waypoint list
                        rightToolbar.setScreen(RightToolbar.SCREEN_ADD_ELEMENT);
                        return;
                }
            }
        }
    }

}
