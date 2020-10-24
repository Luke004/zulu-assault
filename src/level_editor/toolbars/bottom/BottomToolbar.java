package level_editor.toolbars.bottom;

import level_editor.toolbars.Toolbar;
import level_editor.toolbars.elements.Button;
import org.newdawn.slick.*;


public class BottomToolbar extends Toolbar {

    // buttons
    private Button[] buttons;

    public BottomToolbar(GameContainer gc) {
        toolbarHeight = gc.getHeight() / 17;
        toolbarWidth = gc.getWidth();
        toolbarX = 0;
        toolbarY = gc.getHeight() - toolbarHeight;

        // create the four buttons
        final int BUTTON_SIZE = 4;

        buttons = new Button[BUTTON_SIZE];
        int button_margin_x = (int) (toolbarWidth / (BUTTON_SIZE + 1) * Button.RELATIVE_MARGIN_X);
        int button_margin_y = (int) (toolbarHeight * Button.RELATIVE_MARGIN_Y);
        int button_width = (toolbarWidth - (BUTTON_SIZE + 1) * button_margin_x) / BUTTON_SIZE;
        int button_height = toolbarHeight - button_margin_y * 2;
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

    public void draw(GameContainer gc, Graphics graphics) {
        super.draw(gc, graphics);
        graphics.setColor(Color.lightGray);
        graphics.drawLine(toolbarX, toolbarY, gc.getWidth(), toolbarY);

        for (Button b : buttons) {
            b.draw(graphics);
        }
    }

    @Override
    public void onMouseClick(int button, int mouseX, int mouseY) {
        for (Button b : buttons) {
            if (b.clicked(mouseX, mouseY)) {
                System.out.println("clicked " + b.getName());
                break;
            }
        }
    }


    public int getHeight() {
        return toolbarHeight;
    }

}
