package game.menu.console;

import game.levels.LevelManager;
import settings.SettingStorage;
import settings.UserSettings;

public class Scanner {

    private static final String KEYWORD_LEVEL = "level";
    private static final String ERROR_UNRECOGNIZED = "Unrecognized command.";
    private static final String HELP_OPEN_LEVEL = "Use: 'open level x'  " +
            "|  x = number between 1-20 or the name of a custom level";

    public static String scan(String input) {
        if (input.isEmpty()) return "";

        String[] input_split_by_whitespace = input.split("\\s+");
        String command = input_split_by_whitespace[0];

        switch (command) {
            case "open":    // open a specific level
                if (input_split_by_whitespace.length != 3) return HELP_OPEN_LEVEL;
                // e.g. 'open level 1'
                String s_level = input_split_by_whitespace[2];
                if (!LevelManager.existsLevel(s_level)) {
                    return "Level " + s_level + " doesn't exist yet. Maybe create it using the editor?";
                }
                LevelManager.openSingleLevel(s_level);
                closeConsoleWithDelay();
                return "Opening level " + s_level + " ...";
            case "show":
                String element = input_split_by_whitespace[1];
                switch (element) {
                    case "time":    // display the time needed for the play through
                        UserSettings.toggleTimeDisplay();
                        if (UserSettings.displayTime) {
                            SettingStorage.storeSetting(new SettingStorage.Property("show_time", "true"));
                            return "Time display enabled.";
                        } else {
                            SettingStorage.storeSetting(new SettingStorage.Property("show_time", "false"));
                            return "Time display disabled.";
                        }
                    case "fps":    // display frames per second
                        UserSettings.toggleFPSDisplay();
                        if (UserSettings.displayFPS) {
                            SettingStorage.storeSetting(new SettingStorage.Property("show_fps", "true"));
                            return "FPS display enabled.";
                        } else {
                            SettingStorage.storeSetting(new SettingStorage.Property("show_fps", "false"));
                            return "FPS display disabled.";
                        }
                    default:
                        return ERROR_UNRECOGNIZED;
                }
            case "keyboard_layout":
                String country = input_split_by_whitespace[1];
                switch (country) {
                    case "1":   // German
                        UserSettings.keyboardLayout_1 = true;
                        SettingStorage.storeSetting(new SettingStorage.Property("keyboard_layout_1", "true"));
                        return "Using keyboard layout 1 (German).";
                    case "2":   // English
                        UserSettings.keyboardLayout_1 = false;
                        SettingStorage.storeSetting(new SettingStorage.Property("keyboard_layout_1", "false"));
                        return "Using keyboard layout 2 (English).";
                    default:
                        return ERROR_UNRECOGNIZED;
                }

            case "exit":    // all commands to exit the game.menu.console
            case "leave":
            case "end":
            case "stop":
            case "close":
                Console.toggle();
                return "";

        }
        return ERROR_UNRECOGNIZED;

    }

    private static void closeConsoleWithDelay() {
        // just because it looks cool when you see the 'opening level x ...' message before it runs Xd
        Thread t1 = new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Console.toggle();
        });
        t1.start();
    }

}
