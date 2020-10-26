package game.models.items;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class MegaPulseItem extends Item {

    public MegaPulseItem(Vector2f position) {
        super(position);
        try {
            base_image = new Image("assets/items/mega_pulse/mega_pulse_animation.png");
            final int IMAGE_COUNT = 4;
            super.init(IMAGE_COUNT);
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "MEGA_PULSE";
    }
}
