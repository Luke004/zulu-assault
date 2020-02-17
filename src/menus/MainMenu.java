package menus;

import main.ZuluAssault;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MainMenu extends BasicGameState {

    // ALL MENUS
    public static final int STATE_MAIN_MENU = 0,
            STATE_IN_GAME_MENU = 1,
            STATE_LOAD_GAME_MENU = 2,
            STATE_SAVE_GAME_MENU = 3,
            STATE_OPTIONS_MENU = 4,
            STATE_DEATH_MENU = 5,
            STATE_CONFIRM_EXIT_MENU = 6;

    private static int current_menu_idx, prev_menu_idx;

    private AbstractMenuScreen[] menus;

    private static boolean firstCall_leave = true, firstCall_enter = true;

    protected static Sound main_menu_intro_sound;
    protected static Music main_menu_music;

    protected static String title_string;
    protected static Vector2f title_string_position;
    private static TrueTypeFont ttf_title_string;

    private static String[] info_strings;
    private static TrueTypeFont ttf_info_string;
    public final static int TEXT_MARGIN = 5;

    public MainMenu() {
        try {
            main_menu_intro_sound = new Sound("audio/music/main_menu_intro.ogg");
            main_menu_music = new Music("audio/music/main_menu.ogg");
        } catch (SlickException e) {
            e.printStackTrace();
        }

        File directory = new File("saves/settings/");
        // try to load user settings
        try {
            File user_settings_file = new File(directory + File.separator + "user_settings");
            if (user_settings_file.exists()) {
                Properties props = new Properties();
                FileInputStream in = new FileInputStream(directory + File.separator + "user_settings");
                props.load(in);
                UserSettings.setSoundVolume(Integer.parseInt(props.getProperty("sound_volume_level")));
                UserSettings.setMusicVolume(Integer.parseInt(props.getProperty("music_volume_level")));
                in.close();
            } else {
                // user settings don't exist -> use default settings
                UserSettings.setSoundVolume(UserSettings.VOLUME_MAX_LEVEL);
                UserSettings.setMusicVolume(UserSettings.VOLUME_MAX_LEVEL);
            }
        } catch (IOException e) {
            System.out.println("could not load 'user_settings'");
            // use default settings
            UserSettings.setSoundVolume(UserSettings.VOLUME_MAX_LEVEL);
            UserSettings.setMusicVolume(UserSettings.VOLUME_MAX_LEVEL);
            e.printStackTrace();
        }
    }

    @Override
    public int getID() {
        return ZuluAssault.MAIN_MENU;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        Font awtFont = new Font("Courier", Font.BOLD, 50);
        ttf_title_string = new TrueTypeFont(awtFont, false);
        title_string = "ZULU ASSAULT";
        title_string_position = new Vector2f(
                gameContainer.getWidth() / 2.f - ttf_title_string.getWidth(title_string) / 2.f,
                gameContainer.getHeight() / 4.f - ttf_title_string.getHeight(title_string) / 2.f
        );

        awtFont = new Font("DialogInput", Font.PLAIN, 11);
        ttf_info_string = new TrueTypeFont(awtFont, false);
        info_strings = new String[3];
        info_strings[0] = "(C) 1998 Dallas Nutsch";
        info_strings[1] = "Rebuild by Lukas Hilfrich";
        info_strings[2] = "Alpha Test Version 1.0";


        try {
            // set custom animated mouse cursor
            gameContainer.setAnimatedMouseCursor(
                    "assets/menus/animated_cursor.tga",
                    15,
                    15,
                    32,
                    32,
                    new int[]{20, 20, 20, 20}
            );
        } catch (SlickException e) {
            e.printStackTrace();
        }
        menus = new AbstractMenuScreen[7];
        menus[STATE_MAIN_MENU] = new MainScreen(this, gameContainer);
        menus[STATE_IN_GAME_MENU] = new InGameScreen(this, gameContainer);
        menus[STATE_LOAD_GAME_MENU] = new LoadGameScreen(this, gameContainer);
        menus[STATE_SAVE_GAME_MENU] = new SaveGameScreen(this, gameContainer);
        menus[STATE_OPTIONS_MENU] = new OptionsScreen(this, gameContainer);
        menus[STATE_DEATH_MENU] = new DeathScreen(this);
        menus[STATE_CONFIRM_EXIT_MENU] = new ConfirmExitScreen(this, gameContainer);

        goToMenu(STATE_MAIN_MENU, gameContainer);
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) {
        menus[current_menu_idx].render(gameContainer);
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) {
        menus[current_menu_idx].update(gameContainer, stateBasedGame);
    }

    public static void goToMenu(int menu_idx, GameContainer gameContainer) {
        prev_menu_idx = current_menu_idx;
        current_menu_idx = menu_idx;
        clearKeyPresses(gameContainer);
    }


    public static void returnToPreviousMenu() {
        int temp = prev_menu_idx;
        prev_menu_idx = current_menu_idx;
        current_menu_idx = temp;
    }

    public static void updateMainMenuMusicVolume() {
        main_menu_music.setVolume(UserSettings.MUSIC_VOLUME);
    }

    @Override
    public void enter(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        if (firstCall_enter) {  // this is needed because this GameState is entered twice on startup
            firstCall_enter = false;
            return;
        }
        gameContainer.setMouseGrabbed(false);   // show the mouse cursor
        menus[current_menu_idx].onEnterState(gameContainer);
        clearKeyPresses(gameContainer);
    }

    @Override
    public void leave(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        if (firstCall_leave) {  // this is needed because this GameState is entered twice on startup
            firstCall_leave = false;
            return;
        }
        menus[current_menu_idx].onLeaveState(gameContainer);
        clearKeyPresses(gameContainer);
    }

    protected static void drawInfoStrings(GameContainer gameContainer) {
        ttf_info_string.drawString(
                TEXT_MARGIN,
                gameContainer.getHeight() - ttf_info_string.getHeight() - TEXT_MARGIN,
                info_strings[0]);
        ttf_info_string.drawString(
                gameContainer.getWidth() - ttf_info_string.getWidth(info_strings[1]) - TEXT_MARGIN,
                gameContainer.getHeight() - ttf_info_string.getHeight() - TEXT_MARGIN,
                info_strings[1]);
        ttf_info_string.drawString(
                gameContainer.getWidth() - ttf_info_string.getWidth(info_strings[2]) - TEXT_MARGIN,
                TEXT_MARGIN,
                info_strings[2]);
    }

    protected static void drawGameTitle() {
        ttf_title_string.drawString(
                title_string_position.x,
                title_string_position.y,
                title_string,
                org.newdawn.slick.Color.lightGray);
    }

    /* this exists to fix a bug where key press functions simply are called as soon as they can be called */
    private static void clearKeyPresses(GameContainer gameContainer) {
        if (gameContainer.getInput().isKeyPressed(Input.KEY_LEFT)) {
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_RIGHT)) {
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_UP)) {
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_DOWN)) {
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_ENTER)) {
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_LSHIFT)) {
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_RSHIFT)) {
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_X)) {
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_Y)) {
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_A)) {
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_Y)) {
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_J)) {
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_N)) {
        }
    }


}
