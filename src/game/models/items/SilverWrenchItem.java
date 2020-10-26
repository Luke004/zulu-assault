package game.models.items;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class SilverWrenchItem extends Item {
    public SilverWrenchItem(Vector2f position) {
        super(position);
        try {
            base_image = new Image("assets/items/wrenches/silver_wrench/silver_wrench_animation.png");
            final int IMAGE_COUNT = 10;
            super.init(IMAGE_COUNT);
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "SILVER_WRENCH";
    }
}
