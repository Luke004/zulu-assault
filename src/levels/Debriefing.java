package levels;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import main.SoundManager;
import main.ZuluAssault;
import menus.MainScreen;
import menus.UserSettings;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import static menus.MainMenu.TEXT_MARGIN;


public class Debriefing extends BasicGameState {

    private GameContainer gameContainer;
    private StateBasedGame stateBasedGame;

    private int finished_level_ID;

    private List<String> debriefing_message;
    private String debriefing_header, debriefing_mission_header, confirm_message, mission_name;
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
        debriefing_music.play(1.f, UserSettings.MUSIC_VOLUME);

        this.finished_level_ID = ZuluAssault.nextLevelID;
        AbstractLevel level = (AbstractLevel) ZuluAssault.prevState;
        this.mission_name = "Mission " + finished_level_ID;
        String debriefing_message = level.getDebriefingMessage();
        this.debriefing_message = new ArrayList<>();
        if (debriefing_message == null || debriefing_message.isEmpty()) {
            this.debriefing_message.add("You have won the level. Well done!");
        } else {
            String[] split_strings = debriefing_message.split("\\s+");
            StringBuilder builder = new StringBuilder();

            for (String next_part : split_strings) {
                builder.append(next_part).append(" ");
                if (ttf_info_string.getWidth(builder.toString()) > gameContainer.getWidth() - 40) {
                    this.debriefing_message.add(builder.toString());
                    builder.setLength(0);
                }
            }
            if (builder.length() > 0) {
                this.debriefing_message.add(builder.toString());
            }
        }
        this.debriefing_mission_header = this.mission_name + " - " + level.getMissionTitle();
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

        for (int idx = 0; idx < debriefing_message.size(); ++idx) {
            ttf_info_string.drawString(
                    TEXT_MARGIN,
                    MESSAGE_Y_START + MESSAGE_HEIGHT * (3 + idx),
                    debriefing_message.get(idx));
        }

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
        SoundManager.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
        final int NEXT_LEVEL_ID = finished_level_ID + 1;
        MainScreen.startLevel(NEXT_LEVEL_ID, stateBasedGame, this);
    }

    @Override
    public void leave(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        debriefing_music.stop();
        ZuluAssault.prevState = this;
    }
}
