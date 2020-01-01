package screen_drawer.drawings;

import models.war_attenders.MovableWarAttender;
import models.war_attenders.WarAttender;
import org.lwjgl.Sys;
import org.newdawn.slick.TrueTypeFont;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static levels.LevelInfo.TILE_HEIGHT;
import static levels.LevelInfo.TILE_WIDTH;

public class ScoreValueDrawer extends Drawing {

    private TrueTypeFont point_text;
    private String score_value_string;

    // create extra instances of this class in case there are already scores being drawn somewhere else
    private ArrayList<ScoreValueDrawer> extraDrawers;

    public ScoreValueDrawer() {
        Font awtFont = new Font("Arial", Font.PLAIN, 12);
        point_text = new TrueTypeFont(awtFont, false);
        extraDrawers = new ArrayList<>();
    }

    @Override
    public void init(int seconds, WarAttender warAttender) {
        if (!isStopped) {
            // this is called when score is still being drawn somewhere else but we need to draw another score
            ScoreValueDrawer extraScoreValueDrawer = new ScoreValueDrawer();
            extraScoreValueDrawer.init(seconds, warAttender);
            extraDrawers.add(extraScoreValueDrawer);
            return;
        }
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
    public void update(int deltaTime) {
        super.update(deltaTime);

        for (int i = 0; i < extraDrawers.size(); ++i) {
            extraDrawers.get(i).update(deltaTime);
            if (extraDrawers.get(i).isStopped) extraDrawers.remove(extraDrawers.get(i));
        }
    }

    @Override
    public void draw() {
        if (!isStopped) {
            point_text.drawString(xPos, yPos, score_value_string, org.newdawn.slick.Color.white);
        }

        for (ScoreValueDrawer extraDrawer : extraDrawers) {
            extraDrawer.draw();
        }
    }
    
}
