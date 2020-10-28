package game.models.items;

import game.models.CollisionModel;
import game.models.Element;
import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

public abstract class Item extends Element {

    public Animation animation;
    private final static int IMAGE_WIDTH_AND_HEIGHT = 40;
    public boolean isMandatory;

    public Item(Vector2f position) {
        this.position = position;
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

        // init the collision model and center its position
        int width = IMAGE_WIDTH_AND_HEIGHT / 2;
        int height = IMAGE_WIDTH_AND_HEIGHT / 2;
        Vector2f centeredPosition = new Vector2f(position.x + width / 2.f, position.y + height / 2.f);
        collisionModel = new CollisionModel(centeredPosition, width, height);
    }

    @Override
    public void draw(Graphics graphics) {
        this.animation.draw(position.x, position.y);
    }

    @Override
    public void drawPreview(Graphics graphics) {
        this.animation.getImage(0).draw(position.x, position.y, 0.6f);
    }

    @Override
    public void update(GameContainer gc, int deltaTime) {
        this.animation.update(deltaTime);
    }

    @Override
    public Image getBaseImage() {
        return this.animation.getImage(0);
    }

    abstract public String getName();
}
