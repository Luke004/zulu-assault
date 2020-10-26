package game.models.weapons.projectiles;

import game.models.CollisionModel;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Flame extends Projectile {
    List<Animation> buffered_explosions;
    List<Animation> active_explosions;
    Animation starting_flame;
    List<FlameInstance> explosion_animations;
    private final float ANIMATION_WIDTH_HALF, ANIMATION_HEIGHT_HALF;
    private final int CREATE_NEW_FLAME_TIMER = projectile_max_lifetime / 5;
    private int time = 0;
    private Random random;

    public Flame(Vector2f startPos, Vector2f dir, float rotation, Texture projectile_texture) {
        super(startPos, dir, rotation, projectile_texture);
        explosion_animations = new ArrayList<>();
        buffered_explosions = new ArrayList<>();
        active_explosions = new ArrayList<>();
        random = new Random();
        final int FLAMES_TO_CREATE = projectile_max_lifetime / CREATE_NEW_FLAME_TIMER;
        int counter = 0;
        do {
            buffered_explosions.add(createNewExplosionInstance());
            counter++;
        } while (counter < FLAMES_TO_CREATE);

        // individual napalm specs
        projectile_speed = 0.17f;
        projectile_max_lifetime = 1000;

        this.projectile_collision_model = new CollisionModel(projectile_pos, WIDTH_HALF * 2, HEIGHT_HALF * 2);

        starting_flame = createNewExplosionInstance();
        ANIMATION_HEIGHT_HALF = starting_flame.getCurrentFrame().getHeight() / 2.f;
        ANIMATION_WIDTH_HALF = starting_flame.getCurrentFrame().getWidth() / 2.f;
    }

    private Animation createNewExplosionInstance() {
        Image explosion_animation_image = null;
        try {
            explosion_animation_image = new Image("assets/animations/big_explosion.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        int IMAGE_COUNT = 4;
        int x = 0;
        Animation explosion_animation = new Animation(false);
        int random_flame_rotation = random.nextInt(360);
        for (int idx = 0; idx < IMAGE_COUNT; ++idx) {
            Image subImage = explosion_animation_image.getSubImage(x, 0, 40, 50);
            subImage.rotate(random_flame_rotation);
            explosion_animation.addFrame(subImage, 200);
            x += 40;
        }
        explosion_animation.setLooping(false);
        return explosion_animation;
    }

    private Animation getNextFreshExplosionInstance() {
        Animation explosion = null;
        try {
            explosion = buffered_explosions.get(0);
            active_explosions.add(explosion);
            buffered_explosions.remove(explosion);
        } catch (Exception e) {
            System.out.println("Buffered rockets size too low! Increase size of buffered_rockets list!");
        }
        return explosion;
    }

    private void putExplosionInstanceBackToBuffer(Animation instance) {
        buffered_explosions.add(instance);
        active_explosions.remove(instance);
    }

    @Override
    public void update(int deltaTime) {
        super.update(deltaTime);
        starting_flame.update(deltaTime);
        time += deltaTime;

        projectile_collision_model.update(projectile_image.getRotation());

        if (time > CREATE_NEW_FLAME_TIMER) {
            time = 0;
            FlameInstance flameInstance = new FlameInstance(getNextFreshExplosionInstance(), projectile_pos.x, projectile_pos.y);
            flameInstance.setup();
            explosion_animations.add(flameInstance);
        }

        for (FlameInstance explosion_animation : explosion_animations)
            explosion_animation.update(deltaTime);
    }

    @Override
    public void draw(Graphics graphics) {
        for (FlameInstance explosion_animation : explosion_animations)
            explosion_animation.draw();

        starting_flame.draw(projectile_pos.x - ANIMATION_WIDTH_HALF, projectile_pos.y - ANIMATION_HEIGHT_HALF);
    }

    private class FlameInstance {
        Animation flame_animation;
        float xPos, yPos;

        FlameInstance(Animation flame_animation, float xPos, float yPos) {
            this.flame_animation = flame_animation;
            this.xPos = xPos;
            this.yPos = yPos;
        }

        public void update(int deltaTime) {
            flame_animation.update(deltaTime);

            if (flame_animation.isStopped()) {
                putExplosionInstanceBackToBuffer(flame_animation);
            }
        }

        public void draw() {
            flame_animation.draw(xPos - ANIMATION_WIDTH_HALF, yPos - ANIMATION_HEIGHT_HALF);
        }

        public void setup() {
            flame_animation.setCurrentFrame(0);
            flame_animation.start();
            int rotation = random.nextInt(360);
            for (int idx = 0; idx < flame_animation.getFrameCount(); ++idx) {
                flame_animation.getImage(idx).setRotation(rotation);
            }
        }
    }
}
