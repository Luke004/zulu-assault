package screen_drawer.drawings;

import models.war_attenders.MovableWarAttender;
import models.war_attenders.WarAttender;
import org.newdawn.slick.TrueTypeFont;

import java.awt.*;

import static levels.LevelInfo.TILE_HEIGHT;
import static levels.LevelInfo.TILE_WIDTH;

public class ScoreValueDrawer extends Drawing {

    private TrueTypeFont point_text;
    private String score_value_string;

    public ScoreValueDrawer() {
        Font awtFont = new Font("Arial", Font.PLAIN, 12);
        point_text = new TrueTypeFont(awtFont, false);
    }

    @Override
    public void init(int seconds, WarAttender warAttender) {
        super.init(seconds, warAttender);
        // get the score value to print as a string
        this.score_value_string = Integer.toString(warAttender.getScoreValue());
        int textWidth = point_text.getWidth(score_value_string);
        this.xPos -= textWidth / 2.f;
        int textHeight = point_text.getHeight(score_value_string);
        this.yPos -= textHeight / 2.f;

        // if it's a MovableWarAttender, the x and y pos are already centered, so do nothing
        if (!(warAttender instanceof MovableWarAttender)) { // center using default values (TILE specs)
            this.xPos += TILE_WIDTH / 2.f;
            this.yPos += TILE_HEIGHT / 2.f;
        }
    }

    @Override
    public void draw() {
        if (isStopped) return;
        point_text.drawString(xPos, yPos, score_value_string, org.newdawn.slick.Color.white);
    }
}
