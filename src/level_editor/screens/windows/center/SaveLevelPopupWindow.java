package level_editor.screens.windows.center;

import game.util.LevelDataStorage;
import level_editor.LevelEditor;
import level_editor.screens.elements.Button;
import level_editor.screens.windows.CenterPopupWindow;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.gui.TextField;

public class SaveLevelPopupWindow extends CenterPopupWindow {

    private static final String title = "SAVE LEVEL";
    private TextField[] textFields;
    private Button confirmBtn;

    public SaveLevelPopupWindow(GameContainer gc, LevelEditor levelEditor) {
        super(title, gc, levelEditor);
        this.levelEditor = levelEditor;

        textFields = new TextField[3];

        // title text field
        textFields[0] = new TextField(gc, text_drawer, windowX + Props.DEFAULT_MARGIN, startYSuper, 100, 20);
        textFields[0].setMaxLength(20);

        // briefing message text field
        textFields[1] = new TextField(gc, text_drawer, windowX + Props.DEFAULT_MARGIN, startYSuper, 100, 20);
        textFields[1].setMaxLength(1000);

        // debriefing message text field
        textFields[2] = new TextField(gc, text_drawer, windowX + Props.DEFAULT_MARGIN, startYSuper, 100, 20);
        textFields[2].setMaxLength(1000);

        for (int i = 0; i < textFields.length; ++i) {
            textFields[i].setLocation(windowX + Props.DEFAULT_MARGIN, startYSuper + i * (20 + Props.DEFAULT_MARGIN * 2));
            textFields[i].setConsumeEvents(true);
            textFields[i].setBackgroundColor(Color.lightGray);
            textFields[i].setBorderColor(Color.darkGray);
        }

        confirmBtn = new Button("OK",
                windowX + Props.DEFAULT_MARGIN,
                startYSuper + textFields.length * (20 + Props.DEFAULT_MARGIN * 2),
                100,
                20
        );

    }

    @Override
    public void draw(GameContainer gc, Graphics graphics) {
        super.draw(gc, graphics);
        if (!isActive) return;
        for (TextField textField : textFields) {
            textField.render(gc, graphics);
        }
        confirmBtn.draw(graphics);

    }

    @Override
    public void update(GameContainer gc) {
        if (!this.isActive) return;
        confirmBtn.update(gc);

    }

    @Override
    public void onMouseClick(int button, int mouseX, int mouseY) {
        if (confirmBtn.isMouseOver(mouseX, mouseY)) {
            new LevelDataStorage().saveLevel(
                    levelEditor.getSimpleMapName(),
                    levelEditor.getElements(),
                    textFields[0].getText(),
                    textFields[1].getText(),
                    textFields[2].getText()
            );
            this.isActive = false;
            levelEditor.setPopupWindow(null);
        }
    }
}
