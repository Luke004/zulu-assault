package level_editor;

import audio.MenuSounds;
import graphics.fonts.FontManager;
import level_editor.toolbars.Toolbar;
import level_editor.toolbars.bottom.BottomToolbar;
import level_editor.toolbars.right.RightToolbar;
import level_editor.toolbars.right.screens.EntitySelector;
import level_editor.util.Elements;
import logic.Camera;
import main.ZuluAssault;
import models.Element;
import models.entities.Entity;
import models.entities.MovableEntity;
import models.items.Item;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;
import settings.UserSettings;

import java.util.ArrayList;
import java.util.List;

public class LevelEditor extends BasicGameState {

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
    private boolean allowMouseSlide;

    private static final float MOVE_SPEED = 0.3f, SCROLL_SPEED = 0.8f;

    private RightToolbar rightToolbar;
    private BottomToolbar bottomToolbar;
    private Camera camera;

    static {
        title_string = "LEVEL EDITOR";
    }

    public LevelEditor() {
        elements = new ArrayList<>();
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
            if (isMouseInEditor(mouseX, mouseY)) {
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
        if (isMouseInEditor(mouseX, mouseY)) {
            if (button == Input.MOUSE_LEFT_BUTTON) {
                if (selectedElement != null) {
                    Element copy = Elements.getCopyByName(selectedElement.getClass().getSimpleName(),
                            EntitySelector.isHostile, EntitySelector.isDrivable);
                    if (copy != null) {
                        MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
                        // deep copy the selected element
                        copy.setPosition(new Vector2f(selectedElement.getPosition().x + mapX,
                                selectedElement.getPosition().y + mapY));
                        // add rotation
                        if (selectedElement instanceof Entity) {
                            ((Entity) copy).setRotation(((Entity) selectedElement).getRotation());
                        }
                        elements.add(copy);
                    }
                }
            }
        } else {
            // mouse is not in editor -> is on a toolbar
            if (mouseY > bottomToolbar.getY()) {
                bottomToolbar.onMouseClick(button, mouseX, mouseY);
            } else {
                rightToolbar.onMouseClick(button, mouseX, mouseY);
            }
        }
    }

    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        title_string_drawer = FontManager.getStencilBigFont();
        //titleHeight = title_string_drawer.getHeight(title_string) - 7;

        Toolbar.Props.initMargin(gc.getWidth());
        rightToolbar = new RightToolbar(this, TITLE_RECT_HEIGHT + 1, gc);
        bottomToolbar = new BottomToolbar(rightToolbar, gc);

        TiledMap map = new TiledMap("assets/maps/level_1.tmx");

        camera = new Camera(gc, map);
        mapWidth = map.getWidth() * map.getTileWidth();
        mapHeight = map.getHeight() * map.getTileHeight();
        mapX = mapWidth / 2.f;
        mapY = mapHeight / 2.f;
        //camera.centerOn(mapX, mapY);
    }

    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int dt) {
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
                selectedElement.update(gc, dt);
            }
            selectedElement.getPosition().x = gc.getInput().getMouseX();
            selectedElement.getPosition().y = gc.getInput().getMouseY();
        }

        for (Element element : elements) {
            // show the drivable animation for friendly drivable entities
            if (element instanceof MovableEntity) {
                MovableEntity movableEntity = (MovableEntity) element;
                if (movableEntity.isDrivable() && !movableEntity.isHostile) {
                    ((MovableEntity) element).drivable_animation.update(dt);
                }
            }
        }

    }

    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics graphics) throws SlickException {
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
        if (isMouseInEditor(gc.getInput().getMouseX(), gc.getInput().getMouseY())) {
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

        rightToolbar.draw(gc, graphics);
        bottomToolbar.draw(gc, graphics);


    }

    public void setSelectedElement(Element element) {
        this.selectedElement = element;
        element.getBaseImage().setAlpha(0.7f);
    }

    public Element getSelectedElement() {
        return this.selectedElement;
    }

    private boolean isMouseInEditor(int mouseX, int mouseY) {
        return (mouseX < rightToolbar.getX() && mouseY > TITLE_RECT_HEIGHT && mouseY < bottomToolbar.getY());
    }

    @Override
    public int getID() {
        return ZuluAssault.LEVEL_EDITOR;
    }

}
