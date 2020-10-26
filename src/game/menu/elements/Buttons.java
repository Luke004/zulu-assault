package game.menu.elements;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class Buttons {

    private Button[] buttons;
    private int currIdx;
    private Vector2f starting_position;
    private int button_amount;

    public Buttons(int amount, boolean hasReturnButton, Texture active_button_texture, Texture inactive_button_texture,
                   Vector2f starting_position, String[] descriptions) {
        this.starting_position = new Vector2f(starting_position);
        this.button_amount = amount + (hasReturnButton ? 1 : 0);
        buttons = new Button[this.button_amount];
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

    public int isClicked(int mouseX, int mouseY) {
        if (mouseX < starting_position.x || mouseX > starting_position.x + Button.BUTTON_WIDTH)
            return -1;
        if (mouseY < starting_position.y || mouseY > starting_position.y + Button.BUTTON_HEIGHT * button_amount)
            return -1;

        int startY = (int) starting_position.y;

        for (int idx = 0; idx < button_amount; ++idx) {
            if (mouseY < startY + ((idx + 1) * Button.BUTTON_HEIGHT)) {
                if (currIdx != idx) buttons[currIdx].isActive = false;
                currIdx = idx;
                if (buttons[currIdx].isActive) {
                    return currIdx;
                } else {
                    buttons[currIdx].isActive = true;
                    return -2;
                }
            }
        }
        return -1;
    }

    private static class Button extends AbstractMenuElement {

        private boolean isActive;
        private Image inactive_button_image;
        static int BUTTON_WIDTH, BUTTON_HEIGHT;

        public Button(Texture active_button_texture, Texture inactive_button_texture, Vector2f button_position,
                      String description, boolean isActive) {
            super(active_button_texture, button_position, description);
            inactive_button_image = new Image(inactive_button_texture);
            this.isActive = isActive;
            BUTTON_WIDTH = inactive_button_image.getWidth();
            BUTTON_HEIGHT = inactive_button_image.getHeight();
        }


        @Override
        public void draw() {
            if (isActive) base_image.draw(base_position.x, base_position.y);
            else inactive_button_image.draw(base_position.x, base_position.y);
            text_drawer.drawString(
                    base_position.x + ELEMENT_WIDTH + 30,
                    base_position.y + 5.f,
                    description);
        }
    }

}
