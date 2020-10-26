package game.menu.console;

import game.graphics.fonts.FontManager;
import org.newdawn.slick.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

import java.text.Normalizer;
import java.util.ArrayList;

public class Console {

    private static Graphics graphics;   // give it its own game.graphics object for performance
    private static GameContainer gameContainer;

    private static final int CONSOLE_HEIGHT = 20;
    private static int CONSOLE_WIDTH;

    private static boolean isActive;

    private static Font inputFont, outputFont;

    // game.menu.console writing
    private static final String DEFAULT_TEXT = "> ";
    private static final String WRITE_INDICATOR = "|";
    private static int write_indicator_x;
    private static boolean wi_blink;
    private static final int WI_BLINK_TIME_MILLIS = 1000;  // blink in second ticks
    private static long wi_timePassed, wi_lastTime;

    // game.menu.console output
    private static boolean printResponse;
    private static String s_response;
    private static final int RT_TIME_MILLIS = 5000;  // show response for 5 seconds
    private static long rt_timePassed, rt_lastTime;

    // game.menu.console memory
    private static ArrayList<String> memory;
    private static final int MEMORY_MAX_SIZE = 6;
    private static int currMemoryIdx = 0;

    private static String s_input;

    public static void init(GameContainer gc) {
        gameContainer = gc;
        CONSOLE_WIDTH = gc.getWidth() / 2;
        graphics = gc.getGraphics();
        inputFont = FontManager.getConsoleInputFont();
        outputFont = FontManager.getConsoleOutputFont(false);
        s_input = DEFAULT_TEXT;
        memory = new ArrayList<>();
        memory.add(DEFAULT_TEXT);
    }

    public static void update(GameContainer gc) {
        if (!isActive()) return;
        // calc the x coordinate of the write indicator
        write_indicator_x = inputFont.getWidth(s_input) + 2;
        // this is to fix wrong font with calculation if last char of the sting is a space bar (' ')
        if (s_input.charAt(s_input.length() - 1) == ' ') {
            write_indicator_x += 8;
        }
        // make the write indicator blink in second ticks
        wi_timePassed += gc.getTime() - wi_lastTime;
        if (wi_timePassed > WI_BLINK_TIME_MILLIS) {
            wi_timePassed = 0;
            wi_blink = !wi_blink;
        }
        wi_lastTime = gc.getTime();

        // make the response disappear after 5 sec
        if (printResponse) {
            if (rt_lastTime == 0) rt_lastTime = gc.getTime();
            rt_timePassed += gc.getTime() - rt_lastTime;
            rt_lastTime = gc.getTime();
            if (rt_timePassed > RT_TIME_MILLIS) {
                rt_timePassed = 0;
                rt_lastTime = 0;
                printResponse = false;
            }
        }
    }

    public static void render(GameContainer gc) {
        if (!isActive()) return;
        // draw the user input text
        graphics.setColor(Color.lightGray);
        graphics.drawRect(0, 0, Console.CONSOLE_WIDTH, Console.CONSOLE_HEIGHT);
        inputFont.drawString(5, 0, s_input, Color.lightGray);
        // draw the write indicator every second
        if (wi_blink) {
            inputFont.drawString(write_indicator_x, 0, WRITE_INDICATOR, Color.lightGray);
        }
        // draw a response for 10 seconds
        if (printResponse) {
            outputFont.drawString(5, Console.CONSOLE_HEIGHT, s_response, Color.lightGray);
        }
    }

    public static void handleUserInput(int key, char c) {
        switch (key) {
            case Input.KEY_BACK:
                // delete the last char
                if (s_input.length() == 2) return;
                s_input = s_input.substring(0, s_input.length() - 1);
                break;
            case Input.KEY_ENTER:
                // submit the input
                memorizeCommand(s_input);
                String result = Scanner.scan(s_input.substring(DEFAULT_TEXT.length()));
                if (!result.isEmpty()) {
                    print(result);
                }
                clear();
                break;
            case Input.KEY_ESCAPE:
                if (s_input.length() == 2) toggle();
                else clear();
                break;
            case Input.KEY_UP:
                // access latest memory
                s_input = memory.get(currMemoryIdx);
                if ((currMemoryIdx - 1) < 0) {
                    currMemoryIdx = memory.size() - 1;
                } else currMemoryIdx--;
                break;
            case Input.KEY_DOWN:
                // access oldest memory
                s_input = memory.get(currMemoryIdx);
                if ((currMemoryIdx + 1) > memory.size() - 1) {
                    currMemoryIdx = 0;
                } else currMemoryIdx++;
                break;
            default:
                // default text input
                String charName;
                if (c == '^') {
                    // handle the tilde press
                    charName = Input.getKeyName(key);
                    if (charName.length() > 1)
                        return;   // make sure that it's a single key, not for instance a 'SPACE BAR'
                } else {
                    charName = (String.valueOf(c));
                }
                charName = normalize(charName);
                if (!isValidInputChar(charName)) return;
                s_input += charName;
        }
    }

    private static String normalize(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s.toLowerCase();
    }

    private static boolean isValidInputChar(String m_char) {
        return (m_char.matches("[-_a-zA-Z0-9 ]"));
    }

    private static void print(String response) {
        s_response = response;
        printResponse = true;
        rt_timePassed = 0;
        rt_lastTime = 0;
    }

    private static void memorizeCommand(String cmd) {
        // remove the oldest memorized command if the memory has reached its limit
        if (memory.size() >= MEMORY_MAX_SIZE) {
            memory.remove(1);
        }
        memory.add(cmd);
        currMemoryIdx = memory.size() - 1;
    }

    private static void clear() {
        s_input = DEFAULT_TEXT;
    }

    private static void close() {
        clear();
        printResponse = false;
        s_response = "";
        gameContainer.getInput().clearKeyPressedRecord();
        rt_timePassed = 0;
        rt_lastTime = 0;
    }

    public static void toggle() {
        isActive = !isActive;
        if (!isActive()) {
            close();
        }
    }

    public static boolean isActive() {
        return isActive;
    }

}

