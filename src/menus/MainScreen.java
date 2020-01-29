package menus;

import main.SoundManager;
import main.ZuluAssault;
import menus.menu_elements.Arrow;
import org.newdawn.slick.*;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import static menus.MainMenu.*;

public class MainScreen extends AbstractMenuScreen {

    private Arrow arrow;
    private Image main_menu_image;
    private Vector2f main_menu_image_position;

    private final int[] MENU_Y_MOUSE_CLICK_OFFSETS;

    public MainScreen(BasicGameState gameState, GameContainer gameContainer) {
        super(gameState);
        final int MENU_ITEM_SIZE = 5;
        try {
            main_menu_image = new Image("assets/menus/main_menu.png");
            main_menu_image_position = new Vector2f(
                    gameContainer.getWidth() / 2.f - main_menu_image.getWidth() / 2.f,
                    gameContainer.getHeight() / 2.f);

            arrow = new Arrow(gameContainer, MENU_ITEM_SIZE, (int) main_menu_image_position.y);
        } catch (SlickException e) {
            e.printStackTrace();
        }
        final int menu_y_offset = main_menu_image.getHeight() / MENU_ITEM_SIZE;
        MENU_Y_MOUSE_CLICK_OFFSETS = new int[MENU_ITEM_SIZE];
        int idx = 0;
        do {
            MENU_Y_MOUSE_CLICK_OFFSETS[idx] = (int) main_menu_image_position.y + menu_y_offset * (idx + 1);
            idx++;
        } while (idx < MENU_ITEM_SIZE);
    }

    @Override
    public void render(GameContainer gameContainer) {
        main_menu_image.draw(main_menu_image_position.x, main_menu_image_position.y);
        MainMenu.drawGameTitle();
        arrow.draw();
        MainMenu.drawInfoStrings(gameContainer);
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        super.update(gameContainer, stateBasedGame);
        if (!main_menu_intro_sound.playing()) {
            if (!main_menu_music.playing()) {
                main_menu_music.play();
                main_menu_music.loop();
                main_menu_music.setVolume(UserSettings.MUSIC_VOLUME);
            }
        }
    }

    @Override
    public void onUpKeyPress(GameContainer gameContainer) {
        SoundManager.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
        arrow.moveUp();
    }

    @Override
    public void onDownKeyPress(GameContainer gameContainer) {
        SoundManager.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
        arrow.moveDown();
    }

    @Override
    public void onEnterKeyPress(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        SoundManager.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
        handleMenuItemChoice(gameContainer, stateBasedGame, arrow.currIdx);
    }

    @Override
    public void onExitKeyPress(GameContainer gameContainer, StateBasedGame stateBasedGame) {

    }

    @Override
    public void onMouseClick(GameContainer gameContainer, StateBasedGame stateBasedGame, int mouseX, int mouseY) {
        if (mouseX > main_menu_image_position.x && mouseX < main_menu_image_position.x + main_menu_image.getWidth()) {
            if (mouseY > main_menu_image_position.y && mouseY < main_menu_image_position.y + main_menu_image.getHeight()) {
                SoundManager.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
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
        switch (idx) {
            case 0: // NEW
                // START NEW GAME
                try {
                    // init a new game starting with level 1
                    stateBasedGame.getState(ZuluAssault.LEVEL_1).init(gameContainer, stateBasedGame);
                    stateBasedGame.enterState(ZuluAssault.LEVEL_1,
                            new FadeOutTransition(), new FadeInTransition());
                    ZuluAssault.prevState = gameState;
                } catch (SlickException e) {
                    e.printStackTrace();
                }
                break;  // TODO: LOAD AND SAVE
            case 1: // LOAD
            case 2: // SAVE
                SoundManager.ERROR_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
                break;
            case 3: // OPTIONS
                MainMenu.goToMenu(MainMenu.STATE_OPTIONS_MENU);
                break;
            case 4: // EXIT
                goToMenu(STATE_CONFIRM_EXIT_MENU);
                break;
        }
    }

    @Override
    public void onEnterState(GameContainer gc) {
        main_menu_intro_sound.play(1.f, UserSettings.MUSIC_VOLUME);
    }

    @Override
    public void onLeaveState(GameContainer gameContainer) {
        main_menu_intro_sound.stop();
        main_menu_music.stop();
    }
}
