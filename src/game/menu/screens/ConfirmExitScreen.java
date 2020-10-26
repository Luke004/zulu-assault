package game.menu.screens;

import game.audio.MenuSounds;
import game.menu.Menu;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import settings.UserSettings;

import static game.menu.Menu.main_menu_intro_sound;
import static game.menu.Menu.main_menu_music;

public class ConfirmExitScreen extends AbstractMenuScreen {

    private Image confirm_exit_image;
    private Vector2f confirm_exit_image_position;

    private Rectangle confirmRect, cancelRect;

    public ConfirmExitScreen(BasicGameState gameState, GameContainer gameContainer) {
        super(gameState);

        try {
            confirm_exit_image = new Image("assets/menus/confirm_exit.png");
            confirm_exit_image_position = new Vector2f(gameContainer.getWidth() / 4.f,
                    gameContainer.getHeight() / 4.f);

        } catch (SlickException e) {
            e.printStackTrace();
        }

        confirmRect = new Rectangle(confirm_exit_image_position.x + 154,
                confirm_exit_image_position.y + 95,
                39,
                35);
        cancelRect = new Rectangle(confirm_exit_image_position.x + 194,
                confirm_exit_image_position.y + 95,
                39,
                35);
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        if (gameContainer.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
            onMouseClick(gameContainer, stateBasedGame,
                    gameContainer.getInput().getMouseX(),
                    gameContainer.getInput().getMouseY());
        }
        handleKeyInput(gameContainer, stateBasedGame);

        // loop the game.menu sound
        if (!main_menu_intro_sound.playing()) {
            if (!main_menu_music.playing()) {
                main_menu_music.play();
                main_menu_music.loop();
                main_menu_music.setVolume(UserSettings.musicVolume);
            }
        }
    }

    @Override
    public void render(GameContainer gameContainer) {
        confirm_exit_image.draw(confirm_exit_image_position.x, confirm_exit_image_position.y);
        Menu.drawInfoStrings(gameContainer);
    }

    @Override
    public void handleKeyInput(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        if (gameContainer.getInput().isKeyPressed(Input.KEY_ENTER)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
            System.exit(0);
        } else if (gameContainer.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
            Menu.returnToPreviousMenu();
        } else if (gameContainer.getInput().isKeyPressed(Input.KEY_Y)
                || gameContainer.getInput().isKeyPressed(Input.KEY_J)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
            System.exit(0);
        } else if (gameContainer.getInput().isKeyPressed(Input.KEY_N)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
            Menu.returnToPreviousMenu();
        }
    }

    @Override
    public void onMouseClick(GameContainer gameContainer, StateBasedGame stateBasedGame, int mouseX, int mouseY) {
        if (mouseX < confirm_exit_image_position.x || mouseX > confirm_exit_image_position.x + confirm_exit_image.getWidth()) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
            Menu.returnToPreviousMenu();
            return;
        }
        if (mouseY < confirm_exit_image_position.y || mouseY > confirm_exit_image_position.y + confirm_exit_image.getHeight()) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
            Menu.returnToPreviousMenu();
            return;
        }
        if (confirmRect.contains(mouseX, mouseY)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
            System.exit(0);
        } else if (cancelRect.contains(mouseX, mouseY)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
            Menu.returnToPreviousMenu();
        }
    }

    @Override
    public void onEnterState(GameContainer gameContainer) {

    }

    @Override
    public void onLeaveState(GameContainer gameContainer) {

    }
}
