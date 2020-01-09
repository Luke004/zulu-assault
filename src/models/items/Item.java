package models.items;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public abstract class AbstractItem {

    private Vector2f position;
    private Animation animation;
    protected Image animation_image;
    private final static int IMAGE_WIDTH_AND_HEIGHT = 40;

    public AbstractItem(Vector2f position) {
        this.position = position;
    }

    protected void init(final int IMAGE_COUNT) {
        animation = new Animation(false);
        int x = 0;
        for (int idx = 0; idx < IMAGE_COUNT; ++idx) {
            animation.addFrame(animation_image.getSubImage(x, 0, IMAGE_WIDTH_AND_HEIGHT, IMAGE_WIDTH_AND_HEIGHT),
                    100);
            x += IMAGE_WIDTH_AND_HEIGHT;
        }
        animation.setLooping(true);
    }

    public void draw() {
        this.animation.draw(position.x, position.y);
    }

    public void update(int deltaTime) {
        this.animation.update(deltaTime);
    }
}
