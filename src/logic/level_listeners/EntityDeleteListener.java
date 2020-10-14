package logic.level_listeners;

import models.entities.Entity;

public interface EntityDeleteListener {
    void notifyForEntityDestruction(Entity entity);
}
