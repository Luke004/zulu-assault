package game.util;

import org.newdawn.slick.TrueTypeFont;

import java.util.LinkedList;
import java.util.List;

public class StringUtil {

    public static List<String> getLineSplitMessageList(String msg, int windowWidth, TrueTypeFont text_drawer) {
        List<String> lineSplitList = new LinkedList<>();
        String[] split_strings = msg.split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String next_part : split_strings) {
            if (text_drawer.getWidth(builder.toString() + next_part) >= windowWidth) {
                lineSplitList.add(builder.toString());
                builder.setLength(0);
            }
            builder.append(next_part).append(" ");
        }
        if (builder.length() > 0) {
            lineSplitList.add(builder.toString());
        }
        return lineSplitList;
    }

}
