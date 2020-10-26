package game.models.weapons.projectiles;

import game.logic.level_listeners.GroundTileDamager;

public interface iGroundTileDamageWeapon {
    void addListener(GroundTileDamager groundTileDamager);
}
