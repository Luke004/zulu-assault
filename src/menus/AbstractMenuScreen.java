package menus;

import main.SoundManager;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public abstract class AbstractMenuScreen implements iMenuScreen {

    protected BasicGameState gameState;

    public AbstractMenuScreen(BasicGameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        if (gameContainer.getInput().isKeyPressed(Input.KEY_UP)) {
            SoundManager.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
            onUpKeyPress(gameContainer);
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_DOWN)) {
            SoundManager.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
            onDownKeyPress(gameContainer);
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_ENTER)) {
            SoundManager.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
            onEnterKeyPress(gameContainer, stateBasedGame);
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
            SoundManager.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
            onExitKeyPress(gameContainer, stateBasedGame);
        }
    }
}
