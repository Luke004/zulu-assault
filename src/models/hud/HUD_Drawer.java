package models.hud;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import player.Player;

import java.util.ArrayList;
import java.util.List;

import static models.hud.HUD.GAME_HEIGHT;
import static models.hud.HUD.GAME_WIDTH;

public class HUD_Drawer {
    private List<Image> number_images;
    private Image dmg_image, pts_image;
    private static final int OFFSET = 19;
    private static final int MARGIN = 10;
    private Player player;

    HUD_Drawer(Player player) {
        this.player = player;

        number_images = new ArrayList<>();
        final int NUM_AMOUNT = 10;  // 10 numbers (0-9)
        int number = 0; // start with number '0' until number '9'
        while (number < NUM_AMOUNT) {
            try {
                number_images.add(new Image("assets/hud/text/font/numbers/" + number + ".png"));
            } catch (SlickException e) {
                e.printStackTrace();
            }
            ++number;

            try {
                dmg_image = new Image("assets/hud/text/font/text/dmg.png");
                pts_image = new Image("assets/hud/text/font/text/pts.png");
            } catch (SlickException e) {
                e.printStackTrace();
            }
        }
    }

    void drawNumber(int number, int xPos, int yPos) {
        assert (number >= 0);

        int numOfDigits = getNumOfDigits(number);
        int currX = xPos;

        do {
            number_images.get(getNthDigit(number, numOfDigits)).draw(currX, yPos);
            numOfDigits--;
            currX += OFFSET;
        }
        while (numOfDigits > 0);
    }

    private static int getNthDigit(int number, int n) {
        return (int) ((number / Math.pow(10, n - 1)) % 10);
    }

    private static int getNumOfDigits(int number) {
        if (number == 0) return 1;
        return (int) (Math.log10(number) + 1);
    }

    public void draw() {
        // draw both the 'DMG' and the 'PTS' text as an image
        dmg_image.draw(MARGIN, GAME_HEIGHT - 30);
        pts_image.draw(GAME_WIDTH / 2.f + MARGIN, GAME_HEIGHT - 30);

        // draw the points
        drawNumber(player.getPoints(), GAME_WIDTH / 2 + MARGIN + 35, GAME_HEIGHT - 40);
        // draw the health
        drawNumber(player.getWarAttender().getHealth(), MARGIN + 35, GAME_HEIGHT - 40);
    }
}
