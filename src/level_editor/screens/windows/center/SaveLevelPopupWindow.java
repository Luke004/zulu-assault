package level_editor.screens.windows.center;

import game.graphics.fonts.FontManager;
import level_editor.screens.windows.CenterWindow;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.gui.TextField;

public class SaveLevelWindow extends CenterWindow {

    private static TrueTypeFont textFieldFont;

    private TextField[] textFields;

    static {
        textFieldFont = FontManager.getConsoleOutputFont(false);
    }

    public SaveLevelWindow(String title, GameContainer gc) {
        super(title, gc);

        textFields = new TextField[3];

        // title text field
        textFields[0] = new TextField(gc, textFieldFont, windowX + Props.DEFAULT_MARGIN, startYSuper, 100, 20);
        textFields[0].setMaxLength(20);

        // briefing message text field
        textFields[1] = new TextField(gc, textFieldFont, windowX + Props.DEFAULT_MARGIN, startYSuper, 100, 20);
        textFields[1].setMaxLength(1000);

        // debriefing message text field
        textFields[2] = new TextField(gc, textFieldFont, windowX + Props.DEFAULT_MARGIN, startYSuper, 100, 20);
        textFields[2].setMaxLength(1000);

        for (int i = 0; i < textFields.length; ++i) {
            textFields[i].setLocation(windowX + Props.DEFAULT_MARGIN, startYSuper + i * (20 + Props.DEFAULT_MARGIN * 2));
            textFields[i].setConsumeEvents(true);
            textFields[i].setBackgroundColor(Color.lightGray);
            textFields[i].setBorderColor(Color.darkGray);
        }

    }

    @Override
    public void draw(GameContainer gc, Graphics graphics) {
        super.draw(gc, graphics);
        if (!show) return;
        for (TextField textField : textFields) {
            textField.render(gc, graphics);
        }

    }

    @Override
    public void update(GameContainer gc) {

    }

    @Override
    public void onMouseClick(int button, int mouseX, int mouseY) {

    }
}
