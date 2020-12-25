package game.menu.screens;

import game.audio.MenuSounds;
import game.levels.LevelManager;
import main.ZuluAssault;
import game.menu.Menu;
import game.menu.elements.Buttons;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import settings.UserSettings;


public class DeathScreen extends AbstractMenuScreen {

    private BasicGameState gameState;

    private Image you_are_dead_image;
    private Sound you_are_dead_sound;

    private Buttons buttons;

    public DeathScreen(BasicGameState gameState) {
        super(gameState);
        this.gameState = gameState;
        try {
            you_are_dead_image = new Image("assets/menus/you_are_dead.png");
            you_are_dead_sound = new Sound("audio/music/you_are_dead.ogg");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        Texture button_texture_inactive;
        try {
            button_texture_inactive = new Image("assets/menus/green_button_inactive.png").getTexture();
            Texture button_texture_active = new Image("assets/menus/green_button_active.png").getTexture();
            buttons = new Buttons(3, false, button_texture_active, button_texture_inactive,
                    new Vector2f(100, 100), new String[]{
                    "Replay Mission", "Load Game", "Main Menu"
            });
        } catch (SlickException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        if (gameContainer.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
            onMouseClick(gameContainer, stateBasedGame,
                    gameContainer.getInput().getMouseX(),
                    gameContainer.getInput().getMouseY());
        }
        handleKeyInput(gameContainer, stateBasedGame);
    }


    @Override
    public void render(GameContainer gameContainer) {
        super.render(gameContainer);
        you_are_dead_image.draw();
        buttons.draw();
    }

    @Override
    public void handleKeyInput(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        if (gameContainer.getInput().isKeyPressed(Input.KEY_UP)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
            buttons.onUpKeyPress();
        } else if (gameContainer.getInput().isKeyPressed(Input.KEY_DOWN)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
            buttons.onDownKeyPress();
        } else if (gameContainer.getInput().isKeyPressed(Input.KEY_ENTER)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
            handleMenuItemChoice(gameContainer, stateBasedGame, buttons.getCurrentButtonIdx());
        }
    }

    @Override
    public void onMouseClick(GameContainer gameContainer, StateBasedGame stateBasedGame, int mouseX, int mouseY) {
        int idx = buttons.isClicked(mouseX, mouseY);
        if (idx != -1) MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
        handleMenuItemChoice(gameContainer, stateBasedGame, idx);
    }

    private void handleMenuItemChoice(GameContainer gameContainer, StateBasedGame stateBasedGame, int idx) {
        switch (idx) {
            case 0: // REPLAY MISSION
                LevelManager.startNewGame(ZuluAssault.nextLevelName, gameState);
                break;
            case 1: // LOAD A GAME
                // TODO: LOAD
                MenuSounds.ERROR_SOUND.play(1.f, UserSettings.soundVolume);
                break;
            case 2: // MAIN MENU
                you_are_dead_sound.stop();
                Menu.goToMenu(Menu.STATE_MAIN_MENU);
                break;
        }
    }

    @Override
    public void onEnterState(GameContainer gc) {
        you_are_dead_sound.play(1.f, UserSettings.musicVolume);
    }

    @Override
    public void onLeaveState(GameContainer gameContainer) {
        you_are_dead_sound.stop();
    }
}
