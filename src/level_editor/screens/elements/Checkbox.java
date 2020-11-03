package level_editor.screens.elements;

import game.audio.MenuSounds;
import level_editor.screens.windows.Window;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import settings.UserSettings;

public class Checkbox extends ToolbarElement {

    private int innerCheckboxSize, outerCheckboxSize;
    private int innerCheckboxX, innerCheckboxY;
    private int outerCheckboxY;

    private boolean isChecked, isDisabled;

    private static Color lightGreyWithOpacity = new Color(0.7f, 0.7f, 0.7f, 0.5f);

    public Checkbox(String name, int startX, int startY, int width, int height) {
        super(name, startX, startY, width, height);
        this.innerCheckboxSize = (int) (height / 2.5f);
        this.outerCheckboxSize = (int) (height / 1.5f);
        this.outerCheckboxY = (int) (yPos + height / 2.f - outerCheckboxSize / 2.f);
        this.innerCheckboxX = (int) (xPos + outerCheckboxSize / 2.f - innerCheckboxSize / 2.f) + 1; // +1 = line width
        this.innerCheckboxY = (int) (outerCheckboxY + outerCheckboxSize / 2.f - innerCheckboxSize / 2.f) + 1;

        this.xNameString = (xPos + outerCheckboxSize + Window.Props.DEFAULT_MARGIN);
        this.yNameString = (int) (yPos + height / 2.f - string_drawer.getHeight(name) / 2.f);

    }

    @Override
    public void draw(Graphics graphics) {
        // draw outline
        //game.graphics.drawRect(xPos, yPos, width, height);

        // draw the check rect
        graphics.setColor(isDisabled ? lightGreyWithOpacity : Color.lightGray);
        if (isChecked) {
            graphics.fillRect(innerCheckboxX,
                    innerCheckboxY,
                    innerCheckboxSize,
                    innerCheckboxSize);
        }
        graphics.drawRect(xPos, outerCheckboxY, outerCheckboxSize, outerCheckboxSize);

        string_drawer.drawString(xNameString, yNameString, name, isDisabled ? lightGreyWithOpacity : Color.lightGray);
    }

    @Override
    public void update(GameContainer gc) {

    }

    public void toggle() {
        if (isDisabled) return;
        MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
        isChecked = !isChecked;
    }

    public void setChecked(boolean val) {
        isChecked = val;
    }

    public void setDisabled(boolean val) {
        isDisabled = val;
        if (val) isChecked = false;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

}
