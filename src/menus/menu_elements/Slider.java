package menus.menu_elements;

import org.newdawn.slick.Image;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

import java.awt.*;

public class Slider {

    private Image slider_image;
    private Vector2f slider_position;
    private String description;
    private static TrueTypeFont ttf_string;

    public Slider(Texture slider_texture, Vector2f slider_position, String description) {
        this.slider_position = slider_position;
        slider_image = new Image(slider_texture);
        this.description = description;
    }

    static {
        Font awtFont = new Font("DialogInput", Font.PLAIN, 12);
        ttf_string = new TrueTypeFont(awtFont, false);
    }

    public void draw() {
        slider_image.draw(slider_position.x, slider_position.y);
        ttf_string.drawString(
                slider_position.x + slider_image.getWidth() + 10,
                slider_position.y + ttf_string.getHeight() - 7.f,
                description);
    }

}
