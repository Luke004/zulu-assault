package game.levels;

import game.util.TimeManager;
import main.ZuluAssault;
import game.menu.Menu;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class LevelHandler {

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
        ZuluAssault.nextLevelName = s_level;
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

}
