package logic.level_listeners;

/*
this interface notifies the map about when having to place a burnt texture indicating that the tile has been damaged
on a GROUND TILE (-> ground tile != barracks, trees etc.)
 */

public interface GroundTileDamageListener {
    void notifyForGroundTileDamage(float xPos, float yPos);
}
