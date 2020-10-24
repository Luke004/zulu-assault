package level_editor.toolbars.elements;

import graphics.fonts.FontManager;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;

public class Button {

    // default button margins
    public static final float RELATIVE_MARGIN_Y = 0.4f;     // 40% margin
    public static final float RELATIVE_MARGIN_X = 0.2f;

    // drawing
    private static TrueTypeFont button_string_drawer = FontManager.getConsoleOutputFont(true);
    private int xNameString, yNameString;

    private int xPos, yPos;
    private String name;

    private int width, height;

    private boolean mouseOver;

    public Button(String name, int startX, int startY, int width, int height) {
        this.name = name;
        this.yPos = startY;
        this.xPos = startX;
        this.width = width;
        this.height = height;
        this.xNameString = (int) (xPos + width / 2.f - button_string_drawer.getWidth(name) / 2.f);
        this.yNameString = (int) (yPos + height / 2.f - button_string_drawer.getHeight(name) / 2.f);
    }

    public void draw(Graphics graphics) {
        graphics.setColor(Color.lightGray);
        if (mouseOver) {
            graphics.fillRect(xPos, yPos, width, height);
            graphics.setColor(Color.black);
        }
        graphics.drawRect(xPos, yPos, width, height);

        button_string_drawer.drawString(xNameString, yNameString, name, mouseOver ? Color.black : Color.lightGray);
    }

    public void update(GameContainer gc) {
        mouseOver = isMouseOver(gc.getInput().getMouseX(), gc.getInput().getMouseY());
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return (mouseX > xPos && mouseX < xPos + width && mouseY > yPos && mouseY < yPos + height);
    }

    public String getName() {
        return name;
    }

}
