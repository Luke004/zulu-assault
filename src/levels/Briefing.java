package levels;

import audio.SoundManager;
import main.ZuluAssault;
import settings.UserSettings;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import static menus.MainMenu.*;


public class Briefing extends BasicGameState {

    private GameContainer gameContainer;
    private StateBasedGame stateBasedGame;

    private Thread loadMusicThread;

    private int nextLevelID;

    private List<String> briefing_message;
    private String briefing_header, briefing_mission_header, confirm_message, mission_name;
    private static TrueTypeFont ttf_info_string;
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
    public void enter(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        briefing_music_intro.play(1.f, UserSettings.MUSIC_VOLUME);
        gameContainer.setMouseGrabbed(true);    // hide the mouse cursor
        this.nextLevelID = ZuluAssault.nextLevelID;
        AbstractLevel level = (AbstractLevel) stateBasedGame.getState(nextLevelID);
        this.mission_name = "Mission " + nextLevelID;
        String briefing_message = level.getBriefingMessage();
        this.briefing_message = new ArrayList<>();
        if (briefing_message == null || briefing_message.isEmpty()) {
            this.briefing_message.add("The creator did not create a briefing message. Good luck!");
        } else {
            String[] split_strings = briefing_message.split("\\s+");
            StringBuilder builder = new StringBuilder();

            for (String next_part : split_strings) {
                builder.append(next_part).append(" ");
                if (ttf_info_string.getWidth(builder.toString()) > gameContainer.getWidth() - 50) {
                    this.briefing_message.add(builder.toString());
                    builder.setLength(0);
                }
            }
            if (builder.length() > 0) {
                this.briefing_message.add(builder.toString());
            }
        }

        loadMusicThread = new LoadMusicThread();
        loadMusicThread.start();

        this.briefing_mission_header = this.mission_name + " - " + level.getMissionTitle();
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        if (!has_initialized_once) {
            // this gets only executed once
            has_initialized_once = true;
            this.gameContainer = gameContainer;
            this.stateBasedGame = stateBasedGame;
            Font awtFont = new Font("DialogInput", Font.PLAIN, 14);
            ttf_info_string = new TrueTypeFont(awtFont, false);
            MESSAGE_Y_START = gameContainer.getHeight() / 8.f;
            this.confirm_message = "Press a key to start mission.";
            this.briefing_header = "ZULU TEAM - BRIEFING";
            MESSAGE_HEIGHT = ttf_info_string.getHeight(briefing_header);
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
                briefing_music.setVolume(UserSettings.MUSIC_VOLUME);
            }
        }
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) {
        briefing_screen_image.draw();

        ttf_info_string.drawString(
                TEXT_MARGIN,
                MESSAGE_Y_START,
                briefing_header);

        ttf_info_string.drawString(
                TEXT_MARGIN,
                MESSAGE_Y_START + MESSAGE_HEIGHT,
                briefing_mission_header);

        for (int idx = 0; idx < briefing_message.size(); ++idx) {
            ttf_info_string.drawString(
                    TEXT_MARGIN,
                    MESSAGE_Y_START + MESSAGE_HEIGHT * (3 + idx),
                    briefing_message.get(idx));
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
        if (loadMusicThread.isAlive()) return;
        SoundManager.CLICK_SOUND.play(1.f, UserSettings.SOUND_VOLUME);
        try {
            stateBasedGame.getState(nextLevelID).init(gameContainer, stateBasedGame);
            stateBasedGame.enterState(nextLevelID,
                    new FadeOutTransition(), new FadeInTransition());
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void leave(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        briefing_music_intro.stop();
        briefing_music.stop();
        ZuluAssault.prevState = this;
    }

    class LoadMusicThread extends Thread {
        @Override
        public void run() {
            ((AbstractLevel) stateBasedGame.getState(nextLevelID)).loadLevelMusic();
        }
    }

}
