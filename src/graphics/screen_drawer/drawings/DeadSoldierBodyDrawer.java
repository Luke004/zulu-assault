package graphics.screen_drawer.drawings;

import models.war_attenders.WarAttender;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class DeadSoldierBodyDrawer extends Drawing {
    private Image dead_body_image_friendly;
    private Image dead_body_image_hostile;
    private boolean isHostile;

    public DeadSoldierBodyDrawer() {
        super();
        try {
            dead_body_image_friendly = new Image("assets/war_attenders/soldiers/player_soldier_dead.png");
            dead_body_image_hostile = new Image("assets/war_attenders/soldiers/enemy_soldier_dead.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void init(int seconds, WarAttender warAttender) {
        super.init(seconds, warAttender);
        this.isHostile = warAttender.isHostile;
    }

    @Override
    public void draw() {
        if (isStopped) return;
        if (isHostile) dead_body_image_hostile.drawCentered(xPos, yPos);
        else dead_body_image_friendly.draw(xPos, yPos);
    }
}
