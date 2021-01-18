package game.levels;

import java.util.ArrayList;
import java.util.List;

import game.audio.MenuSounds;
import game.graphics.fonts.FontManager;
import game.util.LevelDataStorage;
import game.util.StringUtil;
import main.ZuluAssault;
import settings.UserSettings;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import static game.menu.Menu.TEXT_MARGIN;


public class Debriefing extends BasicGameState {

    //private GameContainer gameContainer;
    //private StateBasedGame stateBasedGame;

    private String finished_level_Name;

    private List<String> debriefing_message_line_split_list;
    private String debriefing_header, debriefing_mission_header, confirm_message, mission_title_abstract;
    private static TrueTypeFont text_drawer;
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
        debriefing_music.play(1.f, UserSettings.musicVolume);

        boolean isStandardLevel = Level.isStandardLevel(ZuluAssault.nextLevelName);
        LevelDataStorage lds = LevelDataStorage.loadLevel(ZuluAssault.nextLevelName, isStandardLevel);

        if (lds != null) {
            String s_debriefing_message = lds.debriefing_message;
            String mission_title_detailed = lds.mission_title;
            this.finished_level_Name = ZuluAssault.nextLevelName;
            this.debriefing_message_line_split_list = new ArrayList<>();
            if (s_debriefing_message == null || s_debriefing_message.isEmpty()) {
                this.debriefing_message_line_split_list.add("You have won the level. Well done!");
            } else {
                this.debriefing_message_line_split_list = StringUtil.getLineSplitMessageList(
                        s_debriefing_message,
                        gameContainer.getWidth() - TEXT_MARGIN * 2,
                        text_drawer
                );
            }

            if (isStandardLevel) {
                mission_title_abstract = "Mission " + ZuluAssault.nextLevelName.substring(ZuluAssault.nextLevelName.indexOf("_") + 1);
            } else {
                mission_title_abstract = "Custom Mission";
            }
            this.debriefing_mission_header = mission_title_abstract + " - " + mission_title_detailed;
        }


    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        if (!has_initialized_once) {
            // this gets only executed once
            has_initialized_once = true;
            text_drawer = FontManager.getConsoleInputFont();
            MESSAGE_Y_START = gameContainer.getHeight() / 8.f;
            this.confirm_message = "Press a key to confirm orders.";
            this.debriefing_header = "ZULU TEAM - DEBRIEFING";
            MESSAGE_HEIGHT = text_drawer.getHeight(debriefing_header);
            debriefing_music = new Sound("audio/music/debriefing.ogg");
            debriefing_screen_image = new Image("assets/menus/debriefing.png");
        }
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) {
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) {
        debriefing_screen_image.draw();

        text_drawer.drawString(
                TEXT_MARGIN,
                MESSAGE_Y_START,
                debriefing_header);

        text_drawer.drawString(
                TEXT_MARGIN,
                MESSAGE_Y_START + MESSAGE_HEIGHT,
                debriefing_mission_header);

        for (int idx = 0; idx < debriefing_message_line_split_list.size(); ++idx) {
            text_drawer.drawString(
                    TEXT_MARGIN,
                    MESSAGE_Y_START + MESSAGE_HEIGHT * (3 + idx),
                    debriefing_message_line_split_list.get(idx));
        }

        text_drawer.drawString(
                TEXT_MARGIN,
                gameContainer.getHeight() - text_drawer.getHeight(mission_title_abstract) - TEXT_MARGIN,
                mission_title_abstract);

        text_drawer.drawString(
                gameContainer.getWidth() / 2.f - text_drawer.getWidth(confirm_message) / 2.f,
                gameContainer.getHeight() - text_drawer.getHeight(confirm_message) - TEXT_MARGIN,
                confirm_message);
    }

    @Override
    public void keyPressed(int key, char c) {
        MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);

        if (LevelManager.playerIsInPlayThrough()) {
            int idx = finished_level_Name.indexOf("_");
            String s_nextLevel = finished_level_Name.substring(idx + 1);
            int nextLevelID = Integer.parseInt(s_nextLevel) + 1;

            // TODO: remove this, this is temporary for the alpha version
            if (nextLevelID > ZuluAssault.MAX_LEVEL) {      // there is no more level
                LevelManager.goToMainMenu();
                return;
            }

            s_nextLevel = "map_" + nextLevelID;
            LevelManager.startNextLevel(s_nextLevel, this);
        } else {
            LevelManager.goToMainMenu();
        }
    }

    @Override
    public void leave(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        debriefing_music.stop();
        ZuluAssault.prevState = this;
        gameContainer.getInput().clearKeyPressedRecord();
    }
}
