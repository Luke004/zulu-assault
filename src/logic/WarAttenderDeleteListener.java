package logic;

import models.war_attenders.MovableWarAttender;

public interface WarAttenderDeleteListener {
    void notifyForDeletion(MovableWarAttender warAttender, boolean isHostile);
}
