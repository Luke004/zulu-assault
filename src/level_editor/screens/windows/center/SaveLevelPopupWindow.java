package level_editor.screens.windows.center;

import game.audio.CombatBackgroundMusic;
import game.models.entities.Entity;
import game.util.LevelDataStorage;
import level_editor.LevelEditor;
import level_editor.screens.elements.Button;
import level_editor.screens.elements.TextFieldTitled;
import level_editor.screens.windows.CenterPopupWindow;
import level_editor.util.EditorWaypointList;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.gui.TextField;

public class SaveLevelPopupWindow extends CenterPopupWindow {

    private static final String title = "SAVE LEVEL";
    private TextFieldTitled[] textFields;
    private Button confirmBtn;

    public SaveLevelPopupWindow(GameContainer gc, LevelEditor levelEditor) {
        super(title, gc, levelEditor);
        this.levelEditor = levelEditor;

        int textFieldWidth = windowWidth - Props.DEFAULT_MARGIN * 2;
        int textFieldHeight = 20;   // TODO: 30.10.2020 add relative size

        textFields = new TextFieldTitled[4];
        // title text field
        int titleTextFieldWidth = windowWidth / 2;
        textFields[0] = new TextFieldTitled(gc, text_drawer, 0, 0, titleTextFieldWidth, textFieldHeight, "Title");
        textFields[0].setMaxLength(20);

        // briefing message text field
        textFields[1] = new TextFieldTitled(gc, text_drawer, 0, 0, textFieldWidth, textFieldHeight, "Briefing");
        textFields[1].setMaxLength(1000);

        // debriefing message text field
        textFields[2] = new TextFieldTitled(gc, text_drawer, 0, 0, textFieldWidth, textFieldHeight, "Debriefing");
        textFields[2].setMaxLength(1000);

        // music index text field
        textFields[3] = new TextFieldTitled(gc, text_drawer, 0, 0, titleTextFieldWidth / 2, textFieldHeight, "Music (1-10)");
        textFields[3].setMaxLength(2);

        for (int i = 0; i < textFields.length; ++i) {
            textFields[i].setLocation(windowX + Props.DEFAULT_MARGIN, startYSuper + i * (20 + Props.DEFAULT_MARGIN * 2));
            textFields[i].setConsumeEvents(true);
            textFields[i].setBackgroundColor(Color.lightGray);
            textFields[i].setBorderColor(Color.darkGray);
            textFields[i].setTextColor(Color.black);
        }

        int marginsHeight = 0;
        int textFieldsHeight = 0;
        marginsHeight += Props.DEFAULT_MARGIN;  // one top margin
        for (TextFieldTitled textField : textFields) {
            textFieldsHeight += textField.getHeight();
            marginsHeight += Props.DEFAULT_MARGIN;  // margin below each text field
        }

        int buttonWidth = 100;  // TODO: 30.10.2020 add relative size
        int buttonHeight = 20;
        confirmBtn = new Button("OK", windowX + windowWidth / 2 - buttonWidth / 2, 0, buttonWidth, buttonHeight);
        marginsHeight += Props.DEFAULT_MARGIN * 2;  // margin for the button (top and bottom)

        // calculate height of window
        super.initHeight(textFieldsHeight + buttonHeight + marginsHeight);

        // set y positions based on the height
        int textFieldY = startYSuper;
        for (TextField textField : textFields) {
            textField.setLocation(windowX + windowWidth / 2 - textField.getWidth() / 2, textFieldY);
            textFieldY += textField.getHeight() + Props.DEFAULT_MARGIN;
        }
        TextFieldTitled lastTextField = textFields[textFields.length - 1];
        int textFieldMaxY = lastTextField.getY() + lastTextField.getHeight();

        this.confirmBtn.setYPos(textFieldMaxY + Props.DEFAULT_MARGIN * 2);
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
                    (Entity) levelEditor.getPlayerEntity(),
                    levelEditor.getAllWayPointLists(),
                    EditorWaypointList.getEntityConnections(),
                    textFields[0].getText(),
                    textFields[1].getText(),
                    textFields[2].getText(),
                    parseMusicIdx(textFields[3].getText())
            );
            this.isActive = false;
            levelEditor.setPopupWindow(null);
        }
    }

    private static int parseMusicIdx(String s_musicIdx) {
        int i_musicIdx;
        try {
            i_musicIdx = Integer.parseInt(s_musicIdx);
        } catch (Exception e) {
            i_musicIdx = 1;
        }
        if (i_musicIdx < 1 || i_musicIdx > CombatBackgroundMusic.COMBAT_MUSIC_SIZE) i_musicIdx = 1;
        return i_musicIdx - 1;  // subtract 1 (UI uses 1-10, we use indexes 0-9)
    }

    public void fillTextFields(String title, String briefing, String debriefing, int musicIdx) {
        this.textFields[0].setText(title);
        this.textFields[1].setText(briefing);
        this.textFields[2].setText(debriefing);
        this.textFields[3].setText(Integer.toString(musicIdx + 1));
    }

}
