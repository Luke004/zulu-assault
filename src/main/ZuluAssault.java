package main;

import levels.Level_1;
import levels.Level_2;
import menus.MainMenu;
import org.lwjgl.LWJGLUtil;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ZuluAssault extends StateBasedGame {

    public static final int MAIN_MENU = 0;
    public static final int LEVEL_1 = 1;
    public static final int LEVEL_2 = 2;

    public static BasicGameState prevState = null;

    public ZuluAssault(String name) {
        super(name);
        this.addState(new MainMenu());
        this.addState(new Level_1());
        this.addState(new Level_2());

        File directory = new File("saves/settings/");
        // try to load user properties
        try {
            File user_settings_file = new File(directory + File.separator + "user_settings");
            if (user_settings_file.exists()) {
                Properties props = new Properties();
                FileInputStream in = new FileInputStream(directory + File.separator + "user_settings");
                props.load(in);
                MainMenu.sound_volume = Float.parseFloat(props.getProperty("sound_volume"));
                MainMenu.music_volume = Float.parseFloat(props.getProperty("music_volume"));
                in.close();
            } else {
                // user settings don't exists -> use default settings
                MainMenu.sound_volume = 1.f;
                MainMenu.music_volume = 1.f;
            }
        } catch (IOException e) {
            System.out.println("could not load 'user_settings'");
            // use default settings
            MainMenu.sound_volume = 1.f;
            MainMenu.music_volume = 1.f;
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String gameName = "Zulu Assault";
        boolean fullscreen = false;

        System.setProperty("org.lwjgl.librarypath", new File(
                new File(System.getProperty("user.dir"), "native"), LWJGLUtil.getPlatformName()).getAbsolutePath());
        try {
            AppGameContainer app = new AppGameContainer(new ZuluAssault(gameName));
            app.setDisplayMode(800, 600, fullscreen);
            app.setTargetFrameRate(120);
            app.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initStatesList(GameContainer gameContainer) throws SlickException {
        //this.getState(MAIN_MENU).init(gameContainer, this);
        //this.getState(LEVEL_1).init(gameContainer, this);
        this.enterState(MAIN_MENU);
    }
}
