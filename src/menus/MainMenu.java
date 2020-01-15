package menus;

import main.ZuluAssault;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.Font;

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

    private static Sound sound_before_main, click_sound, error_sound;
    private static Music main_menu_music;

    private static float sound_volume = 1.f, music_volume = 1.f;

    private TrueTypeFont ttf_info_string;

    public MainMenu() {
        try {
            click_sound = new Sound("audio/sounds/click.ogg");
            error_sound = new Sound("audio/sounds/error.ogg");
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
            click_sound.play(1.f, sound_volume);
            menus[current_menu_idx].onUpKeyPress(gameContainer, stateBasedGame);
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_DOWN)) {
            click_sound.play(1.f, sound_volume);
            menus[current_menu_idx].onDownKeyPress(gameContainer, stateBasedGame);
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_ENTER)) {
            click_sound.play(1.f, sound_volume);
            menus[current_menu_idx].onEnterKeyPress(gameContainer, stateBasedGame);
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_LEFT)) {
            click_sound.play(1.f, sound_volume);
            menus[current_menu_idx].onLeftKeyPress(gameContainer, stateBasedGame);
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_RIGHT)) {
            click_sound.play(1.f, sound_volume);
            menus[current_menu_idx].onRightKeyPress(gameContainer, stateBasedGame);
        }

        if (!sound_before_main.playing()) {
            if (!main_menu_music.playing()) {
                main_menu_music.play();
                main_menu_music.loop();
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

    public static void setSoundVolume(float volume) {
        sound_volume += volume;
        if (sound_volume < 0.0f) {
            sound_volume = 0.0f;
        } else if (sound_volume > 1.0f) {
            sound_volume = 1.0f;
        }
    }

    public static void setMusicVolume(float volume) {
        music_volume += volume;
        if (music_volume < 0.0f) {
            music_volume = 0.0f;
        } else if (music_volume > 1.0f) {
            music_volume = 1.0f;
        }
        main_menu_music.setVolume(music_volume);
    }

    public static void playErrorSound() {
        error_sound.play(1.f, sound_volume);
    }


    @Override
    public void enter(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        if (firstCall_enter) {  // this is needed because this GameState is entered twice on startup
            firstCall_enter = false;
            return;
        }
        // show the mouse cursor
        gameContainer.setMouseGrabbed(false);
        sound_before_main.play(1.f, music_volume);

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
        sound_before_main.stop();
        main_menu_music.stop();
    }


}
