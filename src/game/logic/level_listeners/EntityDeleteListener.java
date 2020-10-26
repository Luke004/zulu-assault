package game.logic.level_listeners;

import game.models.entities.Entity;

public interface EntityDeleteListener {
    void notifyForEntityDestruction(Entity entity);
}
