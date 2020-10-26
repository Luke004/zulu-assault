package game.models.interaction_circles;

import org.newdawn.slick.*;
import settings.UserSettings;
import org.newdawn.slick.geom.Vector2f;

public class TeleportCircle extends InteractionCircle {

    private static Sound teleport_sound;

    static {
        try {
            teleport_sound = new Sound("audio/sounds/teleport.ogg");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public TeleportCircle(Vector2f map_position) {
        super(map_position);
        this.position = map_position;
        try {
            outerCircle = new Image("assets/interaction_circles/teleport_circle.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(GameContainer gc, int deltaTime) {
        super.update(gc, deltaTime);
        outerCircle.rotate(0.2f * deltaTime);
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        outerCircle.drawCentered(position.x, position.y);
    }

    @Override
    public void drawPreview(Graphics graphics) {
        super.drawPreview(graphics);
        outerCircle.draw(position.x, position.y, 0.2f);
    }

    public static void playTeleportSound() {
        teleport_sound.play(1.f, UserSettings.soundVolume);
    }

}
