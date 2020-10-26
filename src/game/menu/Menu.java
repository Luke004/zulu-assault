package game.menu;

import game.menu.console.Console;
import game.graphics.fonts.FontManager;
import main.ZuluAssault;
import game.menu.screens.*;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import settings.UserSettings;

public class Menu extends BasicGameState {

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

    public static Sound main_menu_intro_sound;
    public static Music main_menu_music;

    protected static String title_string;
    protected static Vector2f title_string_position;
    private static TrueTypeFont title_string_drawer;

    private static String[] info_strings;
    private static TrueTypeFont info_string_drawer;
    public final static int TEXT_MARGIN = 5;

    public Menu() {
        try {
            main_menu_intro_sound = new Sound("audio/music/main_menu_intro.ogg");
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
        title_string_drawer = FontManager.getGameTitleFont();
        title_string = "ZULU ASSAULT";
        title_string_position = new Vector2f(
                gameContainer.getWidth() / 2.f - title_string_drawer.getWidth(title_string) / 2.f,
                gameContainer.getHeight() / 4.f - title_string_drawer.getHeight(title_string) / 2.f
        );
        info_string_drawer = FontManager.getConsoleOutputFont(false);
        info_strings = new String[3];
        info_strings[0] = "(C) 1998 Dallas Nutsch";
        info_strings[1] = "Rebuild by Lukas Hilfrich";
        info_strings[2] = ZuluAssault.gameVersion;

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

        Console.init(gameContainer);    // init the game.menu.console

        goToMenu(STATE_MAIN_MENU);
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) {
        menus[current_menu_idx].render(gameContainer);
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) {
        menus[current_menu_idx].update(gameContainer, stateBasedGame);
    }

    public static void goToMenu(int menu_idx) {
        prev_menu_idx = current_menu_idx;
        current_menu_idx = menu_idx;
        //gameContainer.getInput().clearKeyPressedRecord();
    }

    public static void returnToPreviousMenu() {
        int temp = prev_menu_idx;
        prev_menu_idx = current_menu_idx;
        current_menu_idx = temp;
    }

    public static void updateMainMenuMusicVolume() {
        main_menu_music.setVolume(UserSettings.musicVolume);
    }

    @Override
    public void enter(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        if (firstCall_enter) {  // this is needed because this GameState is entered twice on startup
            firstCall_enter = false;
            return;
        }
        gameContainer.setMouseGrabbed(false);   // show the mouse cursor
        menus[current_menu_idx].onEnterState(gameContainer);
        gameContainer.getInput().clearKeyPressedRecord();
    }

    @Override
    public void leave(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        if (firstCall_leave) {  // this is needed because this GameState is entered twice on startup
            firstCall_leave = false;
            return;
        }
        menus[current_menu_idx].onLeaveState(gameContainer);
        gameContainer.getInput().clearKeyPressedRecord();
        ZuluAssault.prevState = this;
    }

    public static void drawInfoStrings(GameContainer gameContainer) {
        info_string_drawer.drawString(
                TEXT_MARGIN,
                gameContainer.getHeight() - info_string_drawer.getHeight() - TEXT_MARGIN,
                info_strings[0],
                Color.lightGray);
        info_string_drawer.drawString(
                gameContainer.getWidth() - info_string_drawer.getWidth(info_strings[1]) - TEXT_MARGIN,
                gameContainer.getHeight() - info_string_drawer.getHeight() - TEXT_MARGIN,
                info_strings[1],
                Color.lightGray);
        info_string_drawer.drawString(
                gameContainer.getWidth() - info_string_drawer.getWidth(info_strings[2]) - TEXT_MARGIN,
                TEXT_MARGIN,
                info_strings[2],
                Color.lightGray);
    }

    public static void drawGameTitle() {
        title_string_drawer.drawString(
                title_string_position.x,
                title_string_position.y,
                title_string,
                Color.darkGray);
    }

    @Override
    public void keyPressed(int key, char c) {
        if (!Console.isActive()) return;
        else Console.handleUserInput(key, c);
    }


}
