package game.graphics.hud;

import game.graphics.fonts.FontManager;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import game.player.Player;

import static game.graphics.hud.HUD.GAME_HEIGHT;

class Item {
    private Image item_image;
    private TrueTypeFont item_amount_drawer;
    private static int ITEM_WIDTH;
    private float xPos, yPos, test_x_pos, text_y_pos;
    protected boolean isActive, isFadingIn, isFadingOut;
    protected int amount;

    private static final int ITEM_START_Y = GAME_HEIGHT / (60 / 13);
    private static final float FADE_SPEED = 0.05f; // how fast the item banner moves in and back out of the screen

    Item(Player.Item_e item) {
        int ITEM_HEIGHT;
        try {
            switch (item) {
                case INVINCIBILITY:
                    item_image = new Image("assets/items/invincibility/invincibility.png");
                    ITEM_HEIGHT = item_image.getHeight();
                    yPos = ITEM_START_Y;
                    text_y_pos = yPos + ITEM_HEIGHT / 2.5f;
                    ITEM_WIDTH = item_image.getWidth();
                    break;
                case EMP:
                    item_image = new Image("assets/items/emp/emp.png");
                    ITEM_HEIGHT = item_image.getHeight();
                    yPos = ITEM_START_Y + ITEM_HEIGHT;
                    text_y_pos = yPos + ITEM_HEIGHT / 2.5f;
                    ITEM_WIDTH = item_image.getWidth();
                    break;
                case MEGA_PULSE:
                    item_image = new Image("assets/items/mega_pulse/mega_pulse.png");
                    ITEM_HEIGHT = item_image.getHeight();
                    yPos = ITEM_START_Y + ITEM_HEIGHT * 2;
                    text_y_pos = yPos + ITEM_HEIGHT / 2.5f;
                    ITEM_WIDTH = item_image.getWidth();
                    break;
                case EXPAND:
                    item_image = new Image("assets/items/expand/expand.png");
                    ITEM_HEIGHT = item_image.getHeight();
                    yPos = ITEM_START_Y + ITEM_HEIGHT * 3;
                    text_y_pos = yPos + ITEM_HEIGHT / 2.5f;
                    ITEM_WIDTH = item_image.getWidth();
                    break;
            }
            xPos = -ITEM_WIDTH;
            test_x_pos = ITEM_WIDTH * 1.35f;
        } catch (SlickException e) {
            e.printStackTrace();
        }
        item_amount_drawer = FontManager.getItemFont();
    }

    public void draw() {
        if (!isActive) return;
        item_image.draw(xPos, yPos);
        item_amount_drawer.drawString(test_x_pos, text_y_pos, Integer.toString(amount), org.newdawn.slick.Color.white);
    }

    public void update(int deltaTime) {
        if (!isActive) return;
        if (isFadingIn) {
            if (xPos < 0) {
                xPos += FADE_SPEED * deltaTime;
            } else {
                xPos = 0.f;
                isFadingIn = false;
            }
        } else if (isFadingOut) {
            if (xPos > -ITEM_WIDTH) {
                xPos -= FADE_SPEED * deltaTime;
            } else {
                xPos = -ITEM_WIDTH;
                isFadingOut = false;
                isActive = false;
            }
        }
    }
}
