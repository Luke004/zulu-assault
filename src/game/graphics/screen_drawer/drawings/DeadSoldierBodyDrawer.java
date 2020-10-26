package game.graphics.screen_drawer.drawings;

import game.models.entities.Entity;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class DeadSoldierBodyDrawer extends Drawing {
    private Image dead_body_image_friendly;
    private Image dead_body_image_hostile;
    private boolean isHostile;

    public DeadSoldierBodyDrawer() {
        super();
        try {
            dead_body_image_friendly = new Image("assets/entities/soldiers/player_soldier_dead.png");
            dead_body_image_hostile = new Image("assets/entities/soldiers/enemy_soldier_dead.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void init(int seconds, Entity entity) {
        super.init(seconds, entity);
        this.isHostile = entity.isHostile;
    }

    @Override
    public void draw() {
        if (isStopped) return;
        if (isHostile) dead_body_image_hostile.drawCentered(xPos, yPos);
        else dead_body_image_friendly.draw(xPos, yPos);
    }
}
