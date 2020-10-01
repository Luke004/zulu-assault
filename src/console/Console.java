package console;

import org.newdawn.slick.*;

import java.text.Normalizer;

public class Console {

    private static Graphics graphics;   // give it its own graphics object for performance
    private static GameContainer gameContainer;

    private static final int CONSOLE_HEIGHT = 20;
    private static int CONSOLE_WIDTH = 20;

    private static boolean isActive;

    private static final String DEFAULT_TEXT = "> ";
    private static final String WRITE_INDICATOR = "|";
    private static int write_indicator_x;
    private static boolean wi_blink;
    private static final int WI_BLINK_TIME_MILLIS = 1000;  // blink in second ticks
    private static long timePassed, currTime, lastTime;

    private static String s_input;

    public static void init(GameContainer gc) {
        gameContainer = gc;
        CONSOLE_WIDTH = gc.getWidth() / 2;
        graphics = gc.getGraphics();
        s_input = DEFAULT_TEXT;
    }

    public static void update(GameContainer gc) {
        if (!isActive()) return;
        // calc the x coordinate of the write indicator
        write_indicator_x = graphics.getFont().getWidth(s_input) + 2;
        // this is to fix wrong font with calculation if last char of the sting is a space bar (' ')
        if (s_input.charAt(s_input.length() - 1) == ' ') {
            write_indicator_x += 8;
        }
        // make the write indicator blink in second ticks
        timePassed += gc.getTime() - lastTime;
        if (timePassed > WI_BLINK_TIME_MILLIS) {
            timePassed = 0;
            wi_blink = !wi_blink;
        }
        lastTime = gc.getTime();
    }

    public static void render(GameContainer gc) {
        if (!isActive()) return;
        // draw the user input text
        graphics.setColor(Color.lightGray);
        graphics.drawRect(0, 0, Console.CONSOLE_WIDTH, Console.CONSOLE_HEIGHT);
        graphics.drawString(s_input, 5, 0);
        // draw the write indicator every second
        if (wi_blink)
            graphics.drawString(WRITE_INDICATOR, write_indicator_x, 0);
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
                System.out.println("SUBMIT");
                break;
            case Input.KEY_ESCAPE:
                if (s_input.length() == 2) toggle();
                else reset();
                break;
            default:
                // default text input
                String keyName;
                if (c == '^') {
                    // handle the tilde press
                    keyName = Input.getKeyName(key);
                    if (keyName.length() > 1)
                        return;   // make sure that it's a single key, not for instance a 'SPACE BAR'
                    keyName = Normalizer.normalize(keyName, Normalizer.Form.NFD);
                    s_input += keyName.toLowerCase();
                } else {
                    keyName = String.valueOf(c);
                    s_input += Normalizer.normalize(keyName, Normalizer.Form.NFD);
                }
        }

    }

    private static void reset() {
        s_input = DEFAULT_TEXT;
    }

    public static void toggle() {
        isActive = !isActive;

        if (!isActive()) {
            // CLOSE
            reset();
            gameContainer.getInput().clearKeyPressedRecord();
        }
    }

    public static boolean isActive() {
        return isActive;
    }

}

