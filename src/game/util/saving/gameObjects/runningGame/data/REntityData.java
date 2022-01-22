package game.util.saving.gameObjects.runningGame.data;

import game.util.saving.gameObjects.newGame.data.IEntityData;

/**
 * 'IEntityData'-class but extended with parameter 'currentHealth'.
 * Needed for saving and loading running games.
 */
public class REntityData extends IEntityData {
    public float currentHealth;
}
