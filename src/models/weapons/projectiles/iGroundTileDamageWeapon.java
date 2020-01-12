package models.weapons.projectiles;

import logic.level_listeners.GroundTileDamageListener;

public interface iGroundTileDamageWeapon {
    void addListener(GroundTileDamageListener groundTileDamageListener);

    GroundTileDamageListener getListener();
}
