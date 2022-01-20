package game.util.saving.running.data;

import game.util.saving.init.data.IEntityData;

/**
 * 'IEntityData'-class but extended with parameter 'currentHealth'.
 * Needed for saving and loading running games.
 */
public class REntityData extends IEntityData {
    public float currentHealth;
}
