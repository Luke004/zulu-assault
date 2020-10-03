package menus.screens;

import console.Console;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import settings.UserSettings;

import static menus.MainMenu.main_menu_intro_sound;
import static menus.MainMenu.main_menu_music;

public abstract class AbstractMenuScreen implements iMenuScreen {

    protected BasicGameState basicGameState;

    public AbstractMenuScreen(BasicGameState gameState) {
        this.basicGameState = gameState;
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        Console.update(gameContainer);

        if (gameContainer.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
            onMouseClick(gameContainer, stateBasedGame,
                    gameContainer.getInput().getMouseX(),
                    gameContainer.getInput().getMouseY());
        } else if (gameContainer.getInput().isKeyPressed(Input.KEY_BACKSLASH)) {
            // open / close console
            Console.toggle();
        }

        if (!Console.isActive()) {
            handleKeyInput(gameContainer, stateBasedGame);
        }

        // loop the menu sound
        if (!main_menu_intro_sound.playing()) {
            if (!main_menu_music.playing()) {
                main_menu_music.play();
                main_menu_music.loop();
                main_menu_music.setVolume(UserSettings.MUSIC_VOLUME);
            }
        }
    }

    @Override
    public void render(GameContainer gameContainer) {
        Console.render(gameContainer);
    }

    public abstract void handleKeyInput(GameContainer gameContainer, StateBasedGame stateBasedGame);

}
