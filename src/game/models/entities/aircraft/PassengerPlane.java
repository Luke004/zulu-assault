package game.models.entities.other;

import game.models.CollisionModel;
import game.models.entities.Entity;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class PassengerPlane extends Entity {

    private static Texture passenger_plane_texture;

    private static final float PASSENGER_PLANE_ARMOR = 90.f;
    private static final int PASSENGER_PLANE_SCORE_VALUE = 2000;

    public PassengerPlane(Vector2f startPos, boolean isHostile) {
        super(startPos, isHostile);

        // LOAD TEXTURES
        try {
            if (passenger_plane_texture == null) {
                passenger_plane_texture = new Image("assets/entities/other/passenger_plane.png")
                        .getTexture();
            }
            base_image = new Image(passenger_plane_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }
        this.init();
    }

    @Override
    public void init() {
        collisionModel = new CollisionModel(position, base_image.getWidth() / 8, base_image.getHeight());
        super.init();
    }

    @Override
    public void update(GameContainer gc, int deltaTime) {
        super.update(gc, deltaTime);

        if (isDestroyed) {
            level_delete_listener.notifyForEntityDestruction(this);
        }
    }

    @Override
    public void draw(Graphics graphics) {
        base_image.drawCentered(position.x, position.y);
        drawHealthBar(graphics);
    }

    @Override
    public void drawPreview(Graphics graphics) {
        base_image.draw(position.x, position.y, 0.13f);
    }

    @Override
    public void fireWeapon(WeaponType weapon) {

    }

    @Override
    public void changeAimingDirection(float degree, int deltaTime) {

    }

    @Override
    public void setRotation(float degree) {
        base_image.setRotation(degree);
        collisionModel.update(getRotation());
    }

    @Override
    public void changeHealth(float amount) {
        super.changeHealth(amount, PASSENGER_PLANE_ARMOR);
    }

    @Override
    public int getScoreValue() {
        return PASSENGER_PLANE_SCORE_VALUE;
    }

    @Override
    public float getRotation() {
        return base_image.getRotation();
    }

}
