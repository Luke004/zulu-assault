package models.war_attenders.planes;

import logic.WayPointManager;
import graphics.animations.other.AnimatedCrosshair;
import models.war_attenders.MovableWarAttender;
import models.war_attenders.WarAttender;
import models.weapons.AGM;
import models.weapons.Uzi;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

import java.util.List;

public class GreenEnemyPlane extends Plane {

    private static Texture green_enemy_plane_texture;

    private AnimatedCrosshair animatedCrosshair;

    private static final float ARMOR = 40.f;
    private static final int SCORE_VALUE = 500;
    private static final float ROTATE_SPEED_PLAYER = 0.15f, ROTATE_SPEED_BOT = 0.15f;
    private static final float MAX_SPEED_PLAYER = 0.25f, MAX_SPEED_BOT = 0.2f;

    public GreenEnemyPlane(Vector2f startPos, boolean isHostile, boolean isDrivable) {
        super(startPos, isHostile, isDrivable);

        if (isDrivable) animatedCrosshair = new AnimatedCrosshair();

        current_speed = getMaxSpeed();  // speed is always the same for this plane

        weapons.add(new Uzi(isDrivable));  // WEAPON_1
        weapons.add(new AGM(isDrivable));  // WEAPON_2

        // LOAD TEXTURES
        try {
            if (green_enemy_plane_texture == null) {
                green_enemy_plane_texture = new Image("assets/war_attenders/planes/green_enemy_plane.png")
                        .getTexture();
            }
            base_image = new Image(green_enemy_plane_texture);
        } catch (SlickException e) {
            e.printStackTrace();
        }

        super.init();
    }

    @Override
    public void update(GameContainer gc, int deltaTime) {
        super.update(gc, deltaTime);
        if (isDrivable)
            animatedCrosshair.update(deltaTime, position, getRotation());
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        // draw the plane's crosshair
        if (isDrivable && !hasLanded) animatedCrosshair.draw();
    }

    /* don't use following 2 methods, this plane always flies at same speed */
    @Override
    public void increaseSpeed(int deltaTime) {
    }

    @Override
    public void decreaseSpeed(int deltaTime) {
    }

    @Override
    public void shootAtEnemies(MovableWarAttender player, List<? extends WarAttender> enemies_of_warAttender, int deltaTime) {
        if (isDestroyed) return;
        float xPos, yPos, dist;
        if (player != null) {
            // player not null means it's a hostile tank
            // calculate dist between the player and the enemy
            xPos = player.getPosition().x;
            yPos = player.getPosition().y;
            dist = (float) Math.sqrt((xPos - position.x) * (xPos - position.x)
                    + (yPos - position.y) * (yPos - position.y));
        } else {
            // player null means it's a friendly tank
            xPos = 0;
            yPos = 0;
            dist = Float.MAX_VALUE;
        }
        // calculate dist between each tank and all its enemies
        for (WarAttender enemy_war_attender : enemies_of_warAttender) {
            float next_xPos = enemy_war_attender.getPosition().x;
            float next_yPos = enemy_war_attender.getPosition().y;
            float next_dist = WayPointManager.dist(position, new Vector2f(next_xPos, next_yPos));

            if (next_dist < dist) {
                dist = next_dist;
                xPos = next_xPos;
                yPos = next_yPos;
            }
        }

        // follow the closest enemy
        float rotationDegree;
        if (dist < 380) {
            isEnemyNear = true;
            rotationDegree = WayPointManager.calculateAngleToRotateTo(position, new Vector2f(xPos, yPos));
            changeAimingDirection(rotationDegree, deltaTime);
            fireWeapon(MovableWarAttender.WeaponType.WEAPON_1);
            fireWeapon(WeaponType.WEAPON_2);   // green plane can also shoot wpn2
        } else {
            isEnemyNear = false;
        }
    }

    @Override
    protected float getBaseRotateSpeed() {
        return isDrivable ? ROTATE_SPEED_PLAYER : ROTATE_SPEED_BOT;
    }

    @Override
    protected float getMaxSpeed() {
        return isDrivable ? MAX_SPEED_PLAYER : MAX_SPEED_BOT;
    }

    @Override
    public void changeHealth(float amount) {
        super.changeHealth(amount, ARMOR);
    }

    @Override
    public int getScoreValue() {
        return SCORE_VALUE;
    }
}
