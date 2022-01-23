package game.menu.elements;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class Buttons {

    private final Button[] buttons;
    private int currIdx;

    public Buttons(Texture active_button_texture, Texture inactive_button_texture,
                   Vector2f starting_position, String[] descriptions, boolean defaultActive, Color color) {
        Vector2f nextBtnPosition = new Vector2f(starting_position);
        int amount = descriptions.length;
        buttons = new Button[amount];
        for (int idx = 0; idx < buttons.length; ++idx) {
            buttons[idx] = new Button(active_button_texture, inactive_button_texture, new Vector2f(nextBtnPosition.x,
                    nextBtnPosition.y), descriptions[idx], defaultActive ? idx == 0 : false, color);
            nextBtnPosition.y += 32;
            // split buttons in 2 columns when there are more than 6
            if (amount > 6 && idx == amount / 2 - 1) {
                nextBtnPosition.x += 250;
                nextBtnPosition.y -= (idx + 1) * 32;
            }
        }
    }

    public Buttons(Texture active_button_texture, Texture inactive_button_texture,
                   Vector2f starting_position, String[] descriptions, boolean defaultActive) {
        this(active_button_texture, inactive_button_texture, starting_position, descriptions,defaultActive, Color.white);
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

    public Button getButtonByIdx(int idx) {
        return buttons[idx];
    }

    public Button getCurrentActiveBtn() {
        return buttons[currIdx];
    }

    public void reset() {
        this.getCurrentActiveBtn().setActive(false);
        this.currIdx = 0;
    }

    public int isClicked(int mouseX, int mouseY) {
        if (mouseX < buttons[0].base_position.x) return -1;
        if (mouseX > buttons[buttons.length - 1].base_position.x + Button.BUTTON_WIDTH) return -1;
        if (mouseY < buttons[0].base_position.y) return -1;
        if (mouseY > buttons[buttons.length - 1].base_position.y + Button.BUTTON_HEIGHT) return -1;

        for (int idx = 0; idx < buttons.length; ++idx) {
            if (buttons[idx].base_position.x < mouseX
                    && buttons[idx].base_position.x + Button.BUTTON_WIDTH > mouseX) {
                if (buttons[idx].base_position.y < mouseY
                        && buttons[idx].base_position.y + Button.BUTTON_HEIGHT > mouseY) {
                    // CLICKED
                    if (currIdx != idx) buttons[currIdx].isActive = false; // set prev clicked as inactive
                    currIdx = idx; // set new active idx
                    if (buttons[currIdx].isActive) {
                        return currIdx;
                    } else {
                        buttons[currIdx].isActive = true;
                        return -2; // btn was just set as active
                    }
                }
            }
        }
        return -1;
    }


    public static class Button extends AbstractMenuElement {

        private boolean isActive;
        private final Image inactive_button_image;
        static int BUTTON_WIDTH, BUTTON_HEIGHT;
        private Color color;

        public Button(Texture active_button_texture, Texture inactive_button_texture, Vector2f button_position,
                      String description, boolean isActive) {
            super(active_button_texture, button_position, description);
            inactive_button_image = new Image(inactive_button_texture);
            this.isActive = isActive;
            BUTTON_WIDTH = inactive_button_image.getWidth();
            BUTTON_HEIGHT = inactive_button_image.getHeight();
        }

        public Button(Texture active_button_texture, Texture inactive_button_texture, Vector2f button_position,
                      String description, boolean isActive, Color color) {
            this(active_button_texture, inactive_button_texture, button_position, description, isActive);
            this.color = color;
        }

        @Override
        public void draw() {
            if (isActive) base_image.draw(base_position.x, base_position.y);
            else inactive_button_image.draw(base_position.x, base_position.y);
            text_drawer.drawString(
                    base_position.x + ELEMENT_WIDTH + 30,
                    base_position.y + 5.f,
                    description, this.color == null ? Color.white : this.color);
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDescription() {
            return this.description;
        }

        public void changeColor(Color newColor) {
            this.color = newColor;
        }

        public void setActive(boolean value) {
            this.isActive = value;
        }

    }

}
