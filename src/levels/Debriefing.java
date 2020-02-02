package levels;

import java.awt.Font;

import main.ZuluAssault;
import menus.UserSettings;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import static menus.MainMenu.TEXT_MARGIN;


public class Debriefing extends BasicGameState {

    private GameContainer gameContainer;
    private StateBasedGame stateBasedGame;

    private int finished_level_ID;

    private String debriefing_header, debriefing_mission_header, debriefing_message, confirm_message, mission_name;
    private static TrueTypeFont ttf_info_string;
    private static boolean has_initialized_once;
    private static Image debriefing_screen_image;
    private static Sound debriefing_music;
    private float MESSAGE_Y_START, MESSAGE_HEIGHT;

    @Override
    public int getID() {
        return ZuluAssault.DEBRIEFING;
    }

    @Override
    public void enter(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        debriefing_music.play(1.f, UserSettings.SOUND_VOLUME);

        this.finished_level_ID = ZuluAssault.prevState.getID();
        if (finished_level_ID > 0) {
            AbstractLevel level = (AbstractLevel) ZuluAssault.prevState;
            this.mission_name = "Mission " + finished_level_ID;
            this.debriefing_message = level.getDebriefingMessage();
            this.debriefing_mission_header = this.mission_name + " - " + level.getMissionTitle();
        } else {
            this.debriefing_message = "You have won the level. Well done!";
        }
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        if (!has_initialized_once) {
            // this gets only executed once
            has_initialized_once = true;
            this.gameContainer = gameContainer;
            this.stateBasedGame = stateBasedGame;
            Font awtFont = new java.awt.Font("DialogInput", java.awt.Font.PLAIN, 14);
            ttf_info_string = new TrueTypeFont(awtFont, false);
            MESSAGE_Y_START = gameContainer.getHeight() / 8.f;
            this.confirm_message = "Press a key to confirm orders.";
            this.debriefing_header = "ZULU TEAM - DEBRIEFING";
            MESSAGE_HEIGHT = ttf_info_string.getHeight(debriefing_header);
            debriefing_music = new Sound("audio/music/debriefing.ogg");
            debriefing_screen_image = new Image("assets/menus/debriefing.png");
        }
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) {
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {
        debriefing_screen_image.draw();

        ttf_info_string.drawString(
                TEXT_MARGIN,
                MESSAGE_Y_START,
                debriefing_header);

        ttf_info_string.drawString(
                TEXT_MARGIN,
                MESSAGE_Y_START + MESSAGE_HEIGHT,
                debriefing_mission_header);

        ttf_info_string.drawString(
                TEXT_MARGIN,
                MESSAGE_Y_START + MESSAGE_HEIGHT * 3,
                debriefing_message);

        ttf_info_string.drawString(
                TEXT_MARGIN,
                gameContainer.getHeight() - ttf_info_string.getHeight(mission_name) - TEXT_MARGIN,
                mission_name);

        ttf_info_string.drawString(
                gameContainer.getWidth() / 2.f - ttf_info_string.getWidth(confirm_message) / 2.f,
                gameContainer.getHeight() - ttf_info_string.getHeight(confirm_message) - TEXT_MARGIN,
                confirm_message);
    }

    @Override
    public void keyPressed(int key, char c) {
        try {
            final int NEXT_LEVEL_ID = finished_level_ID + 1;
            stateBasedGame.getState(NEXT_LEVEL_ID).init(gameContainer, stateBasedGame);
            stateBasedGame.enterState(NEXT_LEVEL_ID,
                    new FadeOutTransition(), new FadeInTransition());
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void leave(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        debriefing_music.stop();
        ZuluAssault.prevState = this;
    }
}
