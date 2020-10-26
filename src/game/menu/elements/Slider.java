package game.menu.elements;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;


public class Slider extends AbstractMenuElement {

    private int value;
    private final int MAX_VALUE;
    private Image slider_value_image;
    private Vector2f slider_image_position, slider_value_position;
    private static final float x_OFFSET = 10;

    public Slider(Texture slider_texture, Texture slider_value_texture,
                  Vector2f slider_position, String description, int max_value) {
        super(slider_texture, slider_position, description);
        this.slider_value_image = new Image(slider_value_texture);
        this.value = max_value;
        this.MAX_VALUE = max_value;

        this.slider_image_position = new Vector2f(slider_position);

        this.slider_value_position = new Vector2f(
                slider_position.x + base_image.getWidth() - x_OFFSET,
                slider_position.y
        );
    }

    public void draw() {
        super.draw();
        slider_value_image.draw(slider_value_position.x, slider_value_position.y);
    }

    public void increaseValue() {
        if (value + 1 > MAX_VALUE) return;
        value += 1;
        this.slider_value_position.x += ELEMENT_WIDTH / MAX_VALUE;
    }

    public void decreaseValue() {
        if (value - 1 < 0) return;
        value -= 1;
        this.slider_value_position.x -= ELEMENT_WIDTH / MAX_VALUE;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        this.slider_value_position.x = (base_position.x + x_OFFSET) + value * (ELEMENT_WIDTH / MAX_VALUE);
    }

    public boolean onClick(int mouseX, int mouseY) {
        if (mouseX < slider_image_position.x || mouseX > slider_image_position.x + base_image.getWidth())
            return false;
        if (mouseY < slider_image_position.y || mouseY > slider_image_position.y + base_image.getHeight())
            return false;

        // calculate relative slider value position
        int relativeMouseX = mouseX - (int) slider_image_position.x;
        int newSliderValue = relativeMouseX * MAX_VALUE / base_image.getWidth();

        setValue(newSliderValue);
        return true;
    }
}
