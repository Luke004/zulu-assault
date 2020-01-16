package menus.menu_elements;

import org.newdawn.slick.Image;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

import java.awt.*;

public class Slider {

    private int value;
    private final int MAX_VALUE;
    private final float SLIDER_WIDTH;
    private Image slider_image, slider_value;
    private Vector2f slider_position, slider_value_position;
    private String description;
    private static TrueTypeFont ttf_string;
    private static final float x_OFFSET = 10;

    public Slider(Texture slider_texture, Texture slider_value_texture,
                  Vector2f slider_position, String description, int max_value) {
        this.slider_position = slider_position;
        this.slider_image = new Image(slider_texture);
        this.slider_value = new Image(slider_value_texture);
        this.description = description;
        this.value = max_value;
        this.MAX_VALUE = max_value;
        this.SLIDER_WIDTH = slider_image.getWidth() - x_OFFSET * 2;

        this.slider_value_position = new Vector2f(
                slider_position.x + slider_image.getWidth() - x_OFFSET,
                slider_position.y
        );
    }

    static {
        Font awtFont = new Font("DialogInput", Font.PLAIN, 12);
        ttf_string = new TrueTypeFont(awtFont, false);
    }

    public void draw() {
        slider_image.draw(slider_position.x, slider_position.y);
        slider_value.draw(slider_value_position.x, slider_value_position.y);
        ttf_string.drawString(
                slider_position.x + slider_image.getWidth() + 10,
                slider_position.y + ttf_string.getHeight() - 6.f,
                description);
    }

    public void increaseValue() {
        if (value + 1 > MAX_VALUE) return;
        value += 1;
        this.slider_value_position.x += SLIDER_WIDTH / MAX_VALUE;
    }

    public void decreaseValue() {
        if (value - 1 < 0) return;
        value -= 1;
        this.slider_value_position.x -= SLIDER_WIDTH / MAX_VALUE;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        this.slider_value_position.x = (slider_position.x + x_OFFSET) + value * (SLIDER_WIDTH / MAX_VALUE);
    }
}
