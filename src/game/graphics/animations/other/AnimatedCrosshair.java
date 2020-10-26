package game.graphics.animations.other;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class AnimatedCrosshair {
    private Animation crosshair_animation;
    private Vector2f crosshair_pos;
    final static int IMAGE_HALF = 16;

    public AnimatedCrosshair() {
        crosshair_pos = new Vector2f();
        final int IMAGE_COUNT = 4;
        int y = 0;
        Image crosshair_animation_image;
        try {
            crosshair_animation_image = new Image("assets/animations/animated_crosshair.png");
            crosshair_animation = new Animation(false);
            for (int idx = 0; idx < IMAGE_COUNT; ++idx) {
                crosshair_animation.addFrame(crosshair_animation_image
                        .getSubImage(0, y, IMAGE_HALF * 2, IMAGE_HALF * 2), 200);
                y += IMAGE_HALF * 2;
            }
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public void update(int deltaTime, Vector2f player_pos, float player_rotation) {
        float m_dir_x = (float) Math.sin(player_rotation * Math.PI / 180);
        float m_dir_y = (float) -Math.cos(player_rotation * Math.PI / 180);
        crosshair_pos.x = player_pos.x + m_dir_x * 170.f;
        crosshair_pos.y = player_pos.y + m_dir_y * 170.f;
        crosshair_animation.update(deltaTime);
    }

    public void draw() {
        crosshair_animation.draw(crosshair_pos.x - IMAGE_HALF, crosshair_pos.y - IMAGE_HALF);
    }
}
