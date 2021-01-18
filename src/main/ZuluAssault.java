package main;

import level_editor.LevelEditor;
import game.levels.*;
import settings.SettingStorage;
import game.menu.Menu;
import org.lwjgl.LWJGLUtil;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.io.File;

public class ZuluAssault extends StateBasedGame {

    public static final String gameName = "Zulu Assault";
    public static final String gameVersion = "Alpha Test Version 1.3.2";

    public static final int MAIN_MENU = 0;
    public static final int BRIEFING = 1;
    public static final int DEBRIEFING = 2;
    public static final int IN_LEVEL = 3;
    public static final int LEVEL_EDITOR = 4;

    public static final int MAX_LEVEL = 15;

    public static BasicGameState prevState = null;
    public static String nextLevelName = "";

    public ZuluAssault(String name) {
        super(name);
        this.addState(new Menu());
        this.addState(new Level());
        this.addState(new Debriefing());
        this.addState(new Briefing());
        this.addState(new LevelEditor());
    }

    public static void main(String[] args) {
        boolean fullscreen = false;
        boolean alterRes = false;

        // this looks for the 'natives' folder and uses the right dll according to the operating system
        System.setProperty("org.lwjgl.librarypath", new File(
                new File(System.getProperty("user.dir"), "native"),
                LWJGLUtil.getPlatformName())
                .getAbsolutePath());
        SettingStorage.createSettingsFile();
        try {
            AppGameContainer app = new AppGameContainer(new ZuluAssault(gameName));
            app.setDisplayMode(alterRes ? 1920 : 640, alterRes ? 1080 : 480, fullscreen);
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
        //this.getState(LEVEL_1).init(gameContainer, this);   // pre-load the first level at the start
        this.enterState(MAIN_MENU);
    }

}
