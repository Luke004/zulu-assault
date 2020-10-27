package level_editor;

import game.audio.MenuSounds;
import game.graphics.fonts.FontManager;
import game.logic.Camera;
import game.models.Element;
import game.models.entities.Entity;
import game.models.entities.MovableEntity;
import game.models.items.Item;
import game.util.LevelDataStorage;
import level_editor.screens.windows.CenterPopupWindow;
import level_editor.screens.windows.Window;
import level_editor.screens.windows.toolbars.bottom.BottomToolbar;
import level_editor.screens.windows.toolbars.right.RightToolbar;
import level_editor.screens.windows.toolbars.right.screens.EntitySelector;
import level_editor.util.MapElements;
import main.ZuluAssault;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.tiled.TiledMap;
import settings.TileMapData;
import settings.UserSettings;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LevelEditor extends BasicGameState {

    // for choosing the map
    public static final String CUSTOM_MAPS_FOLDER = "custom_maps/";

    private String mapName;

    private List<Element> elements;
    private Element selectedElement;

    private static final String title_string;
    private static TrueTypeFont title_string_drawer;

    // helper vars
    private static final int TITLE_RECT_HEIGHT = 40;    // absolute height of the title rect

    // map coords
    private float mapX, mapY;
    private int mapWidth, mapHeight;
    private int prevMouseX, prevMouseY, mouseSlideX, mouseSlideY;   // to slide the map using mouse's right key
    private boolean allowMouseSlide, isMouseInEditor;

    private static final float MOVE_SPEED = 0.3f;

    // all windows of the level editor
    private RightToolbar rightToolbar;
    private BottomToolbar bottomToolbar;
    private CenterPopupWindow popupWindow;

    private Camera camera;

    static {
        title_string = "LEVEL EDITOR";
    }

    public LevelEditor() {
        elements = new ArrayList<>();
    }

    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        Window.Props.initMargin(gc.getWidth());
        title_string_drawer = FontManager.getStencilBigFont();
        rightToolbar = new RightToolbar(this, TITLE_RECT_HEIGHT + 1, gc);
        bottomToolbar = new BottomToolbar(this, (RightToolbar) rightToolbar, gc, sbg);
    }

    @Override
    public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
        gc.setFullscreen(false);
        this.elements.clear();
        // prompt the user to select a map file
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Select Level File");
        fc.setCurrentDirectory(new File(CUSTOM_MAPS_FOLDER));
        fc.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".tmx");
            }

            @Override
            public String getDescription() {
                return ".tmx (TileMap created using Tiled)";
            }
        });
        fc.setAcceptAllFileFilterUsed(false);

        int returnValue = fc.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fc.getSelectedFile();
            this.mapName = selectedFile.getName();
            TiledMap map = new TiledMap(CUSTOM_MAPS_FOLDER + mapName, TileMapData.TILESETS_LOCATION);
            camera = new Camera(gc, map);
            mapWidth = map.getWidth() * map.getTileWidth();
            mapHeight = map.getHeight() * map.getTileHeight();
            mapX = mapWidth / 2.f;
            mapY = mapHeight / 2.f;

            // attempt to load already created map data
            LevelDataStorage levelDataStorage = LevelDataStorage.loadLevel(getSimpleMapName(), false);
            if (levelDataStorage != null) {
                this.elements.addAll(levelDataStorage.getAllElements());
                this.elements.addAll(levelDataStorage.getAllEntities());
            }

        } else {
            // USER CANCELLED
            sbg.enterState(ZuluAssault.MAIN_MENU, new FadeOutTransition(), new FadeInTransition());
        }
        //gc.setFullscreen(true);   // TODO: 26.10.2020 - don't forget to enable this for a build!

    }

    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics graphics) throws SlickException {
        if (camera == null) return;
        camera.drawMap();
        camera.translateGraphics();
        // draw all instances that are moving with the map (ENTITIES, ITEMS, CIRCLES etc..) below

        // draw placed elements

        for (Element element : elements) {
            element.draw(graphics);
        }


        camera.untranslateGraphics();
        // draw all instances that are static above the map (HUD) below

        // draw the selected element
        if (isMouseInEditor) {
            if (selectedElement != null) {
                selectedElement.draw(graphics);
            }
        }

        // draw the title rect
        graphics.setColor(Color.black);
        graphics.fillRect(0, 0, gc.getWidth(), TITLE_RECT_HEIGHT);
        graphics.setColor(Color.lightGray);
        graphics.setLineWidth(1);
        graphics.drawLine(0,
                TITLE_RECT_HEIGHT,
                gc.getWidth(),
                TITLE_RECT_HEIGHT);
        title_string_drawer.drawString(
                gc.getWidth() / 2.f - title_string_drawer.getWidth(title_string) / 2.f,     // center text
                2,      // top margin
                title_string,
                Color.lightGray
        );

        if (hasPopupWindow()) {
            popupWindow.draw(gc, graphics);
        }

        rightToolbar.draw(gc, graphics);
        bottomToolbar.draw(gc, graphics);

    }

    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int dt) {
        if (hasPopupWindow()) {
            popupWindow.update(gc);
            return;
        }

        camera.centerOn(mapX - mouseSlideX,
                mapY - mouseSlideY,
                gc.getHeight(), gc.getWidth(), rightToolbar.getWidth(), TITLE_RECT_HEIGHT, bottomToolbar.getHeight());

        rightToolbar.update(gc);
        bottomToolbar.update(gc);

        // move the map using keys
        if (gc.getInput().isKeyDown(Input.KEY_UP) || gc.getInput().isKeyDown(Input.KEY_W)) {
            mapY -= MOVE_SPEED * dt;
            if (mapY < -TITLE_RECT_HEIGHT) mapY = -TITLE_RECT_HEIGHT;
        }
        if (gc.getInput().isKeyDown(Input.KEY_DOWN) || gc.getInput().isKeyDown(Input.KEY_S)) {
            mapY += MOVE_SPEED * dt;
            if (mapY > mapHeight + bottomToolbar.getHeight() - gc.getHeight()) {
                mapY = mapHeight + bottomToolbar.getHeight() - gc.getHeight();
            }
        }
        if (gc.getInput().isKeyDown(Input.KEY_LEFT) || gc.getInput().isKeyDown(Input.KEY_A)) {
            mapX -= MOVE_SPEED * dt;
            if (mapX < 0) mapX = 0;
        }
        if (gc.getInput().isKeyDown(Input.KEY_RIGHT) || gc.getInput().isKeyDown(Input.KEY_D)) {
            mapX += MOVE_SPEED * dt;
            if (mapX > mapWidth - gc.getWidth() + rightToolbar.getWidth()) {
                mapX = mapWidth - gc.getWidth() + rightToolbar.getWidth();
            }
        }

        // slide the map while right mouse key is pressed
        if (gc.getInput().isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) {
            if (allowMouseSlide) {
                mouseSlideX = gc.getInput().getMouseX() - prevMouseX;
                mouseSlideY = gc.getInput().getMouseY() - prevMouseY;

                // make it unable to slide past the borders
                if (mapY < -TITLE_RECT_HEIGHT) mapY = -TITLE_RECT_HEIGHT;
                if (mapY > mapHeight + bottomToolbar.getHeight() - gc.getHeight()) {
                    mapY = mapHeight + bottomToolbar.getHeight() - gc.getHeight();
                }
                if (mapX < 0) mapX = 0;
                if (mapX > mapWidth - gc.getWidth() + rightToolbar.getWidth()) {
                    mapX = mapWidth - gc.getWidth() + rightToolbar.getWidth();
                }
            }
        }

        if (selectedElement != null) {
            if (!(selectedElement instanceof Item)) {
                selectedElement.editorUpdate(gc, dt);
            }
            selectedElement.getPosition().x = gc.getInput().getMouseX();
            selectedElement.getPosition().y = gc.getInput().getMouseY();
        }

        for (Element element : elements) {
            // show the drivable animation for friendly drivable entities
            if (element instanceof MovableEntity) {
                MovableEntity movableEntity = (MovableEntity) element;
                if (movableEntity.isDrivable && !movableEntity.isHostile) {
                    ((MovableEntity) element).drivable_animation.update(dt);
                }
            }
        }

        this.isMouseInEditor = (gc.getInput().getMouseX() < rightToolbar.getX()
                && gc.getInput().getMouseY() > TITLE_RECT_HEIGHT
                && gc.getInput().getMouseY() < bottomToolbar.getY());

    }

    @Override
    public void mouseWheelMoved(int change) {
        change /= 20.f;
        if (selectedElement != null) {
            if (selectedElement instanceof Entity) {
                Entity entity = (Entity) selectedElement;
                entity.setRotation(entity.getRotation() + change);
            }
            /* else {   // should not allow: rotation of items and circles
                selectedElement.getBaseImage().rotate(change);
            }
             */
        }
    }

    @Override
    public void mousePressed(int button, int mouseX, int mouseY) {
        if (button == Input.MOUSE_RIGHT_BUTTON) {
            if (isMouseInEditor) {
                allowMouseSlide = true;
                prevMouseX = mouseX;
                prevMouseY = mouseY;
            } else {
                allowMouseSlide = false;
            }
        }
    }

    @Override
    public void mouseReleased(int button, int mouseX, int mouseY) {
        if (button == Input.MOUSE_RIGHT_BUTTON) {
            mapX = mapX - mouseSlideX;
            mapY = mapY - mouseSlideY;
            mouseSlideX = 0;
            mouseSlideY = 0;
        }
    }

    @Override
    public void mouseClicked(int button, int mouseX, int mouseY, int clickCount) {
        if (hasPopupWindow()) {
            if (popupWindow.isMouseOver(mouseX, mouseY)) {
                popupWindow.onMouseClick(button, mouseX, mouseY);
            }
            return;
        }

        if (isMouseInEditor) {
            if (button == Input.MOUSE_LEFT_BUTTON) {
                if (selectedElement != null) {
                    Element copy = MapElements.getCopyByName(selectedElement.getClass().getSimpleName(),
                            EntitySelector.isHostile, EntitySelector.isDrivable);
                    if (copy != null) {
                        MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
                        // deep copy the selected element
                        copy.setPosition(new Vector2f(selectedElement.getPosition().x + mapX,
                                selectedElement.getPosition().y + mapY));
                        // add rotation and mandatory
                        if (selectedElement instanceof Entity) {
                            Entity entityCopy = (Entity) copy;
                            entityCopy.setRotation(((Entity) selectedElement).getRotation());
                            entityCopy.isMandatory = EntitySelector.isMandatory;
                        }
                        elements.add(copy);
                    }
                }
            }
        } else {
            // mouse is not in editor -> is on a toolbar
            if (rightToolbar.isMouseOver(mouseX, mouseY)) {
                rightToolbar.onMouseClick(button, mouseX, mouseY);
            } else if (bottomToolbar.isMouseOver(mouseX, mouseY)) {
                bottomToolbar.onMouseClick(button, mouseX, mouseY);
            }
        }
    }

    public void setSelectedElement(Element element) {
        this.selectedElement = element;
        element.getBaseImage().setAlpha(0.7f);
    }

    public Element getSelectedElement() {
        return this.selectedElement;
    }

    public List<Element> getElements() {
        return elements;
    }

    private boolean hasPopupWindow() {
        return this.popupWindow != null;
    }

    public void setPopupWindow(CenterPopupWindow popupWindow) {
        isMouseInEditor = false;
        this.popupWindow = popupWindow;
    }

    public String getSimpleMapName() {
        return this.mapName.substring(0, this.mapName.length() - 4);
    }

    @Override
    public int getID() {
        return ZuluAssault.LEVEL_EDITOR;
    }

}
