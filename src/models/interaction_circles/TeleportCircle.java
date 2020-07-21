package models.interaction_circles;

import menus.UserSettings;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Vector2f;

public class TeleportCircle extends InteractionCircle {

    private static Sound teleport_sound;
    private final Vector2f position;

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
    public void update(int deltaTime) {
        super.update(deltaTime);
        outerCircle.rotate(0.2f * deltaTime);
    }

    @Override
    public void draw() {
        super.draw();
        outerCircle.drawCentered(position.x, position.y);
    }

    public Vector2f getPosition() {
        return position;
    }

    public static void playTeleportSound() {
        teleport_sound.play(1.f, UserSettings.SOUND_VOLUME);
    }

}
