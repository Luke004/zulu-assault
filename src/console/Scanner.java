package console;

import levels.LevelHandler;
import logic.SettingsManager;
import logic.TimeManager;
import main.ZuluAssault;
import settings.UserSettings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scanner {

    private static final String KEYWORD_LEVEL = "level";
    private static final String ERROR_UNRECOGNIZED = "Unrecognized command.";

    public static String scan(String input) {
        if (input.isEmpty()) return "";

        String[] input_split_by_whitespace = input.split("\\s+");
        String command = input_split_by_whitespace[0];

        switch (command) {
            case "open":    // open a specific level
                if (input_split_by_whitespace.length > 3 || input_split_by_whitespace.length < 2)
                    return ERROR_UNRECOGNIZED;
                if (input_split_by_whitespace.length == 2) {    // e.g. 'open level_1'
                    String level = input_split_by_whitespace[1];
                    Pattern pattern = Pattern.compile(KEYWORD_LEVEL + "[-_]?([0-9]+)");
                    Matcher matcher = pattern.matcher(level);

                    if (matcher.matches()) {
                        int levelID = Integer.parseInt(matcher.group(1));
                        if (ZuluAssault.existsLevel(levelID)) {
                            LevelHandler.openSingleLevel(levelID);
                            closeConsoleWithDelay();
                            return "Opening level " + levelID + " ...";
                        }
                        return "Level " + levelID + " doesn't exist yet.";
                    }
                } else {    // e.g. 'open level 1'
                    String s_levelID = input_split_by_whitespace[2];
                    int levelID;
                    try {
                        levelID = Integer.parseInt(s_levelID);
                    } catch (Exception ignored) {
                        return "Level " + s_levelID + " doesn't exist.";
                    }
                    if (ZuluAssault.existsLevel(levelID)) {
                        LevelHandler.openSingleLevel(levelID);
                        closeConsoleWithDelay();
                        return "Opening level " + levelID + " ...";
                    }
                    return "Level " + levelID + " doesn't exist yet.";
                }
                return "Level doesn't exist.";
            case "show":
                String element = input_split_by_whitespace[1];
                switch (element) {
                    case "time":    // display the time needed for the play through
                        TimeManager.toggleTimeDisplay();
                        if (UserSettings.displayTime) {
                            SettingsManager.storeSetting(new SettingsManager.Property("show_time", "true"));
                            return "Time display enabled.";
                        } else {
                            SettingsManager.storeSetting(new SettingsManager.Property("show_time", "false"));
                            return "Time display disabled.";
                        }
                    default:
                        return ERROR_UNRECOGNIZED;
                }
            case "keyboard_layout":
                String country = input_split_by_whitespace[1];
                switch (country) {
                    case "1":   // German
                        UserSettings.keyboardLayout_1 = true;
                        SettingsManager.storeSetting(new SettingsManager.Property("keyboard_layout_1", "true"));
                        return "Using keyboard layout 1 (German).";
                    case "2":   // English
                        UserSettings.keyboardLayout_1 = false;
                        SettingsManager.storeSetting(new SettingsManager.Property("keyboard_layout_1", "false"));
                        return "Using keyboard layout 2 (English).";
                    default:
                        return ERROR_UNRECOGNIZED;
                }

            case "exit":    // all commands to exit the console
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
