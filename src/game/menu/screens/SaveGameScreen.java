package game.menu.screens;

import game.audio.MenuSounds;
import game.levels.GameDataStorage;
import game.levels.Level;
import game.menu.elements.Buttons;
import game.util.saving.SaveUtil;
import game.util.saving.gameObjects.newGame.NewGameDataWrapper;
import game.util.saving.gameObjects.runningGame.RunningGameDataWrapper;
import level_editor.LevelEditor;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import settings.UserSettings;

/*
This class is basically like LoadGame but with another functionality if you double click a btn.
 */
public class SaveGameScreen extends LoadGameScreen {

    public SaveGameScreen(BasicGameState gameState, GameContainer gameContainer) {
        super(gameState, gameContainer);
    }

    /*
    * Override the loadLevel method from LoadGame menu and use this to save a level. Genius! :P
     */
    @Override
    protected void loadLevel(String name, int idx) {
        NewGameDataWrapper levelData = GameDataStorage.initGameData;
        if (levelData == null) {
            MenuSounds.ERROR_SOUND.play(1.f, UserSettings.soundVolume);
            return;
        }
        SaveUtil.saveRunningGameDataToXML(new RunningGameDataWrapper(
                levelData.levelName,
                Level.getAllElements(),
                Level.player.getEntity(),
                levelData.getAllWaypointLists(new LevelEditor()),
                levelData.getEntityConnections(levelData.getAllWaypointEntities()),
                levelData.mission_title,
                levelData.briefing_message,
                levelData.debriefing_message,
                levelData.musicIdx
        ), idx);
        SaveUtil.saveTMXMapData(Level.map.getMapLayers(), levelData.levelName, idx);
        Buttons.Button selectedBtn = buttons.getCurrentActiveBtn();
        selectedBtn.setDescription(this.generateLevelName(levelData.levelName));
        selectedBtn.changeColor(Color.white);
    }

}
