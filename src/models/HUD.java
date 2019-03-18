package models;

import logic.ItemChangeListener;
import org.newdawn.slick.*;
import org.newdawn.slick.Image;
import player.Player;

import java.awt.Font;


public class HUD implements ItemChangeListener {
    private Player player;
    private Item[] items;
    private final int ITEM_START_Y;
    private final float FADE_SPEED;

    public HUD(Player player, GameContainer gc) {
        this.player = player;
        ITEM_START_Y = gc.getHeight() / (60 / 13);
        items = new Item[4];
        items[0] = (new Item(Player.Item.INVINCIBLE));
        items[1] = (new Item(Player.Item.EMP));
        items[2] = (new Item(Player.Item.MEGA_PULSE));
        items[3] = (new Item(Player.Item.EXPAND));

        FADE_SPEED = 0.05f; // how fast the item banner moves in and back out of the screen
    }

    public void draw() {
        for (int idx = 0; idx < items.length; ++idx) {
            items[idx].draw();
        }
    }

    public void update(int deltaTime) {
        for (int idx = 0; idx < items.length; ++idx) {
            items[idx].update(deltaTime);
        }
    }

    @Override
    public void itemAdded(int idx) {
        items[idx].isFadingIn = true;
        items[idx].isActive = true;
        items[idx].amount++;
    }

    @Override
    public void itemActivated(int idx) {
        if (--items[idx].amount == 0) {
            items[idx].isFadingOut = true;
        }
    }

    private class Item {
        private Image item_image;
        private TrueTypeFont amount_number_text;
        private int ITEM_WIDTH, ITEM_HEIGHT;
        private float xPos, yPos, test_x_pos, text_y_pos;
        private boolean isActive, isFadingIn, isFadingOut;
        private int amount;

        Item(Player.Item item) {
            try {
                switch (item) {
                    case INVINCIBLE:
                        item_image = new Image("assets/items/item_1.png");
                        ITEM_HEIGHT = item_image.getHeight();
                        yPos = ITEM_START_Y;
                        text_y_pos = yPos + ITEM_HEIGHT / 2.5f;
                        ITEM_WIDTH = item_image.getWidth();
                        break;
                    case EMP:
                        item_image = new Image("assets/items/item_2.png");
                        ITEM_HEIGHT = item_image.getHeight();
                        yPos = ITEM_START_Y + ITEM_HEIGHT;
                        text_y_pos = yPos + ITEM_HEIGHT / 2.5f;
                        ITEM_WIDTH = item_image.getWidth();
                        break;
                    case MEGA_PULSE:
                        item_image = new Image("assets/items/item_3.png");
                        ITEM_HEIGHT = item_image.getHeight();
                        yPos = ITEM_START_Y + ITEM_HEIGHT * 2;
                        text_y_pos = yPos + ITEM_HEIGHT / 2.5f;
                        ITEM_WIDTH = item_image.getWidth();
                        break;
                    case EXPAND:
                        item_image = new Image("assets/items/item_4.png");
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
            Font awtFont = new Font("Century Gothic", Font.PLAIN, 11);
            amount_number_text = new TrueTypeFont(awtFont, false);
        }

        void draw() {
            if (!isActive) return;
            item_image.draw(xPos, yPos);
            amount_number_text.drawString(test_x_pos, text_y_pos, Integer.toString(amount), Color.white);
        }

        void update(int deltaTime) {
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
}
