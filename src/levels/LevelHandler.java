package levels;

import level_editor.util.TimeManager;
import main.ZuluAssault;
import menu.Menu;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class LevelHandler {

    private static StateBasedGame stateBasedGame;

    private static boolean playerIsInPlayThrough;

    public static void startNewGame(int levelID, BasicGameState bgs) {
        playerIsInPlayThrough = true;
        AbstractLevel.resetPlayerStats();
        TimeManager.reset();
        startNextLevel(levelID, bgs);
    }

    public static void startNextLevel(int levelID, BasicGameState bgs) {
        ZuluAssault.nextLevelID = levelID;
        stateBasedGame.enterState(ZuluAssault.BRIEFING, new FadeOutTransition(), new FadeInTransition());
        ZuluAssault.prevState = bgs;
    }

    public static void goToMainMenu() {
        Menu.goToMenu(Menu.STATE_MAIN_MENU);
        stateBasedGame.enterState(ZuluAssault.MAIN_MENU,
                new FadeOutTransition(), new FadeInTransition()
        );
    }

    public static void openSingleLevel(int levelID) {
        playerIsInPlayThrough = false;
        ZuluAssault.nextLevelID = levelID;
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
