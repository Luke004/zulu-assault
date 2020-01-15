package menus.menu_elements;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;


public class Arrow {
    private static final int X_POS = 240, Y_OFFSET = 40;
    private final int Y_POS_START;
    private static Image arrow_image;

    public int currIdx;
    private final int MENU_SIZE;

    /*
    @param menuSize how many options are there in the menu
    @param y_pos_start yPos where the arrow starts at idx 0 (first menu option)
     */

    public Arrow(int menuSize, int y_pos_start) {
        this.MENU_SIZE = menuSize;
        this.Y_POS_START = y_pos_start;
        try {
            arrow_image = new Image("assets/menus/arrow.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public void draw() {
        arrow_image.draw(X_POS, Y_POS_START + currIdx * Y_OFFSET);
    }

    public void moveDown() {
        if (currIdx == MENU_SIZE - 1) {
            currIdx = 0;
        } else currIdx++;
    }

    public void moveUp() {
        if (currIdx == 0) {
            currIdx = MENU_SIZE - 1;
        } else currIdx--;
    }
}