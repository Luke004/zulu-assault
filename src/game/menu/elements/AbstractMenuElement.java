package game.menu.elements;

import game.graphics.fonts.FontManager;
import org.newdawn.slick.Image;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public abstract class AbstractMenuElement {

    private static final float x_OFFSET = 10;
    protected final float ELEMENT_WIDTH;
    protected Image base_image;
    protected Vector2f base_position;
    protected String description;
    protected static TrueTypeFont text_drawer;

    AbstractMenuElement(Texture base_image_texture, Vector2f base_position, String description) {
        this.base_position = base_position;
        this.base_image = new Image(base_image_texture);
        ELEMENT_WIDTH = base_image.getWidth() - x_OFFSET * 2;
        this.description = description;
    }

    static {
        text_drawer = FontManager.getConsoleOutputFont(false);
    }

    public void draw() {
        base_image.draw(base_position.x, base_position.y);

        text_drawer.drawString(
                base_position.x + ELEMENT_WIDTH + 30,
                base_position.y + 10.f,
                description);
    }

    public Vector2f getPosition() {
        return base_position;
    }

}
