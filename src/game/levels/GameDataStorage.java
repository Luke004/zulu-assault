package game.levels;

import game.util.saving.gameObjects.newGame.NewGameDataWrapper;
import game.util.saving.gameObjects.runningGame.RunningGameDataWrapper;

/**
 * This class keeps the InitGameData and RunningGameData in storage so we only have to read and parse the .XML-data
 * once per level.
 */
public class GameDataStorage {
    public static NewGameDataWrapper initGameData;
    public static RunningGameDataWrapper runningGameData;
}
