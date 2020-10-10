package main;

import levels.*;
import logic.SettingsManager;
import menus.MainMenu;
import org.lwjgl.LWJGLUtil;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.io.File;

public class ZuluAssault extends StateBasedGame {

    public static final String gameName = "Zulu Assault";
    public static final String gameVersion = "Alpha Test Version 1.2";

    public static final int MAIN_MENU = 0;
    public static final int LEVEL_1 = 1;
    public static final int LEVEL_2 = 2;
    public static final int LEVEL_3 = 3;
    public static final int LEVEL_4 = 4;
    public static final int LEVEL_5 = 5;
    public static final int LEVEL_6 = 6;
    public static final int BRIEFING = -1;
    public static final int DEBRIEFING = -2;

    public static BasicGameState prevState = null;
    public static int nextLevelID = 0;

    public ZuluAssault(String name) {
        super(name);
        this.addState(new MainMenu());
        this.addState(new Level_1());
        this.addState(new Level_2());
        this.addState(new Level_3());
        this.addState(new Level_4());
        this.addState(new Level_5());
        this.addState(new Level_6());
        this.addState(new Debriefing());
        this.addState(new Briefing());
    }

    public static void main(String[] args) {
        boolean fullscreen = false;

        // this looks for the 'natives' folder and uses the right dll according to the operating system
        System.setProperty("org.lwjgl.librarypath", new File(
                new File(System.getProperty("user.dir"), "native"),
                LWJGLUtil.getPlatformName())
                .getAbsolutePath());
        SettingsManager.createSettingsFile();
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

    public static boolean existsLevel(int id) {
        if (id < 1 || id > 6) return false;
        return true;
    }

}
