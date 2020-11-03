package game.levels;

import game.audio.CombatBackgroundMusic;
import game.audio.MenuSounds;
import game.graphics.fonts.FontManager;
import game.util.LevelDataStorage;
import game.util.StringUtil;
import main.ZuluAssault;
import settings.UserSettings;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import java.util.ArrayList;
import java.util.List;

import static game.menu.Menu.*;


public class Briefing extends BasicGameState {

    private GameContainer gameContainer;
    private StateBasedGame stateBasedGame;

    private Thread loadMusicThread;
    private int musicIdx;

    private List<String> briefing_message_line_split_list;
    private String briefing_header, briefing_mission_header, confirm_message, mission_title_abstract;
    private static TrueTypeFont text_drawer;
    private static boolean has_initialized_once;
    private static Image briefing_screen_image;
    private static Sound briefing_music_intro;
    private static Music briefing_music;
    private float MESSAGE_Y_START, MESSAGE_HEIGHT;

    @Override
    public int getID() {
        return ZuluAssault.BRIEFING;
    }

    @Override
    public void enter(GameContainer gc, StateBasedGame sbg) {
        briefing_music_intro.play(1.f, UserSettings.musicVolume);
        gc.setMouseGrabbed(true);    // hide the mouse cursor

        boolean isStandardLevel = Level.isStandardLevel(ZuluAssault.nextLevelName);

        LevelDataStorage lds = LevelDataStorage.loadLevel(ZuluAssault.nextLevelName, isStandardLevel);

        if (lds != null) {
            this.musicIdx = lds.musicIdx;
            String s_briefingMessage = lds.briefing_message;
            String mission_title_detailed = lds.mission_title;
            this.briefing_message_line_split_list = new ArrayList<>();

            if (s_briefingMessage.isEmpty()) {
                this.briefing_message_line_split_list.add("The creator did not create a briefing message. Good luck!");
            } else {
                this.briefing_message_line_split_list = StringUtil.getLineSplitMessageList(
                        s_briefingMessage,
                        gameContainer.getWidth() - TEXT_MARGIN * 2,
                        text_drawer
                );
            }
            loadMusicThread = new LoadMusicThread();
            loadMusicThread.start();

            if (isStandardLevel) {
                mission_title_abstract = "Mission " + ZuluAssault.nextLevelName.substring(ZuluAssault.nextLevelName.indexOf("_") + 1);
            } else {
                mission_title_abstract = "Custom Mission";
            }
            this.briefing_mission_header = mission_title_abstract + " - " + mission_title_detailed;
        }
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        if (!has_initialized_once) {
            // this gets only executed once
            has_initialized_once = true;
            this.gameContainer = gameContainer;
            this.stateBasedGame = stateBasedGame;
            text_drawer = FontManager.getConsoleInputFont();
            MESSAGE_Y_START = gameContainer.getHeight() / 8.f;
            this.confirm_message = "Press a key to start mission.";
            this.briefing_header = "ZULU TEAM - BRIEFING";
            MESSAGE_HEIGHT = text_drawer.getHeight(briefing_header);
            briefing_music_intro = new Sound("audio/music/briefing_intro.ogg");
            briefing_music = new Music("audio/music/briefing.ogg");
            briefing_screen_image = new Image("assets/menus/briefing.png");
        }
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) {
        if (!briefing_music_intro.playing()) {
            if (!briefing_music.playing()) {
                briefing_music.play();
                briefing_music.loop();
                briefing_music.setVolume(UserSettings.musicVolume);
            }
        }
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) {
        briefing_screen_image.draw();

        text_drawer.drawString(
                TEXT_MARGIN,
                MESSAGE_Y_START,
                briefing_header);

        text_drawer.drawString(
                TEXT_MARGIN,
                MESSAGE_Y_START + MESSAGE_HEIGHT,
                briefing_mission_header);

        for (int idx = 0; idx < briefing_message_line_split_list.size(); ++idx) {
            text_drawer.drawString(
                    TEXT_MARGIN,
                    MESSAGE_Y_START + MESSAGE_HEIGHT * (3 + idx),
                    briefing_message_line_split_list.get(idx));
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
        if (loadMusicThread.isAlive()) return;
        MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
        try {
            stateBasedGame.getState(ZuluAssault.IN_LEVEL).init(gameContainer, stateBasedGame);
            stateBasedGame.enterState(ZuluAssault.IN_LEVEL, new FadeOutTransition(), new FadeInTransition());
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void leave(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        briefing_music_intro.stop();
        briefing_music.stop();
        ZuluAssault.prevState = this;
        gameContainer.getInput().clearKeyPressedRecord();
    }

    class LoadMusicThread extends Thread {
        @Override
        public void run() {
            CombatBackgroundMusic.load(musicIdx);
        }
    }

}
