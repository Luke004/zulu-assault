package menus;

import main.ZuluAssault;
import org.newdawn.slick.*;
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
            STATE_OPTIONS_MENU = 4;

    private static int current_menu_idx, prev_menu_idx;

    private iMenuScreen[] menus;

    private String info_string;
    private static boolean firstCall_leave = true, firstCall_enter = true;

    private static Sound main_menu_intro_sound, click_sound, error_sound;
    private static Music main_menu_music;

    private TrueTypeFont ttf_info_string;

    public MainMenu() {
        try {
            click_sound = new Sound("audio/sounds/click.ogg");
            error_sound = new Sound("audio/sounds/error.ogg");
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
        Font awtFont = new Font("DialogInput", Font.PLAIN, 12);
        ttf_info_string = new TrueTypeFont(awtFont, false);

        info_string = "(C) 1998 Dallas Nutsch - Alpha Test Version Rebuild by Lukas Hilfrich";

        try {
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
        menus = new iMenuScreen[5];
        menus[STATE_MAIN_MENU] = new MainScreen(gameContainer);
        menus[STATE_IN_GAME_MENU] = new InGameScreen(gameContainer);
        menus[STATE_LOAD_GAME_MENU] = new LoadGameScreen(gameContainer);
        menus[STATE_SAVE_GAME_MENU] = new SaveGameScreen(gameContainer);
        menus[STATE_OPTIONS_MENU] = new OptionsScreen(gameContainer);

        goToMenu(STATE_MAIN_MENU);
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) {
        menus[current_menu_idx].render(gameContainer);
        ttf_info_string.drawString(
                5,
                gameContainer.getHeight() - ttf_info_string.getHeight() - 5,
                info_string);
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) {
        if (gameContainer.getInput().isKeyPressed(Input.KEY_UP)) {
            click_sound.play(1.f, UserSettings.SOUND_VOLUME);
            menus[current_menu_idx].onUpKeyPress(gameContainer, stateBasedGame);
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_DOWN)) {
            click_sound.play(1.f, UserSettings.SOUND_VOLUME);
            menus[current_menu_idx].onDownKeyPress(gameContainer, stateBasedGame);
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_ENTER)) {
            click_sound.play(1.f, UserSettings.SOUND_VOLUME);
            menus[current_menu_idx].onEnterKeyPress(gameContainer, stateBasedGame);
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_LEFT)) {
            click_sound.play(1.f, UserSettings.SOUND_VOLUME);
            menus[current_menu_idx].onLeftKeyPress(gameContainer, stateBasedGame);
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_RIGHT)) {
            click_sound.play(1.f, UserSettings.SOUND_VOLUME);
            menus[current_menu_idx].onRightKeyPress(gameContainer, stateBasedGame);
        }

        if (!main_menu_intro_sound.playing()) {
            if (!main_menu_music.playing()) {
                main_menu_music.play();
                main_menu_music.loop();
                main_menu_music.setVolume(UserSettings.MUSIC_VOLUME);
            }
        }

    }

    public static void goToMenu(int menu_idx) {
        prev_menu_idx = current_menu_idx;
        current_menu_idx = menu_idx;
    }

    public static void returnToPreviousMenu() {
        int temp = prev_menu_idx;
        prev_menu_idx = current_menu_idx;
        current_menu_idx = temp;
    }

    public static void updateMainMenuMusicVolume() {
        main_menu_music.setVolume(UserSettings.MUSIC_VOLUME);
    }

    public static void playErrorSound() {
        error_sound.play(1.f, UserSettings.SOUND_VOLUME);
    }

    @Override
    public void enter(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        if (firstCall_enter) {  // this is needed because this GameState is entered twice on startup
            firstCall_enter = false;
            return;
        }
        // show the mouse cursor
        gameContainer.setMouseGrabbed(false);
        main_menu_intro_sound.play(1.f, UserSettings.MUSIC_VOLUME);

        if (ZuluAssault.prevState != null) {
            // a previous state exists -> user is in game -> switch states
            goToMenu(STATE_IN_GAME_MENU);
        }
    }

    @Override
    public void leave(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        if (firstCall_leave) {  // this is needed because this GameState is entered twice on startup
            firstCall_leave = false;
            return;
        }
        main_menu_intro_sound.stop();
        main_menu_music.stop();
    }


}
