package game.levels;

import game.util.TimeManager;
import level_editor.LevelEditor;
import main.ZuluAssault;
import game.menu.Menu;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import java.io.File;

public class LevelManager {

    private static StateBasedGame stateBasedGame;

    private static boolean playerIsInPlayThrough, hasPreviousPlayThrough;

    public static void startNewGame(String s_level, BasicGameState bgs) {
        playerIsInPlayThrough = true;
        if (hasPreviousPlayThrough) Level.prepareNewPlayThrough();
        TimeManager.reset();
        startNextLevel(s_level, bgs);
        hasPreviousPlayThrough = true;
    }

    public static void startNextLevel(String s_level, BasicGameState bgs) {
        ZuluAssault.nextLevelName = s_level;
        stateBasedGame.enterState(ZuluAssault.BRIEFING, new FadeOutTransition(), new FadeInTransition());
        ZuluAssault.prevState = bgs;
    }

    public static void goToMainMenu() {
        Menu.goToMenu(Menu.STATE_MAIN_MENU);
        stateBasedGame.enterState(ZuluAssault.MAIN_MENU,
                new FadeOutTransition(), new FadeInTransition()
        );
    }

    public static void openSingleLevel(String s_level) {
        playerIsInPlayThrough = false;
        // if it is an official level, add 'map_' - prefix
        ZuluAssault.nextLevelName = isCustomLevel(s_level) ? s_level : "map_" + s_level;
        stateBasedGame.enterState(ZuluAssault.BRIEFING, new FadeOutTransition(), new FadeInTransition());
    }

    public static void gameOver(BasicGameState bgs) {
        Menu.goToMenu(Menu.STATE_DEATH_MENU);
        stateBasedGame.enterState(ZuluAssault.MAIN_MENU,
                new FadeOutTransition(), new FadeInTransition()
        );
        ZuluAssault.prevState = bgs;
    }

    public static boolean playerIsInPlayThrough() {
        return playerIsInPlayThrough;
    }

    public static void init(StateBasedGame sbg) {
        stateBasedGame = sbg;
    }

    public static boolean existsLevel(String name) {
        try {
            int levelID = Integer.parseInt(name);
            if (levelID <= ZuluAssault.MAX_LEVEL && levelID >= 1) {
                return true;
            }
        } catch (NumberFormatException e) {
            // a level string was put in -> look for custom level
            return existsCustomLevel(name);
        }
        return existsCustomLevel(name);
    }

    private static boolean existsCustomLevel(String name) {
        String fileName = LevelEditor.CUSTOM_MAPS_FOLDER + name + ".tmx";
        return new File(fileName).exists();
    }

    public static boolean isCustomLevel(String name) {
        try {
            int levelID = Integer.parseInt(name);
            if (levelID <= ZuluAssault.MAX_LEVEL && levelID >= 1) {
                return false;
            }
        } catch (NumberFormatException e) {
            return true;
        }
        return true;
    }

}
