package game.menu.screens;

import game.audio.MenuSounds;
import game.graphics.fonts.FontManager;
import game.menu.elements.Buttons;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import settings.UserSettings;

import static game.menu.screens.MainScreen.MENU_OPTION_HEIGHT;

public class LoadGameScreen extends AbstractMenuScreen {

    private int back_btn_width, back_btn_height;
    private Vector2f back_btn_position;
    private Buttons buttons;
    private static final TrueTypeFont menu_drawer;

    static {
        menu_drawer = FontManager.getStencilBigFont();
    }

    public LoadGameScreen(BasicGameState gameState, GameContainer gameContainer) {
        super(gameState);
        back_btn_width = menu_drawer.getWidth("BACK");
        back_btn_height = MENU_OPTION_HEIGHT;
        back_btn_position = new Vector2f(
                gameContainer.getWidth() / 2.f - back_btn_width / 2.f,
                gameContainer.getHeight() / 2.f);
        Texture button_texture_inactive;
        try {
            button_texture_inactive = new Image("assets/menus/green_button_inactive.png").getTexture();
            Texture button_texture_active = new Image("assets/menus/green_button_active.png").getTexture();
            buttons = new Buttons(button_texture_active, button_texture_inactive,
                    new Vector2f(100, 100), new String[]{
                    "EMPTY", "EMPTY", "EMPTY", "EMPTY", "EMPTY", "EMPTY",
                    "EMPTY", "EMPTY", "EMPTY", "EMPTY", "EMPTY", "EMPTY"
            });
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(GameContainer gameContainer) {
        super.render(gameContainer);
        menu_drawer.drawString(
                gameContainer.getWidth() / 2.f - back_btn_width / 2.f,
                20.f,
                "BACK",
                Color.lightGray);
        buttons.draw();
    }

    @Override
    public void handleKeyInput(GameContainer gameContainer, StateBasedGame stateBasedGame) {

    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        // MOUSE
        if (gameContainer.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
            onMouseClick(gameContainer, stateBasedGame,
                    gameContainer.getInput().getMouseX(),
                    gameContainer.getInput().getMouseY());
        }
    }

    @Override
    public void onMouseClick(GameContainer gameContainer, StateBasedGame stateBasedGame, int mouseX, int mouseY) {
        int idx = buttons.isClicked(mouseX, mouseY);
        if (idx != -1) MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
    }

    @Override
    public void onEnterState(GameContainer gameContainer) {

    }

    @Override
    public void onLeaveState(GameContainer gameContainer) {

    }
}
