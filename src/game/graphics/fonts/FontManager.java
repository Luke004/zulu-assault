package game.graphics.fonts;

import org.newdawn.slick.TrueTypeFont;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FontManager {

    // game title font
    private static final TrueTypeFont game_title;

    // stencil
    private static final TrueTypeFont stencil_smaller, stencil_small, stencil_medium, stencil_big;

    // console fonts
    private static final TrueTypeFont console_input, console_output, console_output_bold;

    // item font
    private static final TrueTypeFont item;

    static {
        console_input = new TrueTypeFont(new java.awt.Font("DialogInput", java.awt.Font.PLAIN, 14), true);
        console_output = new TrueTypeFont(new java.awt.Font("DialogInput", java.awt.Font.PLAIN, 11), true);
        console_output_bold = new TrueTypeFont(new java.awt.Font("DialogInput", Font.BOLD, 11), true);
        game_title = new TrueTypeFont(createStencilFont(69), true);
        stencil_small = new TrueTypeFont(createStencilFont(12), true);
        stencil_smaller = new TrueTypeFont(createStencilFont(11), true);
        stencil_medium = new TrueTypeFont(createStencilFont(19), true);
        stencil_big = new TrueTypeFont(createStencilFont(45), true);
        item = new TrueTypeFont(new Font("Century Gothic", Font.PLAIN, 11), true);
    }

    public static TrueTypeFont getGameTitleFont() {
        return game_title;
    }

    public static TrueTypeFont getConsoleInputFont() {
        return console_input;
    }

    public static TrueTypeFont getConsoleOutputFont(boolean bold) {
        return bold ? console_output_bold : console_output;
    }

    public static TrueTypeFont getStencilSmallFont() {
        return stencil_small;
    }

    public static TrueTypeFont getStencilSmallerFont() {
        return stencil_smaller;
    }

    public static TrueTypeFont getStencilMediumFont() {
        return stencil_medium;
    }

    public static TrueTypeFont getStencilBigFont() {
        return stencil_big;
    }

    public static TrueTypeFont getItemFont() {
        return item;
    }

    private static Font createStencilFont(int size) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, new File("assets/fonts/Stencil.ttf"))
                    .deriveFont((float) size);
        } catch (FontFormatException | IOException e) {
            System.out.println("Failed to load Stencil font. Using fallback font \"Consolas\" instead.\n");
            size -= 7;
            return new Font("Consolas", Font.PLAIN, size);
        }
    }
}