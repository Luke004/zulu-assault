package game.graphics.hud;

import game.logic.level_listeners.ChangeVehicleListener;
import game.logic.level_listeners.ItemChangeListener;
import game.models.weapons.Weapon;
import org.newdawn.slick.*;
import game.player.Player;

import java.util.List;


public class HUD implements ItemChangeListener, ChangeVehicleListener {
    private Player player;

    private Item[] items;

    private List<Weapon> weapons;
    private Image weapon_board_image;

    private HUD_Drawer HUDDrawer;

    public static int GAME_HEIGHT, GAME_WIDTH;

    public HUD(Player player, GameContainer gc) {
        this.player = player;
        this.player.addChangeVehicleListener(this);
        weapons = player.getEntity().getWeapons();
        try {
            weapon_board_image = new Image("assets/hud/weapons/weapon_board.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }

        GAME_HEIGHT = gc.getHeight();
        GAME_WIDTH = gc.getWidth();

        items = new Item[4];
        items[0] = (new Item(Player.Item_e.INVINCIBILITY));
        items[1] = (new Item(Player.Item_e.EMP));
        items[2] = (new Item(Player.Item_e.MEGA_PULSE));
        items[3] = (new Item(Player.Item_e.EXPAND));

        // draw damage and points
        HUDDrawer = new HUD_Drawer(player, gc);
    }

    public void draw(GameContainer gc) {
        weapon_board_image.draw(0, 0);

        for (int i = 0; i < weapons.size(); ++i) {
            Image image = weapons.get(i).getHUDImage();
            switch (i) {
                case 0: // weapon slot 1
                    if (image != null)
                        image.draw(14, 13);
                    break;
                case 1: // weapon slot 2
                    if (image != null)
                        image.draw(89, 13);
                    break;
            }

            //weapon.getHUDImage().draw(14, 13);
        }

        for (Item item : items) {
            item.draw();
        }

        HUDDrawer.draw(gc);
    }

    public void update(int deltaTime) {
        for (Item item : items) {
            item.update(deltaTime);
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

    @Override
    public void onPlayerChangeVehicle() {
        weapons = player.getEntity().getWeapons();
    }

    public void reset() {
        for (Item item : items) {
            item.isActive = false;
            item.amount = 0;
        }
    }
}
