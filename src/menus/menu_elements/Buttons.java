package menus.menu_elements;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class Buttons {

    private Button[] buttons;
    private int currIdx;

    public Buttons(int amount, boolean hasReturnButton, Texture active_button_texture, Texture inactive_button_texture,
                   Vector2f starting_position, String[] descriptions) {

        buttons = new Button[hasReturnButton ? amount + 1 : amount];
        for (int idx = 0; idx < buttons.length; ++idx) {
            buttons[idx] = new Button(active_button_texture, inactive_button_texture, new Vector2f(starting_position.x,
                    starting_position.y),
                    descriptions[idx], idx == 0);
            starting_position.y += 32;
        }
    }

    public void draw() {
        for (Button button : buttons) {
            button.draw();
        }
    }

    public void onDownKeyPress() {
        buttons[currIdx].isActive = false;
        if (currIdx == buttons.length - 1) {
            currIdx = 0;
        } else currIdx++;
        buttons[currIdx].isActive = true;
    }

    public void onUpKeyPress() {
        buttons[currIdx].isActive = false;
        if (currIdx == 0) {
            currIdx = buttons.length - 1;
        } else currIdx--;
        buttons[currIdx].isActive = true;
    }

    public int getCurrentButtonIdx() {
        return currIdx;
    }


    private static class Button extends AbstractMenuElement {

        private boolean isActive;
        private Image inactive_button_image;

        public Button(Texture active_button_texture, Texture inactive_button_texture, Vector2f button_position,
                      String description, boolean isActive) {
            super(active_button_texture, button_position, description);
            inactive_button_image = new Image(inactive_button_texture);
            this.isActive = isActive;
        }

        @Override
        public void draw() {
            if (isActive) base_image.draw(base_position.x, base_position.y);
            else inactive_button_image.draw(base_position.x, base_position.y);
            ttf_string.drawString(
                    base_position.x + ELEMENT_WIDTH + 30,
                    base_position.y + 5.f,
                    description);
        }
    }

}
