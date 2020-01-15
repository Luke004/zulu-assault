package menus;

import menus.menu_elements.Arrow;
import menus.menu_elements.Slider;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.state.StateBasedGame;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


public class OptionsScreen implements iMenuScreen {

    private Arrow arrow;
    private Slider sound_volume_slider, music_volume_slider;
    private Image back_image;
    private Vector2f back_image_position;


    public OptionsScreen(GameContainer gameContainer) {
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
            ), "Sound Volume", 10);
            music_volume_slider = new Slider(slider_texture, slider_value_texture, new Vector2f(
                    back_image_position.x - 9,
                    back_image_position.y + back_image.getHeight() * 2
            ), "Music Volume", 10);
        } catch (SlickException e) {
            e.printStackTrace();
        }
        arrow = new Arrow(3, 305);
    }

    @Override
    public void render(GameContainer gameContainer) {
        back_image.draw(back_image_position.x, back_image_position.y);
        sound_volume_slider.draw();
        music_volume_slider.draw();
        arrow.draw();
    }

    @Override
    public void onUpKeyPress(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        arrow.moveUp();
    }

    @Override
    public void onDownKeyPress(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        arrow.moveDown();
    }

    @Override
    public void onEnterKeyPress(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        switch (arrow.currIdx) {
            case 0: // BACK
                MainMenu.returnToPreviousMenu();
                // store the settings from user in the file 'user_settings'
                try {
                    File directory = new File("saves/settings/");
                    File user_settings_file = new File(directory + File.separator + "user_settings");
                    //noinspection ResultOfMethodCallIgnored
                    user_settings_file.createNewFile(); // this creates a file only if it not already exists
                    // store audio properties in 'user_settings' file
                    Properties userProps = new Properties();
                    userProps.setProperty("sound_volume", Float.toString(MainMenu.getSoundVolume()));
                    userProps.setProperty("music_volume", Float.toString(MainMenu.getMusicVolume()));
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
    public void onLeftKeyPress(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        switch (arrow.currIdx) {
            case 1: // SOUND VOLUME
                MainMenu.incrementSoundVolume(-0.1f);
                sound_volume_slider.decreaseValue();
                break;
            case 2: // MUSIC VOLUME
                MainMenu.incrementMusicVolume(-0.1f);
                music_volume_slider.decreaseValue();
                break;
        }
    }

    @Override
    public void onRightKeyPress(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        switch (arrow.currIdx) {
            case 1: // SOUND VOLUME
                MainMenu.incrementSoundVolume(0.1f);
                sound_volume_slider.increaseValue();
                break;
            case 2: // MUSIC VOLUME
                MainMenu.incrementMusicVolume(0.1f);
                music_volume_slider.increaseValue();
                break;
        }
    }
}
