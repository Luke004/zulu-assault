package models;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

public class Player {
    private Vector2f pos, dir;
    private float speed;
    private Image image;
    private Animation animation;

    public Player(float posX, float posY) {
        this(new Vector2f(posX, posY));
    }

    public Player(Vector2f pos) {
        this.pos = pos;
        this.dir = new Vector2f(0, 0);
        speed = 0.01f;
        try {
            image = new Image("assets/soldiers/player_soldier_animation.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }

        animation = new Animation(false);
        int x = 0;
        do {
            animation.addFrame(image.getSubImage(x, 0, 12, 12), 300);
            x += 12;
        } while (x <= 24);
        animation.setCurrentFrame(1);
        animation.setLooping(true);
        animation.setPingPong(true);
        animation.stop();
    }

    public void render() {
        animation.draw(pos.x, pos.y);
    }

    public void update(GameContainer gameContainer, int deltaTime) {
        pos.add(dir);
        Input input = gameContainer.getInput();
        if (input.isKeyDown(Input.KEY_W)) {
            animation.start();
            pos.add(speed * deltaTime);
        } else {
            animation.stop();
        }
        if (input.isKeyDown(Input.KEY_A)) {
            for (int idx = 0; idx < animation.getFrameCount(); ++idx) {
                animation.getImage(idx).rotate(-1);
            }
            dir.add(animation.getCurrentFrame().getRotation());
        }
        if (input.isKeyDown(Input.KEY_D)) {
            for (int idx = 0; idx < animation.getFrameCount(); ++idx) {
                animation.getImage(idx).rotate(1);
            }
        }
        animation.update(deltaTime);
    }
}
