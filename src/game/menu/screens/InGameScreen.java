package game.menu.screens;

import game.audio.MenuSounds;
import game.graphics.fonts.FontManager;
import game.levels.LevelManager;
import main.ZuluAssault;
import game.menu.Menu;
import game.menu.elements.Arrow;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import settings.UserSettings;

import static game.menu.Menu.*;


public class InGameScreen extends AbstractMenuScreen {

    private BasicGameState gameState;

    // game.menu options
    private static final String[] menu_options;
    private Vector2f menu_options_position;
    private int menu_options_width, menu_options_height;
    private static final TrueTypeFont menu_drawer;

    // selection arrow
    private Arrow arrow;
    private final int[] MENU_Y_MOUSE_CLICK_OFFSETS;

    static {
        menu_drawer = FontManager.getStencilBigFont();
        menu_options = new String[]{"RESUME", "NEW", "LOAD", "SAVE", "OPTIONS", "QUIT"};
    }

    public InGameScreen(BasicGameState gameState, GameContainer gameContainer) {
        super(gameState);
        this.gameState = gameState;
        final int menu_y_offset = 40;
        final int MENU_ITEM_SIZE = menu_options.length;

        menu_options_width = menu_drawer.getWidth(menu_options[3]);
        menu_options_height = menu_y_offset * MENU_ITEM_SIZE;

        menu_options_position = new Vector2f(
                gameContainer.getWidth() / 2.f - menu_drawer.getWidth(menu_options[3]) / 2.f,
                gameContainer.getHeight() / 2.f - menu_y_offset);

        arrow = new Arrow(gameContainer, MENU_ITEM_SIZE, (int) menu_options_position.y);

        MENU_Y_MOUSE_CLICK_OFFSETS = new int[MENU_ITEM_SIZE];
        int idx = 0;
        do {
            MENU_Y_MOUSE_CLICK_OFFSETS[idx] = (int) menu_options_position.y + menu_y_offset * (idx + 1);
            idx++;
        } while (idx < MENU_ITEM_SIZE);
    }

    @Override
    public void render(GameContainer gameContainer) {
        super.render(gameContainer);
        // draw all the game.menu options
        for (int i = 0; i < menu_options.length; ++i) {
            menu_drawer.drawString(gameContainer.getWidth() / 2.f - menu_drawer.getWidth(menu_options[i]) / 2.f,
                    menu_options_position.y + (i * (menu_drawer.getHeight("A") - 5)),
                    menu_options[i], Color.lightGray);
        }
        Menu.drawGameTitle();
        arrow.draw();
        Menu.drawInfoStrings(gameContainer);
    }

    @Override
    public void handleKeyInput(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        if (gameContainer.getInput().isKeyPressed(Input.KEY_UP)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
            arrow.moveUp();
        } else if (gameContainer.getInput().isKeyPressed(Input.KEY_DOWN)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
            arrow.moveDown();
        } else if (gameContainer.getInput().isKeyPressed(Input.KEY_ENTER)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
            handleMenuItemChoice(gameContainer, stateBasedGame, arrow.currIdx);
        } else if (gameContainer.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
            arrow.currIdx = 5;
        }
    }

    @Override
    public void onMouseClick(GameContainer gameContainer, StateBasedGame stateBasedGame, int mouseX, int mouseY) {
        if (mouseX > menu_options_position.x && mouseX < menu_options_position.x + menu_options_width) {
            if (mouseY > menu_options_position.y && mouseY < menu_options_position.y + menu_options_height) {
                MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
                for (int idx = 0; idx < MENU_Y_MOUSE_CLICK_OFFSETS.length; ++idx) {
                    if (mouseY < MENU_Y_MOUSE_CLICK_OFFSETS[idx]) {
                        handleMenuItemChoice(gameContainer, stateBasedGame, idx);
                        break;
                    }
                }
            }
        }
    }

    private void handleMenuItemChoice(GameContainer gameContainer, StateBasedGame stateBasedGame, int idx) {
        arrow.currIdx = idx;
        switch (idx) {
            case 0: // RESUME CURRENT GAME
                stateBasedGame.enterState(ZuluAssault.prevState.getID(),
                        new FadeOutTransition(), new FadeInTransition());
                break;
            case 1: // START NEW GAME
                LevelManager.startNewGame("map_1", gameState);
                break;
            case 2: // LOAD
            case 3: // SAVE
                MenuSounds.ERROR_SOUND.play(1.f, UserSettings.soundVolume);
                break;
            case 4: // OPTIONS
                goToMenu(Menu.STATE_OPTIONS_MENU);
                break;
            case 5: // EXIT
                goToMenu(STATE_CONFIRM_EXIT_MENU);
                break;
        }
    }

    @Override
    public void onEnterState(GameContainer gc) {
        main_menu_intro_sound.play(1.f, UserSettings.musicVolume);
    }

    @Override
    public void onLeaveState(GameContainer gameContainer) {
        main_menu_intro_sound.stop();
        main_menu_music.stop();
        gameContainer.setMouseGrabbed(true);
    }
}
