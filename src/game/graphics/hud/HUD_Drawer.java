package game.graphics.hud;

import game.graphics.fonts.FontManager;
import game.levels.LevelManager;
import game.util.TimeManager;
import org.newdawn.slick.*;
import game.player.Player;
import settings.UserSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static game.graphics.hud.HUD.GAME_HEIGHT;
import static game.graphics.hud.HUD.GAME_WIDTH;

public class HUD_Drawer {

    private List<Image> number_images;
    private Image dmg_image, pts_image;
    private static final int OFFSET = 19;
    private static final int MARGIN = 10;
    private Player player;

    private TrueTypeFont time_text, time_text_header, fps_drawer;

    private static int fps_drawer_x, fps_drawer_y;

    HUD_Drawer(Player player, GameContainer gc) {
        this.player = player;

        this.time_text = FontManager.getStencilSmallerFont();
        this.time_text_header = FontManager.getStencilSmallFont();
        this.fps_drawer = FontManager.getConsoleOutputFont(false);

        fps_drawer_x = gc.getWidth() - fps_drawer.getWidth("FPS: 000") - 2;
        fps_drawer_y = gc.getHeight() - fps_drawer.getHeight("F") - 2;

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

    public void draw(GameContainer gc) {
        // draw both the 'DMG' and the 'PTS' text as an image
        dmg_image.draw(MARGIN, GAME_HEIGHT - 30);
        pts_image.draw(GAME_WIDTH / 2.f + MARGIN, GAME_HEIGHT - 30);

        // draw the points
        drawNumber(player.getPoints(), GAME_WIDTH / 2 + MARGIN + 35, GAME_HEIGHT - 40);
        // draw the health
        drawNumber(player.getEntity().getHealth(), MARGIN + 35, GAME_HEIGHT - 40);

        if (UserSettings.displayTime) {
            // draw the current time spent in level
            drawTime(TimeManager.getTimeInLevel(), 5.f, TimeManager.TEXT_LEVEL_TIME);
            // draw the total time spent in the current play through
            if (LevelManager.playerIsInPlayThrough())
                drawTime(TimeManager.getTotalTime(), 33.f, TimeManager.TEXT_TOTAL_TIME);
        }

        if (UserSettings.displayFPS) {
            fps_drawer.drawString(fps_drawer_x, fps_drawer_y, "FPS: " + gc.getFPS());
        }
    }

    private void drawTime(long timeMilliseconds, float y_pos, String info) {
        // draw info
        time_text_header.drawString((int) (GAME_WIDTH / 2.f - time_text_header.getWidth(info) / 2.f),
                y_pos, info, org.newdawn.slick.Color.white);
        // draw actual time one line below
        long timeSeconds = TimeUnit.MILLISECONDS.toSeconds(timeMilliseconds);
        long minutes = (timeSeconds % 3600) / 60;
        long seconds = timeSeconds % 60;
        String time_string = String.format("%02d:%02d", minutes, seconds);
        time_text.drawString((int) (GAME_WIDTH / 2.f - time_text.getWidth(time_string) / 2.f),
                y_pos + 15, time_string, org.newdawn.slick.Color.white);
    }

}
