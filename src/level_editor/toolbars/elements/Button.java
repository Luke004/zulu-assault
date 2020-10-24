package level_editor.toolbars.elements;

import graphics.fonts.FontManager;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;

public class Button {

    // default button margins
    public static final float RELATIVE_MARGIN_Y = 0.2f;     // 20% margin
    public static final float RELATIVE_MARGIN_X = 0.3f;     // 30% margin

    // drawing
    private static TrueTypeFont button_string_drawer = FontManager.getConsoleOutputFont(true);

    private int xPos, yPos;
    private String name;

    private int width, height;

    public Button(String name, int startX, int startY, int width, int height) {
        this.name = name;
        this.yPos = startY;
        this.xPos = startX;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics graphics) {
        graphics.drawRect(xPos, yPos, width, height);
        button_string_drawer.drawString(xPos + width / 2.f - button_string_drawer.getWidth(name) / 2.f, yPos, name);
    }

    public boolean clicked(int mouseX, int mouseY) {
        return (mouseX > xPos && mouseX < xPos + width && mouseY > yPos && mouseY < yPos + height);
    }

    public String getName() {
        return name;
    }

}
