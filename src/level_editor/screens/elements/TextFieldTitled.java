package level_editor.screens.elements;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.gui.TextField;

public class TextFieldTitled extends TextField {

    private String title;
    private int titleHeight;
    private Font text_drawer;

    public TextFieldTitled(GUIContext container, Font font, int x, int y, int width, int height, String title) {
        super(container, font, x, y, width, height);
        this.title = title;
        this.text_drawer = font;
        titleHeight = text_drawer.getLineHeight();
    }

    @Override
    public void render(GUIContext container, Graphics g) {
        super.render(container, g);
        text_drawer.drawString(x + getWidth() / 2.f - text_drawer.getWidth(title) / 2.f, y - titleHeight, title,
                Color.lightGray);
    }

    @Override
    public int getY() {
        return this.y - titleHeight;
    }

    @Override
    public int getHeight() {
        int height = super.getHeight();
        return height + titleHeight;
    }

    @Override
    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y + titleHeight;
    }

}

