package models.hud;

import logic.ItemChangeListener;
import org.newdawn.slick.*;
import player.Player;


public class HUD implements ItemChangeListener {
    private Item[] items;
    private HUD_Drawer HUDDrawer;

    public static int GAME_HEIGHT, GAME_WIDTH;

    public HUD(Player player, GameContainer gc) {
        GAME_HEIGHT = gc.getHeight();
        GAME_WIDTH = gc.getWidth();

        items = new Item[4];
        items[0] = (new Item(Player.Item.INVINCIBLE));
        items[1] = (new Item(Player.Item.EMP));
        items[2] = (new Item(Player.Item.MEGA_PULSE));
        items[3] = (new Item(Player.Item.EXPAND));

        // draw damage and points
        HUDDrawer = new HUD_Drawer(player);
    }

    public void draw() {
        for (int idx = 0; idx < items.length; ++idx) {
            items[idx].draw();
        }

        HUDDrawer.draw();
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
}
