package level_editor.screens.elements;

import game.graphics.fonts.FontManager;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.gui.MouseOverArea;

public abstract class ToolbarElement {

    // drawing
    protected static TrueTypeFont string_drawer;
    protected int xNameString, yNameString;

    protected int xPos, yPos;
    protected String name;

    protected int width, height;

    static {
        string_drawer = FontManager.getConsoleOutputFont(true);
    }

    public ToolbarElement(String name, int startX, int startY, int width, int height) {
        this.name = name;
        this.yPos = startY;
        this.xPos = startX;
        this.width = width;
        this.height = height;
    }

    public abstract void draw(Graphics graphics);

    public abstract void update(GameContainer gc);

    public boolean isMouseOver(int mouseX, int mouseY) {
        return (mouseX > xPos && mouseX < xPos + width && mouseY > yPos && mouseY < yPos + height);
    }

    public String getName() {
        return name;
    }

}
