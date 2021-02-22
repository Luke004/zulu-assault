package game.menu.screens;

import game.audio.MenuSounds;
import game.graphics.fonts.FontManager;
import game.menu.Menu;
import game.menu.elements.Arrow;
import level_editor.screens.elements.Button;
import level_editor.screens.elements.TextFieldTitled;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import settings.UserSettings;

import javax.swing.*;

import static game.menu.Menu.main_menu_intro_sound;
import static game.menu.Menu.main_menu_music;
import static game.menu.screens.MainScreen.MENU_OPTION_HEIGHT;

public class FeedbackScreen extends AbstractMenuScreen {

    TextFieldTitled textField;
    private Button sendBtn;

    private static final TrueTypeFont menu_drawer;
    private int back_btn_width, back_btn_height;
    private Vector2f back_btn_position;
    private Arrow arrow;

    static {
        menu_drawer = FontManager.getStencilBigFont();
    }

    public FeedbackScreen(BasicGameState gameState, GameContainer gc) {
        super(gameState);

        back_btn_width = menu_drawer.getWidth("BACK");
        back_btn_height = MENU_OPTION_HEIGHT;

        final int MARGIN = 30;
        int y_pos = gc.getHeight() / 2;
        back_btn_position = new Vector2f(gc.getWidth() / 2.f - back_btn_width / 2.f, y_pos);
        y_pos += back_btn_height + MARGIN;
        final int textFieldHeight = 50;
        textField = new TextFieldTitled(gc, FontManager.getConsoleOutputFont(false), 20, y_pos,
                gc.getWidth() - 100, textFieldHeight,
                "Provide feedback below! (Bug Reports, Suggestions, Requests, Opinions etc.)"
        );
        textField.setBorderColor(Color.lightGray);
        y_pos += textFieldHeight + MARGIN;
        sendBtn = new Button("SEND", gc.getWidth() / 2 - 50, y_pos, 100, 40);

        arrow = new Arrow(gc, 1, (int) back_btn_position.y);
    }

    @Override
    public void update(GameContainer gc, StateBasedGame sbg) {
        if (gc.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
            onMouseClick(gc, sbg,
                    gc.getInput().getMouseX(),
                    gc.getInput().getMouseY());
        }
        handleKeyInput(gc, sbg);

        // loop the menu sound
        if (!main_menu_intro_sound.playing()) {
            if (!main_menu_music.playing()) {
                main_menu_music.play();
                main_menu_music.loop();
                main_menu_music.setVolume(UserSettings.musicVolume);
            }
        }
        sendBtn.update(gc);
    }

    @Override
    public void render(GameContainer gc) {
        gc.getGraphics().setColor(Color.lightGray);
        textField.render(gc, gc.getGraphics());
        sendBtn.draw(gc.getGraphics());
        menu_drawer.drawString(back_btn_position.x, back_btn_position.y, "BACK", Color.lightGray);
        arrow.draw();
        Menu.drawInfoStrings(gc);
    }

    @Override
    public void handleKeyInput(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        if (gameContainer.getInput().isKeyPressed(Input.KEY_ESCAPE) || gameContainer.getInput().isKeyPressed(Input.KEY_ENTER)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
            Menu.returnToPreviousMenu();
        }
    }

    @Override
    public void onMouseClick(GameContainer gameContainer, StateBasedGame stateBasedGame, int mouseX, int mouseY) {
        if (sendBtn.isMouseOver(mouseX, mouseY)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
        } else if (mouseX > back_btn_position.x && mouseX < back_btn_position.x + back_btn_width) {
            if (mouseY > back_btn_position.y && mouseY < back_btn_position.y + back_btn_height) {
                MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
                Menu.returnToPreviousMenu();
            }
        }
    }

    @Override
    public void onEnterState(GameContainer gameContainer) {

    }

    @Override
    public void onLeaveState(GameContainer gameContainer) {

    }
}
