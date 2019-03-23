package levels;

import logic.Camera;
import logic.CollisionHandler;
import logic.KeyInputHandler;
import models.HUD;
import models.interaction_circles.InteractionCircle;
import models.war_attenders.MovableWarAttender;
import models.war_attenders.WarAttender;
import models.war_attenders.tanks.FlamethrowerTank;
import models.war_attenders.windmills.Windmill;
import models.war_attenders.windmills.WindmillGreen;
import models.war_attenders.windmills.WindmillGrey;
import models.war_attenders.windmills.WindmillYellow;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TileSet;
import org.newdawn.slick.tiled.TiledMap;
import player.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLevel extends BasicGame {
    Player player;
    TiledMap map;
    List<Windmill> enemy_windmills;
    List<InteractionCircle> interaction_circles;
    List<MovableWarAttender> friendly_war_attenders, hostile_war_attenders;
    KeyInputHandler keyInputHandler;
    CollisionHandler collisionHandler;
    private Camera camera;
    private HUD hud;

    public AbstractLevel(String title) {
        super(title);
        hostile_war_attenders = new ArrayList<>();
        friendly_war_attenders = new ArrayList<>();
        enemy_windmills = new ArrayList<>();
        interaction_circles = new ArrayList<>();
        player = new Player();
    }

    @Override
    public void init(GameContainer gameContainer) throws SlickException {
        setupWindmills();
        camera = new Camera(gameContainer, map);
        hud = new HUD(player, gameContainer);
        player.addListener(hud);
        keyInputHandler = new KeyInputHandler(player, friendly_war_attenders);     // handle key inputs
        collisionHandler = new CollisionHandler(player, map, friendly_war_attenders, hostile_war_attenders, enemy_windmills, interaction_circles);    // handle collisions
    }

    private void setupWindmills() {
        // SETUP WINDMILLS USING MAP DATA
        int[] windmill_indices = new int[]{0, 1, 2};
        // create TileInfo for 'enemy_tiles' TileSet
        final int ENEMY_TILES_TILESET_IDX = 0;
        TileSet enemy_tiles = map.getTileSet(ENEMY_TILES_TILESET_IDX);
        if (!enemy_tiles.name.equals("enemy_tiles"))
            throw new IllegalAccessError("Wrong tileset index: [" + ENEMY_TILES_TILESET_IDX + "] is not enemy_tiles");
        else {
            for (int idx = 0; idx < windmill_indices.length; ++idx) {
                windmill_indices[idx] += enemy_tiles.firstGID;
            }
        }

        final int ENEMY_TILES_LAYER_IDX = 3;
        for (int x = 0; x < map.getWidth(); ++x) {
            for (int y = 0; y < map.getHeight(); ++y) {
                for (int idx = 0; idx < windmill_indices.length; ++idx) {
                    //int tileID = map.getTileId(x, y, ENEMY_TILES_LAYER_IDX);
                    if (map.getTileId(x, y, ENEMY_TILES_LAYER_IDX) == windmill_indices[idx]) {
                        Windmill windmill = null;
                        int windmill_key = x > y ? -x * y : x * y;
                        switch (idx) {
                            case 0: // green windmill
                                windmill = new WindmillGreen(new Vector2f(x * 40, y * 40), true, windmill_key);
                                break;
                            case 1: // grey windmill
                                windmill = new WindmillGrey(new Vector2f(x * 40, y * 40), true, windmill_key);
                                break;
                            case 2: // yellow windmill
                                windmill = new WindmillYellow(new Vector2f(x * 40, y * 40), true, windmill_key);
                                break;
                        }
                        enemy_windmills.add(windmill);
                    }
                }
            }
        }
    }

    @Override
    public void update(GameContainer gameContainer, int deltaTime) {
        player.update(gameContainer, deltaTime);
        for (MovableWarAttender warAttender : friendly_war_attenders) {
            warAttender.update(gameContainer, deltaTime);
        }
        for (MovableWarAttender warAttender : hostile_war_attenders) {
            warAttender.update(gameContainer, deltaTime);
        }
        for (WarAttender warAttender : enemy_windmills) {
            warAttender.update(gameContainer, deltaTime);
        }
        for (InteractionCircle interaction_circle : interaction_circles) {
            interaction_circle.update(deltaTime);
        }
        keyInputHandler.update(gameContainer, deltaTime);
        collisionHandler.update(gameContainer, deltaTime);
        hud.update(deltaTime);
        camera.centerOn(player.getWarAttender().position.x, player.getWarAttender().position.y);
    }

    @Override
    public void render(GameContainer gameContainer, Graphics graphics) {
        camera.drawMap();
        camera.translateGraphics();
        for (InteractionCircle interaction_circle : interaction_circles) {
            interaction_circle.draw();
        }
        player.draw(graphics);
        for (MovableWarAttender warAttender : friendly_war_attenders) {
            warAttender.draw(graphics);
        }
        for (MovableWarAttender warAttender : hostile_war_attenders) {
            warAttender.draw(graphics);
        }
        for (WarAttender warAttender : enemy_windmills) {
            warAttender.draw(graphics);
        }

        // un-translate graphics to draw the HUD- items
        camera.untranslateGraphics();
        hud.draw();

    }

    @Override
    public void keyReleased(int key, char c) {
        keyInputHandler.onKeyRelease(key);
    }
}
