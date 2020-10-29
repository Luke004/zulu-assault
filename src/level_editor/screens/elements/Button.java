package level_editor.screens.elements;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class Button extends ToolbarElement {

    // default button margins
    public static final float RELATIVE_MARGIN_Y = 0.4f;     // 40% margin
    public static final float RELATIVE_MARGIN_X = 0.2f;

    private boolean mouseOver;

    public Button(String name, int startX, int startY, int width, int height) {
        super(name, startX, startY, width, height);
        this.xNameString = (int) (xPos + width / 2.f - string_drawer.getWidth(name) / 2.f);
        this.yNameString = (int) (yPos + height / 2.f - string_drawer.getHeight(name) / 2.f);
    }

    @Override
    public void draw(Graphics graphics) {
        graphics.setColor(Color.lightGray);
        if (mouseOver) {
            graphics.fillRect(xPos, yPos, width, height);
            graphics.setColor(Color.black);
        }
        graphics.drawRect(xPos, yPos, width, height);

        string_drawer.drawString(xNameString, yNameString, name, mouseOver ? Color.black : Color.lightGray);
    }

    @Override
    public void update(GameContainer gc) {
        mouseOver = isMouseOver(gc.getInput().getMouseX(), gc.getInput().getMouseY());
    }

    public void setYPos(int yPos) {
        this.yPos = yPos;
        this.yNameString = (int) (yPos + height / 2.f - string_drawer.getHeight(name) / 2.f);
    }

}
