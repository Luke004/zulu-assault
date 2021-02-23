package game.menu.screens;

import game.audio.MenuSounds;
import game.graphics.fonts.FontManager;
import game.menu.Menu;
import game.menu.console.Console;
import game.menu.elements.Arrow;
import game.util.MailUtil;
import level_editor.screens.elements.Button;
import level_editor.screens.elements.TextFieldTitled;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import settings.UserSettings;

import javax.mail.MessagingException;

import static game.menu.Menu.main_menu_intro_sound;
import static game.menu.Menu.main_menu_music;
import static game.menu.screens.MainScreen.MENU_OPTION_HEIGHT;

public class FeedbackScreen extends AbstractMenuScreen {

    private static TextFieldTitled tf_feedback, tf_name, tf_email;
    private Button sendBtn;
    private boolean sendBtnDisabled;

    private static final TrueTypeFont menu_drawer;
    private int back_btn_width, back_btn_height;
    private Vector2f back_btn_position;
    private Arrow arrow;

    private static TrueTypeFont status_text_drawer;
    private String statusText;
    private boolean showStatusText;
    private Vector2f statusTextPosition;

    static {
        menu_drawer = FontManager.getStencilBigFont();
        status_text_drawer = FontManager.getConsoleOutputFont(false);
    }

    public FeedbackScreen(BasicGameState gameState, GameContainer gc) {
        super(gameState);

        back_btn_width = menu_drawer.getWidth("BACK");
        back_btn_height = MENU_OPTION_HEIGHT;

        final int MARGIN = 30;
        int y_pos = gc.getHeight() / 6;
        back_btn_position = new Vector2f(gc.getWidth() / 2.f - back_btn_width / 2.f, y_pos);
        y_pos += back_btn_height + MARGIN;
        int textFieldHeight = 30;
        int textFieldWidth = gc.getWidth() - 100;
        tf_feedback = new TextFieldTitled(gc, FontManager.getConsoleOutputFont(false),
                gc.getWidth() / 2 - textFieldWidth / 2, y_pos,
                textFieldWidth, textFieldHeight,
                "Provide feedback below! (Bug Reports, Suggestions, Requests, Opinions etc.)"
        );
        y_pos += textFieldHeight + MARGIN;
        textFieldHeight = 20;
        textFieldWidth = 200;
        tf_name = new TextFieldTitled(gc, FontManager.getConsoleOutputFont(false),
                gc.getWidth() / 2 - textFieldWidth / 2, y_pos,
                textFieldWidth, textFieldHeight,
                "Your Name:"
        );
        y_pos += textFieldHeight + MARGIN;
        tf_email = new TextFieldTitled(gc, FontManager.getConsoleOutputFont(false),
                gc.getWidth() / 2 - textFieldWidth / 2, y_pos,
                textFieldWidth, textFieldHeight,
                "Your E-Mail:"
        );
        y_pos += textFieldHeight + MARGIN;
        final int sendBtnHeight = 40;
        sendBtn = new Button("SEND", gc.getWidth() / 2 - 50, y_pos, 100, sendBtnHeight);

        statusTextPosition = new Vector2f(gc.getWidth() / 2.f, y_pos + sendBtnHeight + MARGIN / 4.f);

        arrow = new Arrow(gc, 1, (int) back_btn_position.y);
        activateTextFields(false);
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
        tf_feedback.render(gc, gc.getGraphics());
        tf_name.render(gc, gc.getGraphics());
        tf_email.render(gc, gc.getGraphics());
        sendBtn.draw(gc.getGraphics());
        menu_drawer.drawString(back_btn_position.x, back_btn_position.y, "BACK", Color.lightGray);
        arrow.draw();
        if (showStatusText) {
            status_text_drawer.drawString(statusTextPosition.x - status_text_drawer.getWidth(statusText) / 2.f,
                    statusTextPosition.y,
                    statusText,
                    Color.lightGray);
        }
        Menu.drawInfoStrings(gc);
    }

    @Override
    public void handleKeyInput(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        if (gameContainer.getInput().isKeyPressed(Input.KEY_ESCAPE) || gameContainer.getInput().isKeyPressed(Input.KEY_ENTER)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
            reset();
            Menu.returnToPreviousMenu();
        }
    }

    @Override
    public void onMouseClick(GameContainer gameContainer, StateBasedGame stateBasedGame, int mouseX, int mouseY) {
        if (sendBtn.isMouseOver(mouseX, mouseY)) {
            if (sendBtnDisabled) {
                printStatus("Feedback was already sent!");
                return;
            }
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
            if (tf_feedback.getText().isEmpty()) {
                printStatus("Feedback text is empty!");
                return;
            }
            printStatus("Sending ...");

            Thread sendFeedbackThread = new Thread(() -> {
                try {
                    MailUtil.sendFeedbackMail(tf_name.getText(), tf_email.getText(), tf_feedback.getText());
                } catch (Exception e) {
                    e.printStackTrace();
                    printStatus("Failed sending feedback!");
                }
                printStatus("Feedback sent!");
            });
            sendFeedbackThread.start();
            sendBtnDisabled = true;
            return;
        }

        if (mouseX > back_btn_position.x && mouseX < back_btn_position.x + back_btn_width) {
            if (mouseY > back_btn_position.y && mouseY < back_btn_position.y + back_btn_height) {
                MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
                reset();
                Menu.returnToPreviousMenu();
                return;
            }
        }

        if (!tf_feedback.isAcceptingInput()) {
            activateTextFields(true);
        }
    }

    private void printStatus(String msg) {
        statusText = msg;
        showStatusText = true;
    }

    @Override
    public void onEnterState(GameContainer gameContainer) {
    }

    @Override
    public void onLeaveState(GameContainer gameContainer) {
    }

    private void reset() {
        sendBtnDisabled = false;
        statusText = "";
        showStatusText = false;
        tf_feedback.setText("");
        tf_name.setText("");
        tf_email.setText("");
        activateTextFields(false);
    }

    /* this is needed because of a slick bug that looks at all existing text fields, even of diff states ... */
    /* the text fields are disabled manually on leave here so it does not mess up the text fields in level editor */
    public static void activateTextFields(boolean val) {
        tf_feedback.setAcceptingInput(val);
        tf_name.setAcceptingInput(val);
        tf_email.setAcceptingInput(val);
    }
}
