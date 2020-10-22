package models.entities.aircraft;

import graphics.animations.other.AnimatedCrosshair;
import models.weapons.AGM;
import models.weapons.Uzi;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class WarPlane extends Plane {

    private static Texture war_plane_texture;

    private AnimatedCrosshair animatedCrosshair;

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

}
