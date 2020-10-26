package settings;

import java.io.*;
import java.util.Properties;

public class SettingStorage {

    private static final String SETTINGS_FILE_NAME = "config.properties";
    private static final File directory = new File("saves/settings/");

    /*  CREATE ALL DEFAULT SETTINGS */
    public static void createSettingsFile() {
        File properties_file = new File(directory + File.separator + SETTINGS_FILE_NAME);
        try {
            boolean created = properties_file.createNewFile();
            if (created) {  // file didnt exist and was just created
                // SOUND VOLUME
                SettingStorage.storeSetting(new SettingStorage.Property("sound_volume_level",
                        Integer.toString(UserSettings.VOLUME_MAX_LEVEL)));
                // MUSIC VOLUME
                SettingStorage.storeSetting(new SettingStorage.Property("music_volume_level",
                        Integer.toString(UserSettings.VOLUME_MAX_LEVEL)));
                // SHOW TIME
                SettingStorage.storeSetting(new SettingStorage.Property("show_time", "false"));
            }
        } catch (IOException e) {
            System.out.println("Could not create initial settings file '" + SETTINGS_FILE_NAME + "'");
        }
    }

    public static void storeSetting(Property property) {
        storeSettings(new Property[]{new Property(property.key, property.value)});
    }

    public static void storeSettings(Property[] properties) {
        try {
            //noinspection ResultOfMethodCallIgnored
            directory.mkdirs();
            File properties_file = new File(directory + File.separator + SETTINGS_FILE_NAME);
            //noinspection ResultOfMethodCallIgnored
            properties_file.createNewFile(); // this creates a file only if it not already exists

            Properties props = loadProperties();

            if (props == null) {
                System.out.println("Could not load properties from 'config.properties'");
                return;
            }

            // store all properties in 'config.properties' file
            for (Property property : properties) {
                props.setProperty(property.key, property.value);
            }
            FileOutputStream out = new FileOutputStream(directory + File.separator + SETTINGS_FILE_NAME);
            props.store(out, "User settings:");
            out.close();
        } catch (IOException e) {
            System.out.println("Could not create or store data in file 'src/main/saves/" + SETTINGS_FILE_NAME
                    + "'");
            //e.printStackTrace();
        }
    }

    public static String loadSetting(String key, String defaultValue) {
        // try to load the user setting
        File user_settings_file = new File(directory + File.separator + SETTINGS_FILE_NAME);
        if (user_settings_file.exists()) {
            Properties props = loadProperties();
            if (props == null) {
                System.out.println("Error: Could not load file 'config.properties'!");
                return "";
            }
            String value = props.getProperty(key);
            if (value != null) {
                return value;
            }
        }
        // key doesn't exist -> create one with the default value
        SettingStorage.storeSetting(new SettingStorage.Property(key, defaultValue));
        return defaultValue;
    }

    private static Properties loadProperties() {
        try {
            Properties props = new Properties();
            FileInputStream in = new FileInputStream(directory + File.separator + SETTINGS_FILE_NAME);
            props.load(in);
            in.close();
            return props;
        } catch (IOException ignored) {
            return null;
        }
    }

    public static class Property {

        public Property(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String key, value;

    }

}
