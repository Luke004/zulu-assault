package models.weapons.projectiles;

import logic.level_listeners.GroundTileDamager;

public interface iGroundTileDamageWeapon {
    void addListener(GroundTileDamager groundTileDamager);
}
