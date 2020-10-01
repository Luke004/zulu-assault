package menus.screens;

import audio.MenuSounds;
import console.Console;
import menus.MainMenu;
import menus.menu_elements.Arrow;
import menus.menu_elements.Slider;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import settings.UserSettings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static menus.MainMenu.*;
import static settings.UserSettings.VOLUME_MAX_LEVEL;


public class OptionsScreen extends AbstractMenuScreen {

    private Arrow arrow;
    private Slider sound_volume_slider, music_volume_slider;
    private Image back_image;
    private Vector2f back_image_position;

    public OptionsScreen(BasicGameState gameState, GameContainer gameContainer) {
        super(gameState);
        try {
            back_image = new Image("assets/menus/back.png");
            back_image_position = new Vector2f(
                    gameContainer.getWidth() / 2.f - back_image.getWidth() / 2.f,
                    gameContainer.getHeight() / 2.f);
            Texture slider_texture = new Image("assets/menus/slider.png").getTexture();
            Texture slider_value_texture = new Image("assets/menus/slider_value.png").getTexture();
            sound_volume_slider = new Slider(slider_texture, slider_value_texture, new Vector2f(
                    back_image_position.x - 9,
                    back_image_position.y + back_image.getHeight()
            ), "Sound Volume", VOLUME_MAX_LEVEL);
            sound_volume_slider.setValue(UserSettings.SOUND_VOLUME_LEVEL);

            music_volume_slider = new Slider(slider_texture, slider_value_texture, new Vector2f(
                    back_image_position.x - 9,
                    back_image_position.y + back_image.getHeight() * 2
            ), "Music Volume", VOLUME_MAX_LEVEL);
            music_volume_slider.setValue(UserSettings.MUSIC_VOLUME_LEVEL);

            arrow = new Arrow(gameContainer, 3, (int) back_image_position.y);
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(GameContainer gameContainer) {
        super.render(gameContainer);
        back_image.draw(back_image_position.x, back_image_position.y);
        sound_volume_slider.draw();
        music_volume_slider.draw();
        arrow.draw();
        MainMenu.drawInfoStrings(gameContainer);
    }

    @Override
    public void handleKeyInput(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        if (gameContainer.getInput().isKeyPressed(Input.KEY_UP)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
            arrow.moveUp();
        } else if (gameContainer.getInput().isKeyPressed(Input.KEY_DOWN)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
            arrow.moveDown();
        } else if (gameContainer.getInput().isKeyPressed(Input.KEY_ENTER)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
            handleMenuItemChoice(arrow.currIdx);
        } else if (gameContainer.getInput().isKeyPressed(Input.KEY_LEFT)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
            onLeftKeyPress();
        } else if (gameContainer.getInput().isKeyPressed(Input.KEY_RIGHT)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
            onRightKeyPress();
        } else if (gameContainer.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
            returnToPreviousMenu();
        }
    }

    private void handleMenuItemChoice(int idx) {
        switch (idx) {
            case 0: // BACK
                MainMenu.returnToPreviousMenu();
                // store the settings from user in the file 'user_settings'
                try {
                    File directory = new File("saves/settings/");
                    //noinspection ResultOfMethodCallIgnored
                    directory.mkdirs();
                    File user_settings_file = new File(directory + File.separator + "user_settings");
                    //noinspection ResultOfMethodCallIgnored
                    user_settings_file.createNewFile(); // this creates a file only if it not already exists
                    // store audio properties in 'user_settings' file
                    Properties userProps = new Properties();
                    userProps.setProperty("sound_volume_level", Integer.toString(sound_volume_slider.getValue()));
                    userProps.setProperty("music_volume_level", Integer.toString(music_volume_slider.getValue()));
                    FileOutputStream out = new FileOutputStream(directory + File.separator + "user_settings");
                    userProps.store(out, "audio volume");
                    out.close();
                } catch (IOException e) {
                    System.out.println("could not create or store data in file 'src/main/saves/user_settings'");
                    e.printStackTrace();
                }
                break;
            case 1: // SOUND VOLUME

                break;
            case 2: // MUSIC VOLUME

                break;
        }
    }

    @Override
    public void onMouseClick(GameContainer gameContainer, StateBasedGame stateBasedGame, int mouseX, int mouseY) {
        if (mouseX > back_image_position.x && mouseX < back_image_position.x + back_image.getWidth()) {
            if (mouseY > back_image_position.y && mouseY < back_image_position.y + back_image.getHeight()) {
                MenuSounds.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
                arrow.currIdx = 0;
                handleMenuItemChoice(0);
            }
        }
        if (sound_volume_slider.onClick(mouseX, mouseY)) {
            arrow.currIdx = 1;
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
            UserSettings.setSoundVolume(sound_volume_slider.getValue());
        }
        if (music_volume_slider.onClick(mouseX, mouseY)) {
            arrow.currIdx = 2;
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
            UserSettings.setMusicVolume(music_volume_slider.getValue());
            MainMenu.updateMainMenuMusicVolume();
        }
    }

    private void onLeftKeyPress() {
        switch (arrow.currIdx) {
            case 1: // SOUND VOLUME
                sound_volume_slider.decreaseValue();
                UserSettings.setSoundVolume(sound_volume_slider.getValue());
                break;
            case 2: // MUSIC VOLUME
                music_volume_slider.decreaseValue();
                UserSettings.setMusicVolume(music_volume_slider.getValue());
                MainMenu.updateMainMenuMusicVolume();
                break;
        }
    }

    private void onRightKeyPress() {
        switch (arrow.currIdx) {
            case 1: // SOUND VOLUME
                sound_volume_slider.increaseValue();
                UserSettings.setSoundVolume(sound_volume_slider.getValue());
                break;
            case 2: // MUSIC VOLUME
                music_volume_slider.increaseValue();
                UserSettings.setMusicVolume(music_volume_slider.getValue());
                MainMenu.updateMainMenuMusicVolume();
                break;
        }
    }

    @Override
    public void onEnterState(GameContainer gc) {

    }

    @Override
    public void onLeaveState(GameContainer gameContainer) {
        main_menu_intro_sound.stop();
        main_menu_music.stop();
    }
}
