package logic;

import models.war_attenders.WarAttender;

public interface WarAttenderDeleteListener {
    void notifyForDeletion(WarAttender warAttender);
    void notifyForDeletion(float xPos, float yPos);
}
