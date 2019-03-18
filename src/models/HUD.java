package models;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import player.Player;

public class HUD {
    private Player player;
    private Image item1, item2, item3, item4;
    
    public HUD(Player player) {
        this.player = player;
        try {
            item1 = new Image("assets/items/item_1.png");
            item2 = new Image("assets/items/item_2.png");
            item3 = new Image("assets/items/item_3.png");
            item4 = new Image("assets/items/item_4.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }

    }

    public void draw(Graphics graphics) {

        if (player.getItems()[0] > 0) {
            item1.draw(0, 0);
        }
        /*
        item2.draw(0, 40);
        item3.draw(0, 80);
        item4.draw(0, 120);
*/
    }

    public void update(GameContainer gc, int deltaTime) {

    }
}
