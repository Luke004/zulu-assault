package game.models.weapons.projectiles;

import game.models.entities.Entity;
import game.util.WayPointManager;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

import java.util.*;

import static game.levels.Level.*;

public class GuidedRocket extends GroundRocket {

    // vars needed to let the clouds follow the missile
    private List<Vector2f> prevPositions;
    private static Image[] rocketAnimationSubImages;
    private static final int SPACE_BETWEEN_SUB_IMAGES = 4;

    // guided weapon specs
    private static final float GUIDE_INTENSITY = .09f;   // how strong the rocket guides towards enemy
    private static final float GUIDE_ACTIVATE_DISTANCE = 300;   // distance from when the guided rocket starts flying towards enemy

    // logic helper vars
    private boolean hasAcquiredTarget;
    private Entity target;
    private Map<Entity, Float> possibleTargets;
    private List<Entity> enemyEntities;

    public GuidedRocket(Vector2f startPos, Vector2f dir, float rotation, Texture projectile_texture, Animation rocket_animation,
                        boolean isHostile) {
        super(startPos, dir, rotation, projectile_texture, rocket_animation);
        possibleTargets = new HashMap<>();
        prevPositions = new ArrayList<>();

        if (rocketAnimationSubImages == null) {
            rocketAnimationSubImages = new Image[rocket_animation.getFrameCount()];
            int[] y_offsets = new int[rocketAnimationSubImages.length];
            assert (rocketAnimationSubImages.length == 8);
            y_offsets[0] = 0;
            y_offsets[1] = 17;
            y_offsets[2] = 32;
            y_offsets[3] = 44;
            y_offsets[4] = 57;
            y_offsets[5] = 71;
            y_offsets[6] = 87;
            y_offsets[7] = 103;

            int[] height_offsets = new int[rocketAnimationSubImages.length];
            height_offsets[0] = 17;
            height_offsets[1] = 15;
            height_offsets[2] = 12;
            height_offsets[3] = 13;
            height_offsets[4] = 14;
            height_offsets[5] = 16;
            height_offsets[6] = 16;
            height_offsets[7] = 20;

            for (int i = 0; i < rocketAnimationSubImages.length; ++i) {
                rocketAnimationSubImages[i] = rocket_animation.getImage(i).getSubImage(0, y_offsets[i], 20, height_offsets[i]);
            }
        }

        if (isHostile) {
            enemyEntities = all_friendly_entities;
            enemyEntities.add(player.getEntity());
        } else {
            enemyEntities = all_hostile_entities;
        }
    }

    @Override
    public void draw(Graphics graphics) {
        this.projectile_image.draw(this.projectile_pos.x - WIDTH_HALF, this.projectile_pos.y - HEIGHT_HALF);

        for (int i = 0; i < rocketAnimationSubImages.length; ++i) {
            if (prevPositions.size() < (i + 1) * SPACE_BETWEEN_SUB_IMAGES)
                break;   // if prev position list is too small, cancel!
            rocketAnimationSubImages[i].drawCentered(
                    prevPositions.get(prevPositions.size() - (i + 1) * SPACE_BETWEEN_SUB_IMAGES).x,
                    prevPositions.get(prevPositions.size() - (i + 1) * SPACE_BETWEEN_SUB_IMAGES).y);
        }
    }

    @Override
    public void update(int deltaTime) {
        super.update(deltaTime);

        // keep track of the previous positions of the guided missile, so the clouds can follow its path
        prevPositions.add(new Vector2f(projectile_pos));

        if (!hasAcquiredTarget) {
            searchForTarget();
        } else {
            // make projectiles guide towards the target entity

            // stop guiding if the rocket is getting too far away and thus has no possibility to reach the target
            if (WayPointManager.dist(projectile_pos, target.getPosition()) > GUIDE_ACTIVATE_DISTANCE + 50) return;

            float angle = WayPointManager.calculateAngleToRotateTo(projectile_pos,
                    new Vector2f(target.position.x, target.position.y));

            float shortest_angle = WayPointManager.getShortestSignedAngle(projectile_image.getRotation(), angle);

            if (shortest_angle > 0) {
                projectile_image.setRotation(projectile_image.getRotation() + GUIDE_INTENSITY * deltaTime);
            } else {
                projectile_image.setRotation(projectile_image.getRotation() - GUIDE_INTENSITY * deltaTime);
            }

            projectile_dir.x = (float) Math.sin(projectile_image.getRotation() * Math.PI / 180);
            projectile_dir.y = (float) -Math.cos(projectile_image.getRotation() * Math.PI / 180);
        }
    }

    /* Look for the closest enemy target in reach and lock it */
    private void searchForTarget() {
        float closest_dist = Float.MAX_VALUE;
        Entity closestEntity = null;
        for (Entity entity : enemyEntities) {
            float xPos = entity.position.x;
            float yPos = entity.position.y;
            float dist = WayPointManager.dist(projectile_pos, new Vector2f(xPos, yPos));
            if (dist < closest_dist) {
                closest_dist = dist;
                closestEntity = entity;
            }
        }
        if (closestEntity == null) return;
        if (closest_dist <= GUIDE_ACTIVATE_DISTANCE) {
            if (!isTargetBehind(closestEntity)) {
                hasAcquiredTarget = true;
                target = closestEntity;
            } else {
                // continue searching for the closest target in front of the guided rocket

                // create a list of possible targets
                for (Entity entity : all_hostile_entities) {
                    if (entity.equals(closestEntity)) continue;
                    float xPos = entity.position.x;
                    float yPos = entity.position.y;
                    float dist = WayPointManager.dist(projectile_pos, new Vector2f(xPos, yPos));
                    if (dist <= GUIDE_ACTIVATE_DISTANCE) {
                        possibleTargets.put(entity, dist);
                    }
                }

                while (!possibleTargets.isEmpty()) {
                    Map.Entry<Entity, Float> minEntry = null;
                    for (Map.Entry<Entity, Float> entry : possibleTargets.entrySet()) {
                        if (minEntry == null || entry.getValue().compareTo(minEntry.getValue()) < 0) {
                            minEntry = entry;
                        }
                    }
                    if (!isTargetBehind(minEntry.getKey())) {
                        // target found
                        hasAcquiredTarget = true;
                        target = minEntry.getKey();
                        return;
                    } else {
                        possibleTargets.remove(minEntry.getKey());
                    }
                }
            }
        }
    }

    private boolean isTargetBehind(Entity target) {
        float angleToTarget = WayPointManager.calculateAngleToRotateTo(projectile_pos,
                new Vector2f(target.position.x, target.position.y));
        float angleDiff = projectile_image.getRotation() - angleToTarget;
        angleDiff %= 360;
        return (angleDiff > -270 && angleDiff < -90) || (angleDiff > 90 && angleDiff < 270);
    }

}
