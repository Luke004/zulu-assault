package menus.screens;

import audio.SoundManager;
import menus.MainMenu;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import settings.UserSettings;

import static menus.MainMenu.main_menu_intro_sound;
import static menus.MainMenu.main_menu_music;

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
        super.update(gameContainer, stateBasedGame);

        // handle key inputs
        if (gameContainer.getInput().isKeyPressed(Input.KEY_ENTER)) {
            SoundManager.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
            System.exit(0);
        } else if (gameContainer.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
            SoundManager.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
            MainMenu.returnToPreviousMenu();
        } else if (gameContainer.getInput().isKeyPressed(Input.KEY_Y)
                || gameContainer.getInput().isKeyPressed(Input.KEY_J)) {
            SoundManager.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
            System.exit(0);
        } else if (gameContainer.getInput().isKeyPressed(Input.KEY_N)) {
            SoundManager.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
            MainMenu.returnToPreviousMenu();
        }


        if (!main_menu_intro_sound.playing()) {
            if (!main_menu_music.playing()) {
                main_menu_music.play();
                main_menu_music.loop();
                main_menu_music.setVolume(UserSettings.MUSIC_VOLUME);
            }
        }
    }

    @Override
    public void render(GameContainer gameContainer) {
        confirm_exit_image.draw(confirm_exit_image_position.x, confirm_exit_image_position.y);
        MainMenu.drawInfoStrings(gameContainer);
    }

    @Override
    public void onMouseClick(GameContainer gameContainer, StateBasedGame stateBasedGame, int mouseX, int mouseY) {
        if (mouseX < confirm_exit_image_position.x || mouseX > confirm_exit_image_position.x + confirm_exit_image.getWidth()) {
            SoundManager.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
            MainMenu.returnToPreviousMenu();
            return;
        }
        if (mouseY < confirm_exit_image_position.y || mouseY > confirm_exit_image_position.y + confirm_exit_image.getHeight()) {
            SoundManager.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
            MainMenu.returnToPreviousMenu();
            return;
        }
        if (confirmRect.contains(mouseX, mouseY)) {
            SoundManager.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
            System.exit(0);
        } else if (cancelRect.contains(mouseX, mouseY)) {
            SoundManager.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
            MainMenu.returnToPreviousMenu();
        }
    }

    @Override
    public void onEnterState(GameContainer gameContainer) {

    }

    @Override
    public void onLeaveState(GameContainer gameContainer) {

    }
}
