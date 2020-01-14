package menus;

import main.ZuluAssault;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

public class MainMenu extends BasicGameState {

    // STATES
    final int STATE_STARTED = 0, STATE_IN_GAME = 1, STATE_IN_LOAD = 2, STATE_IN_SAVE = 3, STATE_IN_OPTIONS = 4;
    int state, prev_state;

    private Arrow menu_arrow;
    private Image main_menu_image, resume_image, back_image;
    private Vector2f main_menu_image_position;
    private String info_string, title_string;
    private static boolean firstCall_leave = true, firstCall_enter = true;

    private Sound sound_before_main, click_sound;
    private Music main_menu_music;

    private TrueTypeFont ttf_info_string, ttf_title_string;

    public MainMenu() {
        try {
            click_sound = new Sound("audio/sounds/click.ogg");
            sound_before_main = new Sound("audio/sounds/before_main.ogg");
            main_menu_music = new Music("audio/music/main_menu.ogg");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    private void setState(int state) {
        this.prev_state = this.state;
        this.state = state;
        menu_arrow.switchState(state);
    }

    @Override
    public int getID() {
        return ZuluAssault.MAIN_MENU;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        Font awtFont = new Font("DialogInput", Font.PLAIN, 12);
        ttf_info_string = new TrueTypeFont(awtFont, false);
        awtFont = new Font("Courier", Font.BOLD, 50);
        ttf_title_string = new TrueTypeFont(awtFont, false);

        title_string = "ZULU ASSAULT";
        info_string = "(C) 1998 Dallas Nutsch - Alpha Test Version Rebuild by Lukas Hilfrich";

        try {
            main_menu_image = new Image("assets/menus/main_menu.png");
            resume_image = new Image("assets/menus/resume.png");
            back_image = new Image("assets/menus/back.png");
            main_menu_image_position = new Vector2f(
                    gameContainer.getWidth() / 2.f - main_menu_image.getWidth() / 2.f,
                    gameContainer.getHeight() / 2.f);
            // set custom animated mouse cursor
            gameContainer.setAnimatedMouseCursor(
                    "assets/menus/animated_cursor.tga",
                    0,
                    0,
                    32,
                    32,
                    new int[]{20, 20, 20, 20}
            );
        } catch (SlickException e) {
            e.printStackTrace();
        }
        // the menu arrow pointing at the current selection to enter
        menu_arrow = new Arrow();
        setState(STATE_STARTED);
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) {

        switch (state) {
            case STATE_IN_GAME:
                // add resume option
                resume_image.draw(main_menu_image_position.x, main_menu_image_position.y - resume_image.getHeight());
                // no break
            case STATE_STARTED:
                main_menu_image.draw(main_menu_image_position.x, main_menu_image_position.y);
                break;
            case STATE_IN_OPTIONS:
                back_image.draw(main_menu_image_position.x, main_menu_image_position.y);
                break;

        }

        ttf_title_string.drawString(gameContainer.getWidth() / 2.f - ttf_title_string.getWidth(title_string) / 2.f,
                gameContainer.getHeight() / 4.f - ttf_title_string.getHeight(title_string) / 2.f,
                title_string,
                Color.lightGray);

        ttf_info_string.drawString(
                5,
                gameContainer.getHeight() - ttf_info_string.getHeight() - 5,
                info_string);

        menu_arrow.draw();
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) {
        if (gameContainer.getInput().isKeyPressed(Input.KEY_DOWN)) {
            click_sound.play();
            menu_arrow.moveDown();
        }
        if (gameContainer.getInput().isKeyPressed(Input.KEY_UP)) {
            click_sound.play();
            menu_arrow.moveUp();
        }

        if (gameContainer.getInput().isKeyPressed(Input.KEY_ENTER)) {
            click_sound.play();

            switch (state) {
                case STATE_IN_GAME:
                case STATE_STARTED:
                    switch (menu_arrow.currIdx) {
                        case 0: // NEW
                            // START NEW GAME
                            try {
                                // TODO: FIX BUG THAT MAKES INIT ADD A LEVEL ON TOP INSTEAD OF OVERRIDING
                                stateBasedGame.getState(ZuluAssault.LEVEL_1).init(gameContainer, stateBasedGame);
                                stateBasedGame.enterState(ZuluAssault.LEVEL_1);
                            } catch (SlickException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 3:
                            setState(STATE_IN_OPTIONS);
                            break;
                        case 4: // EXIT
                            System.exit(0);
                        case 5: // RESUME TO GAME
                            stateBasedGame.enterState(ZuluAssault.prevState.getID());
                            break;
                    }
                    break;
                case STATE_IN_OPTIONS:
                    switch (menu_arrow.currIdx) {
                        case 0: // BACK
                            setState(prev_state);
                            break;
                        case 1: // MUSIC VOLUME
                            //TODO
                            break;
                        case 2: // SOUND VOLUME
                            //TODO
                            break;
                    }
                    break;
            }

        }

        if (!sound_before_main.playing()) {
            if (!main_menu_music.playing()) {
                main_menu_music.play();
                main_menu_music.loop();
            }
        }
    }


    @Override
    public void enter(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        if (firstCall_enter) {  // this is needed because this GameState is entered twice on startup
            firstCall_enter = false;
            return;
        }
        // show the mouse cursor
        gameContainer.setMouseGrabbed(false);
        sound_before_main.play();

        if (ZuluAssault.prevState != null) {
            // a previous state exists -> user is in game -> switch states
            setState(STATE_IN_GAME);
        }
    }

    @Override
    public void leave(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        if (firstCall_leave) {  // this is needed because this GameState is entered twice on startup
            firstCall_leave = false;
            return;
        }
        sound_before_main.stop();
        main_menu_music.stop();
    }

    private class Arrow {
        private final int Y_POS_START = 305, Y_OFFSET = 40;
        private Image arrow_image;

        private List<Integer> menu_positions;
        private int currIdx, currMenuSize;

        Arrow() {
            try {
                arrow_image = new Image("assets/menus/arrow.png");
            } catch (SlickException e) {
                e.printStackTrace();
            }

            currIdx = 0;

            menu_positions = new ArrayList<>();
            int yPos = Y_POS_START;
            for (int i = 0; i < 5; ++i) {
                menu_positions.add(yPos);
                yPos += Y_OFFSET;
            }
        }

        void draw() {
            arrow_image.draw(240, menu_positions.get(currIdx));
        }

        void moveDown() {
            if (currIdx == currMenuSize - 1) {
                currIdx = 0;
            } else currIdx++;
        }

        void moveUp() {
            if (currIdx == 0) {
                currIdx = currMenuSize - 1;
            } else currIdx--;
        }

        void addResumeOption() {
            if (menu_positions.size() == 5) {    // only add resume option once of course
                // add resume option at index 5
                menu_positions.add(Y_POS_START - Y_OFFSET);
            }
        }

        void switchState(int state) {
            switch (state) {
                case STATE_STARTED:
                    currMenuSize = 5;
                    currIdx = 0;
                    break;
                case STATE_IN_GAME:
                    addResumeOption();
                    currMenuSize = 6;
                    currIdx = 5;
                    break;
                case STATE_IN_OPTIONS:
                    currMenuSize = 3;
                    currIdx = 0;
                    break;
            }

        }
    }
}
