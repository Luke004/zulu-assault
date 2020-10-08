package settings;

import logic.SettingsManager;

public class UserSettings {

    // audio volume
    public static float soundVolume, musicVolume;
    public static final int VOLUME_MAX_LEVEL = 10;
    public static int soundVolumeLevel, musicVolumeLevel;

    // display elements
    public static boolean displayTime;

    static {
        // load sound level
        String sound_value = SettingsManager.loadSetting("sound_volume_level");
        if (sound_value.isEmpty()) {
            // sound setting doesn't exist -> create one and use default setting
            SettingsManager.storeSetting(new SettingsManager.Property("sound_volume_level",
                    Integer.toString(UserSettings.VOLUME_MAX_LEVEL)));
            UserSettings.setSoundVolume(UserSettings.VOLUME_MAX_LEVEL);
        } else {
            UserSettings.setSoundVolume(Integer.parseInt(sound_value));
        }
        // load music level
        String music_value = SettingsManager.loadSetting("music_volume_level");
        if (music_value.isEmpty()) {
            // music setting doesn't exist -> create one and use default setting
            SettingsManager.storeSetting(new SettingsManager.Property("music_volume_level",
                    Integer.toString(UserSettings.VOLUME_MAX_LEVEL)));
            UserSettings.setMusicVolume(UserSettings.VOLUME_MAX_LEVEL);
        } else {
            UserSettings.setMusicVolume(Integer.parseInt(music_value));
        }
        // load time display
        String time_display_value = SettingsManager.loadSetting("show_time");
        if (time_display_value.isEmpty()) {
            // setting doesn't exist -> store new one and use default
            SettingsManager.storeSetting(new SettingsManager.Property("show_time", "false"));
            // displayTime = false; // -> redundant
        } else {
            displayTime = time_display_value.equals("true");
        }
    }

    public static void setSoundVolume(int level) {
        soundVolume = level / (float) VOLUME_MAX_LEVEL;
        soundVolumeLevel = level;
    }

    public static void setMusicVolume(int level) {
        musicVolume = level / (float) VOLUME_MAX_LEVEL;
        musicVolumeLevel = level;
    }

}
