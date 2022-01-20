package game.levels;

import game.util.saving.init.InitGameDataWrapper;
import game.util.saving.running.RunningGameDataWrapper;

/**
 * This class keeps the InitGameData and RunningGameData in storage so we only have to read and parse the .XML-data
 * once per level.
 */
public class GameDataStorage {
    public static InitGameDataWrapper initGameData;
    public static RunningGameDataWrapper runningGameData;
}
