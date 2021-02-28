package game.models.weapons.projectiles;

import game.models.entities.Entity;
import game.util.WayPointManager;
import org.newdawn.slick.Animation;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;

import java.util.*;

import static game.levels.Level.all_hostile_entities;

public class GuidedRocket extends GroundRocket {

    private static final float GUIDE_INTENSITY = .06f;   // how strong the rocket guides towards enemy
    private static final float GUIDE_ACTIVATE_DISTANCE = 300;   // distance from when the guided rocket starts flying towards enemy

    private boolean hasAcquiredTarget;
    private Entity target;
    private Map<Entity, Float> possibleTargets;

    public GuidedRocket(Vector2f startPos, Vector2f dir, float rotation, Texture projectile_texture, Animation rocket_animation) {
        super(startPos, dir, rotation, projectile_texture, rocket_animation);
        possibleTargets = new HashMap<>();
    }

    @Override
    public void update(int deltaTime) {
        super.update(deltaTime);
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

            rocket_animation.getCurrentFrame().setRotation(projectile_image.getRotation());

            projectile_dir.x = (float) Math.sin(projectile_image.getRotation() * Math.PI / 180);
            projectile_dir.y = (float) -Math.cos(projectile_image.getRotation() * Math.PI / 180);
        }
    }

    /* Look for the closest enemy target in reach and lock it */
    private void searchForTarget() {
        float closest_dist = Float.MAX_VALUE;
        Entity closestEntity = null;
        for (Entity entity : all_hostile_entities) {
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
