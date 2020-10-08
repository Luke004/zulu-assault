package settings;

import logic.SettingsManager;

public class UserSettings {

    // audio volume
    public static float soundVolume, musicVolume;
    public static final int VOLUME_MAX_LEVEL = 10;
    public static int soundVolumeLevel, musicVolumeLevel;

    // display elements
    public static boolean displayTime;

    // 2 keyboard layouts (Y and Z switched)
    public static boolean keyboardLayout_1;

    static {
        // load sound level
        String sound_value = SettingsManager.loadSetting("sound_volume_level",
                Integer.toString(UserSettings.VOLUME_MAX_LEVEL));
        if (!sound_value.isEmpty()) {
            UserSettings.setSoundVolume(Integer.parseInt(sound_value));
        }
        // load music level
        String music_value = SettingsManager.loadSetting("music_volume_level",
                Integer.toString(UserSettings.VOLUME_MAX_LEVEL));
        if (!music_value.isEmpty()) {
            UserSettings.setMusicVolume(Integer.parseInt(music_value));
        }
        // load time display
        String time_display_value = SettingsManager.loadSetting("show_time", "false");
        if (!time_display_value.isEmpty()) {
            displayTime = time_display_value.equals("true");
        }
        // load keyboard layout
        String keyboard_layout_value = SettingsManager.loadSetting("keyboard_layout_1", "true");
        if (!keyboard_layout_value.isEmpty()) {
            keyboardLayout_1 = keyboard_layout_value.equals("true");
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
