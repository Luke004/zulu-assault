package game.menu.screens;

import game.audio.MenuSounds;
import game.graphics.fonts.FontManager;
import game.levels.GameDataStorage;
import game.levels.Level;
import game.levels.LevelManager;
import game.menu.elements.Buttons;
import game.util.saving.SaveUtil;
import game.util.saving.gameObjects.runningGame.RunningGameDataWrapper;
import org.newdawn.slick.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import settings.UserSettings;

import java.awt.*;
import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

import static game.menu.screens.MainScreen.MENU_OPTION_HEIGHT;

public class LoadGameScreen extends AbstractMenuScreen {

    private int back_btn_width, back_btn_height;
    private Vector2f back_btn_position;
    private Buttons buttons;
    private static final TrueTypeFont menu_drawer;
    private static Map<Integer, String> levelNameMap;

    static {
        menu_drawer = FontManager.getStencilBigFont();
        levelNameMap = new HashMap<>();
    }

    public LoadGameScreen(BasicGameState gameState, GameContainer gameContainer) {
        super(gameState);
        back_btn_width = menu_drawer.getWidth("BACK");
        back_btn_height = MENU_OPTION_HEIGHT;
        back_btn_position = new Vector2f(
                gameContainer.getWidth() / 2.f - back_btn_width / 2.f,
                gameContainer.getHeight() / 2.f);
        Texture button_texture_inactive;
        try {
            button_texture_inactive = new Image("assets/menus/green_button_inactive.png").getTexture();
            Texture button_texture_active = new Image("assets/menus/green_button_active.png").getTexture();
            buttons = new Buttons(button_texture_active, button_texture_inactive,
                    new Vector2f(100, 100), new String[]{
                    "EMPTY", "EMPTY", "EMPTY", "EMPTY", "EMPTY", "EMPTY",
                    "EMPTY", "EMPTY", "EMPTY", "EMPTY", "EMPTY", "EMPTY"
            }, Color.darkGray);
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(GameContainer gameContainer) {
        super.render(gameContainer);
        menu_drawer.drawString(
                gameContainer.getWidth() / 2.f - back_btn_width / 2.f,
                20.f,
                "BACK",
                Color.lightGray);
        buttons.draw();
    }

    @Override
    public void handleKeyInput(GameContainer gameContainer, StateBasedGame stateBasedGame) {

    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        // MOUSE
        if (gameContainer.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
            onMouseClick(gameContainer, stateBasedGame,
                    gameContainer.getInput().getMouseX(),
                    gameContainer.getInput().getMouseY());
        }
    }

    @Override
    public void onMouseClick(GameContainer gameContainer, StateBasedGame stateBasedGame, int mouseX, int mouseY) {
        int idx = buttons.isClicked(mouseX, mouseY);
        if (idx != -1) MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
        // look for the save game folder of the selected idx and attempt to load the game
        File folder = new File(SaveUtil.RUNNING_GAMES_DATA_SAVE_FOLDER);
        File[] saveGameFolders = folder.listFiles(this::isInLevelIdxFolder);
        if (saveGameFolders == null) return;
        for (File file : saveGameFolders) {
            int levelIdx;
            try {
                levelIdx = Integer.parseInt(file.getName());
                if (levelIdx == idx + 1) {
                    this.loadLevel(levelNameMap.get(levelIdx), levelIdx);
                }
            } catch (Exception ignored) {
            }
        }

    }

    private boolean isInLevelIdxFolder(File file) {
        if (!file.isDirectory()) return false;
        try {
            int num = Integer.parseInt(file.getName());
            return num >= 1 && num <= 12;
        } catch (Exception ignored) {
        }
        return false;
    }

    private void loadLevel(String name, int idx) {
        RunningGameDataWrapper gameData = SaveUtil.loadRunningGameDataFromXML(name, idx);
        GameDataStorage.runningGameData = gameData; // save game data in storage
        if (gameData == null) {
            MenuSounds.ERROR_SOUND.play(1.f, UserSettings.soundVolume);
            return;
        }
        RunningGameDataWrapper.levelIdxToLoad = idx;
        LevelManager.loadExistingGame(name, basicGameState, gameData);
    }

    @Override
    public void onEnterState(GameContainer gameContainer) {
        File folder = new File(SaveUtil.RUNNING_GAMES_DATA_SAVE_FOLDER);
        File[] saveGameFolders = folder.listFiles(this::isInLevelIdxFolder);
        if (saveGameFolders == null) return;
        for (File saveGame : saveGameFolders) {
            File gameDataFolder = new File(SaveUtil.RUNNING_GAMES_DATA_SAVE_FOLDER + File.separator
                    + saveGame.getName());
            File[] saveGameFiles = gameDataFolder.listFiles();
            if (saveGameFiles == null) continue;
            for (File saveGameFile : saveGameFiles) {
                String saveGameFileName = saveGameFile.getName();
                if (saveGameFileName.endsWith("_layers.xml")) continue; // ignore the "_layers.xml" save file
                if (!saveGameFileName.endsWith(".xml")) continue; // not a xml - can't be loaded
                int loadIdx = Integer.parseInt(saveGame.getName());
                Buttons.Button loadGameBtn = buttons.getButtonByIdx(loadIdx - 1);
                saveGameFileName = saveGameFileName.substring(0, saveGameFileName.length() - 4); // cut ".tmx"
                levelNameMap.put(loadIdx, saveGameFileName);
                String btnDescription;
                if (Level.isOfficialLevel(saveGameFileName)) {
                    btnDescription = "Level " + saveGameFileName.substring(saveGameFileName.indexOf("_") + 1);
                } else {
                    // custom level
                    btnDescription = saveGameFileName.substring(0, saveGameFileName.length() - 4) + " (custom)";
                }
                loadGameBtn.setDescription(btnDescription);
            }
        }

    }

    @Override
    public void onLeaveState(GameContainer gameContainer) {

    }
}
