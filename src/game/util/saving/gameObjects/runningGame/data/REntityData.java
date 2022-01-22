package game.util.saving.gameObjects.runningGame.data;

import game.util.saving.gameObjects.newGame.data.NEntityData;

import java.util.List;
import java.util.Map;

/**
 * 'IEntityData'-class but with extended data needed for running games.
 * Needed for saving and loading running games.
 */
public class REntityData extends NEntityData {
    public float currentHealth;
    public Map<String, List<ProjectileData>> projectileMap;
}


