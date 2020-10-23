package level_editor;

import graphics.fonts.FontManager;
import level_editor.toolbar.Toolbar;
import logic.Camera;
import main.ZuluAssault;
import models.Element;
import models.entities.Entity;
import models.entities.tanks.CannonTank;
import models.interaction_circles.HealthCircle;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import java.util.ArrayList;
import java.util.List;

public class LevelEditor extends BasicGameState {

    List<Element> elements;

    Element selectedElement;

    private static final String title_string;
    private static TrueTypeFont title_string_drawer;

    // helper vars
    private int titleHeight;    // absolute width of the title

    // map coords
    private float mapX, mapY;
    private int mapWidth, mapHeight;

    private static final float MOVE_SPEED = 0.3f;

    private Toolbar toolbar;
    private Camera camera;

    static {
        title_string = "LEVEL EDITOR";
    }

    public LevelEditor() {
        elements = new ArrayList<>();
    }


    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        title_string_drawer = FontManager.getStencilBigFont();
        titleHeight = title_string_drawer.getHeight(title_string) - 7;

        toolbar = new Toolbar(this, titleHeight + 1, gc);

        TiledMap map = new TiledMap("assets/maps/level_1.tmx");

        camera = new Camera(gc, map);
        mapWidth = map.getWidth() * map.getTileWidth();
        mapHeight = map.getHeight() * map.getTileHeight();
        mapX = mapWidth / 2.f;
        mapY = mapHeight / 2.f;
        //camera.centerOn(mapX, mapY);

        //entities.add(new CannonTank(new Vector2f(100, 100), false, true));

        selectedElement = new CannonTank(new Vector2f(0, 0), false, false);
        selectedElement.base_image.setAlpha(0.7f);
    }

    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int dt) {
        camera.centerOn(mapX, mapY, gc.getHeight(), gc.getWidth(), toolbar.getWidth());

        toolbar.update(gc, sbg, dt);

        // move the map using keys
        if (gc.getInput().isKeyDown(Input.KEY_UP) || gc.getInput().isKeyDown(Input.KEY_W)) {
            mapY -= MOVE_SPEED * dt;
            if (mapY < 0) mapY = 0;
        }
        if (gc.getInput().isKeyDown(Input.KEY_DOWN) || gc.getInput().isKeyDown(Input.KEY_S)) {
            mapY += MOVE_SPEED * dt;
            if (mapY > mapHeight - gc.getHeight()) mapY = mapHeight - gc.getHeight();
        }
        if (gc.getInput().isKeyDown(Input.KEY_LEFT) || gc.getInput().isKeyDown(Input.KEY_A)) {
            mapX -= MOVE_SPEED * dt;
            if (mapX < 0) mapX = 0;
        }
        if (gc.getInput().isKeyDown(Input.KEY_RIGHT) || gc.getInput().isKeyDown(Input.KEY_D)) {
            mapX += MOVE_SPEED * dt;
            if (mapX > mapWidth - gc.getWidth() + toolbar.getWidth())
                mapX = mapWidth - gc.getWidth() + toolbar.getWidth();
        }

        if (gc.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
            if (selectedElement != null) {
                selectedElement.base_image.setAlpha(1.f);
                //entities.add((Entity) Entity.deepCopy(currentEntity));

            }
        }

        if (selectedElement != null) {
            selectedElement.update(gc, dt);
            selectedElement.getPosition().x = gc.getInput().getMouseX();
            selectedElement.getPosition().y = gc.getInput().getMouseY();
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

        // title
        graphics.setColor(Color.black);
        graphics.fillRect(0, 0, gc.getWidth(), titleHeight);
        graphics.setColor(Color.lightGray);
        graphics.setLineWidth(1);
        graphics.drawLine(0,
                titleHeight,
                gc.getWidth(),
                titleHeight);
        title_string_drawer.drawString(
                gc.getWidth() / 2.f - title_string_drawer.getWidth(title_string) / 2.f,     // center text
                2,      // top margin
                title_string,
                Color.lightGray
        );

        toolbar.draw(gc, graphics);

        if (isMouseInEditor(gc)) {
            if (selectedElement != null)
                selectedElement.draw(graphics);
        }


    }

    public void setSelectedElement(Element element) {
        this.selectedElement = element;
    }


    private boolean isMouseInEditor(GameContainer gc) {
        return (gc.getInput().getMouseX() < toolbar.getX() && gc.getInput().getMouseY() > titleHeight);
    }

    @Override
    public int getID() {
        return ZuluAssault.LEVEL_EDITOR;
    }

}
