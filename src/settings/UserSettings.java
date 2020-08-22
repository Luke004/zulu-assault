package settings;

public class UserSettings {
    public static float SOUND_VOLUME, MUSIC_VOLUME;
    public static final int VOLUME_MAX_LEVEL = 10;
    public static int SOUND_VOLUME_LEVEL, MUSIC_VOLUME_LEVEL;


    public static void setSoundVolume(int level) {
        SOUND_VOLUME = level / (float) VOLUME_MAX_LEVEL;
        SOUND_VOLUME_LEVEL = level;
    }

    public static void setMusicVolume(int level) {
        MUSIC_VOLUME = level / (float) VOLUME_MAX_LEVEL;
        MUSIC_VOLUME_LEVEL = level;
    }
}
