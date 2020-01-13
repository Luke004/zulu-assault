package menus;

import main.ZuluAssault;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.Font;


public class MainMenu extends BasicGameState {

    private Arrow menu_arrow;
    private Image main_menu_image;
    private Vector2f main_menu_image_position;
    private String info_string, title_string;
    private static boolean firstCall_leave = true, firstCall_enter = true;

    private Sound sound_before_main, click_sound;
    private Music main_menu_music;

    private TrueTypeFont ttf_info_string, ttf_title_string;

    public MainMenu() {
        try {
            click_sound = new Sound("audio/sounds/click.ogg");
            sound_before_main = new Sound("audio/sounds/before_main.ogg");
            main_menu_music = new Music("audio/music/main_menu.ogg");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getID() {
        return ZuluAssault.MAIN_MENU;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        Font awtFont = new Font("DialogInput", Font.PLAIN, 12);
        ttf_info_string = new TrueTypeFont(awtFont, false);
        awtFont = new Font("Courier", Font.BOLD, 50);
        ttf_title_string = new TrueTypeFont(awtFont, false);

        title_string = "ZULU ASSAULT";
        info_string = "(C) 1998 Dallas Nutsch - Alpha Test Version Rebuild by Lukas Hilfrich";

        try {
            main_menu_image = new Image("assets/menus/main_menu.png");
            main_menu_image_position = new Vector2f(
                    gameContainer.getWidth() / 2.f - main_menu_image.getWidth() / 2.f,
                    gameContainer.getHeight() / 2.f);
            // set custom animated mouse cursor
            gameContainer.setAnimatedMouseCursor(
                    "assets/menus/animated_cursor.tga",
                    0,
                    0,
                    32,
                    32,
                    new int[]{20, 20, 20, 20}
            );
        } catch (SlickException e) {
            e.printStackTrace();
        }
        // the menu arrow pointing at the current selection to enter
        menu_arrow = new Arrow();
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) {
        ttf_title_string.drawString(gameContainer.getWidth() / 2.f - ttf_title_string.getWidth(title_string) / 2.f,
                gameContainer.getHeight() / 4.f - ttf_title_string.getHeight(title_string) / 2.f,
                title_string,
                Color.lightGray);

        ttf_info_string.drawString(
                5,
                gameContainer.getHeight() - ttf_info_string.getHeight() - 5,
                info_string);

        main_menu_image.draw(main_menu_image_position.x, main_menu_image_position.y);

        menu_arrow.draw();
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) {
        if (gameContainer.getInput().isKeyPressed(Input.KEY_DOWN)) {
            click_sound.play();
            menu_arrow.moveDown();
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_UP)) {
            click_sound.play();
            menu_arrow.moveUp();
        }

        if (gameContainer.getInput().isKeyPressed(Input.KEY_ENTER)) {
            click_sound.play();
            switch (menu_arrow.currIdx) {
                case 0:
                    // START
                    stateBasedGame.enterState(ZuluAssault.LEVEL_1);
                    break;
                case 4:
                    System.exit(0);
            }
        }

        if (!sound_before_main.playing()) {
            if (!main_menu_music.playing()) {
                main_menu_music.play();
                main_menu_music.loop();
            }
        }
    }


    @Override
    public void enter(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        if (firstCall_enter) {  // this is needed because this GameState is entered twice on startup
            firstCall_enter = false;
            return;
        }
        // show the mouse cursor
        gameContainer.setMouseGrabbed(false);
        sound_before_main.play();
    }

    @Override
    public void leave(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        if (firstCall_leave) {  // this is needed because this GameState is entered twice on startup
            firstCall_leave = false;
            return;
        }
        sound_before_main.stop();
        main_menu_music.stop();
    }

    private static class Arrow {
        private Image arrow_image;

        private int[] main_menu_positions;
        private int currIdx;

        Arrow() {
            try {
                arrow_image = new Image("assets/menus/arrow.png");
            } catch (SlickException e) {
                e.printStackTrace();
            }

            currIdx = 0;

            main_menu_positions = new int[5];
            int startY = 305, y_offset = 40;
            for (int i = 0; i < main_menu_positions.length; ++i) {
                main_menu_positions[i] = startY;
                startY += y_offset;
            }
        }

        void draw() {
            arrow_image.draw(240, main_menu_positions[currIdx]);
        }

        void moveDown() {
            if (currIdx == main_menu_positions.length - 1) {
                currIdx = 0;
            } else currIdx++;
        }

        void moveUp() {
            if (currIdx == 0) {
                currIdx = main_menu_positions.length - 1;
            } else currIdx--;
        }
    }
}
