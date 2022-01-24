package game.menu.screens;

import game.audio.MenuSounds;
import game.graphics.fonts.FontManager;
import game.levels.GameDataStorage;
import game.levels.Level;
import game.levels.LevelManager;
import game.menu.Menu;
import game.menu.elements.Arrow;
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
import settings.SettingStorage;
import settings.UserSettings;

import java.awt.*;
import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

import static game.menu.Menu.*;
import static game.menu.screens.MainScreen.MENU_OPTION_HEIGHT;

public class LoadGameScreen extends AbstractMenuScreen {

    private final Arrow arrow;
    private final int back_btn_width, back_btn_height;
    private final Vector2f back_btn_position;
    protected Buttons buttons;
    private static final TrueTypeFont menu_drawer;
    private static final Map<Integer, String> levelNameMap;
    private boolean isArrowOnBackBtn;

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
                20.f);
        isArrowOnBackBtn = true;
        arrow = new Arrow(gameContainer, 1, (int) back_btn_position.y);
        Texture button_texture_inactive;
        try {
            button_texture_inactive = new Image("assets/menus/green_button_inactive.png").getTexture();
            Texture button_texture_active = new Image("assets/menus/green_button_active.png").getTexture();
            buttons = new Buttons(button_texture_active, button_texture_inactive,
                    new Vector2f(100, 100), new String[]{
                    "EMPTY", "EMPTY", "EMPTY", "EMPTY", "EMPTY", "EMPTY",
                    "EMPTY", "EMPTY", "EMPTY", "EMPTY", "EMPTY", "EMPTY"
            }, false, Color.darkGray);
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(GameContainer gameContainer) {
        super.render(gameContainer);
        menu_drawer.drawString(
                back_btn_position.x,
                back_btn_position.y,
                "BACK",
                Color.lightGray);
        if (isArrowOnBackBtn) arrow.draw();
        buttons.draw();
    }

    @Override
    public void handleKeyInput(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        if (gameContainer.getInput().isKeyPressed(Input.KEY_UP)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
            if (isArrowOnBackBtn) return;
            if (buttons.getCurrentButtonIdx() == 0) {
                buttons.getButtonByIdx(0).setActive(false);
                isArrowOnBackBtn = true;
                return;
            }
            buttons.onUpKeyPress();
        } else if (gameContainer.getInput().isKeyPressed(Input.KEY_DOWN)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
            if (isArrowOnBackBtn) {
                isArrowOnBackBtn = false;
                buttons.getButtonByIdx(0).setActive(true);
                return;
            }
            buttons.onDownKeyPress();
        } else if (gameContainer.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
            returnToPreviousMenu();
        } else if (gameContainer.getInput().isKeyPressed(Input.KEY_ENTER)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
            if (isArrowOnBackBtn) {
                returnToPreviousMenu();
                return;
            }
            this.loadLevel(levelNameMap.get(buttons.getCurrentButtonIdx() + 1), buttons.getCurrentButtonIdx() + 1);
        }
    }

    @Override
    public void update(GameContainer gc, StateBasedGame sbg) {
        super.update(gc, sbg);
        // MOUSE
        if (gc.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
            onMouseClick(gc, sbg,
                    gc.getInput().getMouseX(),
                    gc.getInput().getMouseY());
        }
    }

    @Override
    public void onMouseClick(GameContainer gameContainer, StateBasedGame stateBasedGame, int mouseX, int mouseY) {
        if (mouseX > back_btn_position.x && mouseX < back_btn_position.x + back_btn_width) {
            if (mouseY > back_btn_position.y && mouseY < back_btn_position.y + back_btn_height) {
                MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
                Menu.returnToPreviousMenu();
                return;
            }
        }
        int idx = buttons.isClicked(mouseX, mouseY);
        if (idx != -1) {
            isArrowOnBackBtn = false;
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
        }
        if (idx == -2) return;
        this.loadLevel(levelNameMap.get(buttons.getCurrentButtonIdx() + 1), buttons.getCurrentButtonIdx() + 1);
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

    protected void loadLevel(String name, int idx) {
        if (buttons.getCurrentActiveBtn().getDescription().equals("EMPTY")) {
            MenuSounds.ERROR_SOUND.play(1.f, UserSettings.soundVolume);
            return;
        }
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
                loadGameBtn.changeColor(Color.white);
                saveGameFileName = saveGameFileName.substring(0, saveGameFileName.length() - 4); // cut ".tmx"
                levelNameMap.put(loadIdx, saveGameFileName);
                loadGameBtn.setDescription(this.generateLevelName(saveGameFileName));
            }
        }
        buttons.reset();
        this.isArrowOnBackBtn = true;
    }

    protected String generateLevelName(String saveGameFileName){
        if (Level.isOfficialLevel(saveGameFileName)) {
            return "Level " + saveGameFileName.substring(saveGameFileName.indexOf("_") + 1);
        } else {
            // custom level
            return saveGameFileName.substring(0, saveGameFileName.length() - 4) + " (custom)";
        }
    }

    @Override
    public void onLeaveState(GameContainer gameContainer) {
        main_menu_intro_sound.stop();
        main_menu_music.stop();
    }

}
