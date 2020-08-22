package audio;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import settings.UserSettings;

import java.util.ArrayList;
import java.util.List;

/*
    This class handles the music that is played in the background during combat.
    NOTICE: In this game, each in game background music has its own intro that is only played ONCE at the beginning
            -> background_intro_sound
            After that, the main background music follows and is looped infinitely
            -> background_music
 */

public class CombatBackgroundMusic {

    private static int idx;
    private static List<Sound> combat_background_intro_sounds;
    private static List<Music> combat_background_music;
    private static List<Integer> loadedSoundIndexList;

    static {
        combat_background_intro_sounds = new ArrayList<>();
        combat_background_music = new ArrayList<>();
        loadedSoundIndexList = new ArrayList<>();
    }

    public void setIdx(int m_idx) {
        idx = m_idx;
    }

    public void start() {
        combat_background_intro_sounds.get(idx).play(1.f, UserSettings.MUSIC_VOLUME);
    }

    public void update() {
        if (!combat_background_intro_sounds.get(idx).playing()) {
            if (!combat_background_music.get(idx).playing()) {
                combat_background_music.get(idx).play();
                combat_background_music.get(idx).loop();
                combat_background_music.get(idx).setVolume(UserSettings.MUSIC_VOLUME);
            }
        }
    }

    public void stop() {
        combat_background_intro_sounds.get(idx).stop();
        combat_background_music.get(idx).stop();
    }

    public static void load(int idx) {
        if (loadedSoundIndexList.contains(idx)) return;  // return when sound has already been loaded
        try {
            switch (idx) {
                case 0:
                    combat_background_music.add(new Music("audio/music/level_1.ogg"));
                    combat_background_intro_sounds.add(new Sound("audio/music/level_1_intro.ogg"));
                case 1:
                    combat_background_music.add(new Music("audio/music/level_2.ogg"));
                    combat_background_intro_sounds.add(new Sound("audio/music/level_2_intro.ogg"));
                case 2:
                    combat_background_music.add(new Music("audio/music/level_3.ogg"));
                    combat_background_intro_sounds.add(new Sound("audio/music/level_3_intro.ogg"));
                case 3:
                    combat_background_music.add(new Music("audio/music/level_4.ogg"));
                    combat_background_intro_sounds.add(new Sound("audio/music/level_4_intro.ogg"));
                case 4:
                    combat_background_music.add(new Music("audio/music/level_5.ogg"));
                    combat_background_intro_sounds.add(new Sound("audio/music/level_5_intro.ogg"));
                default:
                    combat_background_music.add(new Music("audio/music/level_1.ogg"));
                    combat_background_intro_sounds.add(new Sound("audio/music/level_1_intro.ogg"));
            }
            loadedSoundIndexList.add(idx);
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

}
