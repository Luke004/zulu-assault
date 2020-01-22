package logic.level_listeners;

import models.war_attenders.WarAttender;

public interface WarAttenderDeleteListener {
    void notifyForWarAttenderDeletion(WarAttender warAttender);
    void notifyForDestructibleTileShotDeletion(float xPos, float yPos);
}
