package game.audio;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import settings.UserSettings;

/*
    This class handles the music that is played in the background during combat.
    NOTICE: In this game, each in game background music has its own intro that is only played ONCE at the beginning
            -> 'combat_background_intro_sounds'
            After that, the main background music follows and is looped infinitely
            -> 'combat_background_music'
 */

public class CombatBackgroundMusic {

    private static final int COMBAT_MUSIC_SIZE = 5;     // there are 5 combat music songs

    private static Sound[] combat_background_intro_sound_list;
    private static Music[] combat_background_music_list;

    private static int idx;

    static {
        combat_background_intro_sound_list = new Sound[COMBAT_MUSIC_SIZE];
        combat_background_music_list = new Music[COMBAT_MUSIC_SIZE];
    }

    public void setIdx(int m_idx) {
        idx = m_idx;
    }

    public void start() {
        combat_background_intro_sound_list[idx].play(1.f, UserSettings.musicVolume);
    }

    public void update() {
        if (!combat_background_intro_sound_list[idx].playing()) {
            if (!combat_background_music_list[idx].playing()) {
                combat_background_music_list[idx].play();
                combat_background_music_list[idx].loop();
                combat_background_music_list[idx].setVolume(UserSettings.musicVolume);
            }
        }
    }

    public void stop() {
        combat_background_intro_sound_list[idx].stop();
        combat_background_music_list[idx].stop();
    }

    public static void load(int idx) {
        if (combat_background_music_list[idx] != null) return;  // return when sound has already been loaded
        try {
            switch (idx) {
                case 1:
                    combat_background_music_list[idx] = new Music("audio/music/level_2.ogg");
                    combat_background_intro_sound_list[idx] = new Sound("audio/music/level_2_intro.ogg");
                    break;
                case 2:
                    combat_background_music_list[idx] = new Music("audio/music/level_3.ogg");
                    combat_background_intro_sound_list[idx] = new Sound("audio/music/level_3_intro.ogg");
                    break;
                case 3:
                    combat_background_music_list[idx] = new Music("audio/music/level_4.ogg");
                    combat_background_intro_sound_list[idx] = new Sound("audio/music/level_4_intro.ogg");
                    break;
                case 4:
                    combat_background_music_list[idx] = new Music("audio/music/level_5.ogg");
                    combat_background_intro_sound_list[idx] = new Sound("audio/music/level_5_intro.ogg");
                    break;
                default:
                    combat_background_music_list[idx] = new Music("audio/music/level_1.ogg");
                    combat_background_intro_sound_list[idx] = new Sound("audio/music/level_1_intro.ogg");
                    break;
            }
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

}
