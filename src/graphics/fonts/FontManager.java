package graphics.fonts;

import org.newdawn.slick.TrueTypeFont;

import java.awt.*;

public class FontManager {

    // main game font
    private static final TrueTypeFont mainFont;

    // stencil
    private static final TrueTypeFont stencil_smaller, stencil_small;

    // console fonts
    private static final TrueTypeFont console_inputFont, console_outputFont;

    // item font
    private static final TrueTypeFont itemFont;

    static {
        console_inputFont = new TrueTypeFont(new java.awt.Font("DialogInput", java.awt.Font.PLAIN, 14), false);
        console_outputFont = new TrueTypeFont(new java.awt.Font("DialogInput", java.awt.Font.PLAIN, 11), false);
        mainFont = new TrueTypeFont(new java.awt.Font("Stencil", java.awt.Font.PLAIN, 60), false);
        stencil_small = new TrueTypeFont(new Font("Stencil", Font.PLAIN, 12), true);
        stencil_smaller = new TrueTypeFont(new Font("Stencil", Font.PLAIN, 11), true);
        itemFont = new TrueTypeFont(new Font("Century Gothic", Font.PLAIN, 11), true);
    }

    public static TrueTypeFont getMainFont() {
        return mainFont;
    }

    public static TrueTypeFont getConsoleInputFont() {
        return console_inputFont;
    }

    public static TrueTypeFont getConsoleOutputFont() {
        return console_outputFont;
    }

    public static TrueTypeFont getStencilSmall() {
        return stencil_small;
    }

    public static TrueTypeFont getStencilSmaller() {
        return stencil_smaller;
    }

    public static TrueTypeFont getItemFont() {
        return itemFont;
    }

}