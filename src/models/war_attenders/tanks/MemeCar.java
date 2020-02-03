package models.war_attenders.tanks;

import models.CollisionModel;
import models.weapons.Uzi;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class MemeCar extends Tank {

    private static Texture meme_car_texture, meme_car_turret_texture;

    private static final float ARMOR = 20.f;
    private static final int SCORE_VALUE = 50;

    public MemeCar(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        // attributes equal for humans and bots
        max_speed = 0.1f;
        backwards_speed = 0.07f;
        acceleration_factor = 0.0005f;
        deceleration_factor = 0.0005f;
        rotate_speed = 0.15f;

        if (isDrivable) weapons.add(new Uzi(isDrivable));  // WEAPON_1

        // LOAD TEXTURES
        try {
            if (meme_car_texture == null) {
                meme_car_texture = new Image("assets/war_attenders/tanks/meme_car.png")
                        .getTexture();
            }
            base_image = new Image(meme_car_texture);
            if (meme_car_turret_texture == null) {
                meme_car_turret_texture =
                        new Image("assets/war_attenders/tanks/meme_car_turret.png")
                                .getTexture();
            }
            turret = new Image(meme_car_turret_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }

        collisionModel = new CollisionModel(position, base_image.getWidth(), base_image.getHeight());
        super.init();
    }

    @Override
    public void changeHealth(float amount) {
        super.changeHealth(amount, ARMOR);
    }
}
