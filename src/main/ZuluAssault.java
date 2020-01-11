package main;

import levels.Level_1;
import menus.MainMenu;
import org.lwjgl.LWJGLUtil;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TileSet;
import org.newdawn.slick.tiled.TiledMap;

import java.io.File;

public class ZuluAssault extends StateBasedGame {

    public static final int MAIN_MENU = 0;
    public static final int LEVEL_1 = 1;

    public ZuluAssault(String name) {
        super(name);
        this.addState(new MainMenu());
        this.addState(new Level_1());
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
