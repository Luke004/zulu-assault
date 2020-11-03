package game.models.entities.aircraft;

import game.graphics.animations.other.AnimatedCrosshair;
import game.models.weapons.AGM;
import game.models.weapons.Uzi;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class WarPlane extends Plane {

    private static Texture war_plane_texture;

    private AnimatedCrosshair animatedCrosshair;

    // STATS
    private static final float ARMOR = 40.f;
    private static final float ROTATE_SPEED_PLAYER = 0.1f, ROTATE_SPEED_BOT = 0.1f;
    private static final float MIN_SPEED_PLAYER = 0.1f, MIN_SPEED_BOT = 0.17f;
    private static final float MAX_SPEED_PLAYER = 0.22f, MAX_SPEED_BOT = 0.22f;
    private static final float SPEED_CHANGE_FACTOR = 0.000035f;

    public WarPlane(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        if (isDrivable) animatedCrosshair = new AnimatedCrosshair();

        weapons.add(new Uzi(isDrivable));  // WEAPON_1
        weapons.add(new AGM(isDrivable));  // WEAPON_2

        // LOAD TEXTURES
        try {
            if (war_plane_texture == null) {
                war_plane_texture = new Image("assets/entities/aircraft/war_plane.png")
                        .getTexture();
            }
            base_image = new Image(war_plane_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }

        super.init();
    }

    @Override
    public void update(GameContainer gc, int deltaTime) {
        super.update(gc, deltaTime);
        if (isDrivable) animatedCrosshair.update(deltaTime, position, getRotation());
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        if (isDrivable && !hasLanded) animatedCrosshair.draw();     // draw the war plane's crosshair
    }

    @Override
    protected float getBaseRotateSpeed() {
        return isDrivable ? ROTATE_SPEED_PLAYER : ROTATE_SPEED_BOT;
    }

    @Override
    protected float getMinSpeed() {
        return (isDrivable ? MIN_SPEED_PLAYER : MIN_SPEED_BOT);
    }

    @Override
    protected float getMaxSpeed() {
        return (isDrivable ? MAX_SPEED_PLAYER : MAX_SPEED_BOT);
    }

    @Override
    protected float getSpeedChangeFactor() {
        return SPEED_CHANGE_FACTOR;
    }

    @Override
    public void changeHealth(float amount) {
        super.changeHealth(amount, ARMOR);
    }

}
