package models.items;

import models.StaticCollisionEntity;
import org.newdawn.slick.Animation;
import org.newdawn.slick.geom.Vector2f;

public abstract class Item extends StaticCollisionEntity {

    protected Animation animation;
    private final static int IMAGE_WIDTH_AND_HEIGHT = 40;

    public Item(Vector2f position) {
        super(position);
    }

    protected void init(final int IMAGE_COUNT) {
        animation = new Animation(false);
        int x = 0;
        for (int idx = 0; idx < IMAGE_COUNT; ++idx) {
            animation.addFrame(base_image.getSubImage(x, 0, IMAGE_WIDTH_AND_HEIGHT, IMAGE_WIDTH_AND_HEIGHT),
                    500);
            x += IMAGE_WIDTH_AND_HEIGHT;
        }
        animation.setLooping(true);
        initCollisionModel(IMAGE_WIDTH_AND_HEIGHT / 2, IMAGE_WIDTH_AND_HEIGHT / 2);
    }

    @Override
    public void draw() {
        this.animation.draw(position.x, position.y);
    }

    @Override
    public void update(int deltaTime) {
        this.animation.update(deltaTime);
    }

    abstract public String getName();
}
