package console;

import main.ZuluAssault;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scanner {

    private static final String KEYWORD_LEVEL = "level";
    private static final String ERROR_UNRECOGNIZED = "Unrecognized command.";

    public static String scan(String input) {
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
                            // TODO: open level by id
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
                        // TODO: open level by id
                        return "Opening level " + levelID + " ...";
                    }
                    return "Level " + levelID + " doesn't exist yet.";
                }
                return "Level doesn't exist.";
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

}
