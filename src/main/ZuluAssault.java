package main;

import levels.Level_1;
import levels.Level_2;
import menus.MainMenu;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.Sys;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.io.File;

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
    }

    public static void main(String[] args) {
        String gameName = "Zulu Assault";
        boolean fullscreen = false;

        // this looks for the 'natives' folder and uses the right dll according to the operating system
        System.setProperty("org.lwjgl.librarypath", new File(
                new File(System.getProperty("user.dir"), "native"),
                LWJGLUtil.getPlatformName())
                .getAbsolutePath());

        try {
            AppGameContainer app = new AppGameContainer(new ZuluAssault(gameName));
            app.setDisplayMode(640, 480, fullscreen);
            app.setTargetFrameRate(120);
            app.setShowFPS(false);
            app.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initStatesList(GameContainer gameContainer) throws SlickException {
        //this.getState(MAIN_MENU).init(gameContainer, this);
        this.getState(LEVEL_1).init(gameContainer, this);   // pre-load the first level at the start
        this.enterState(MAIN_MENU);
    }
}
