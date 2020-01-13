package levels;

import logic.*;
import logic.level_listeners.GroundTileDamageListener;
import logic.level_listeners.WarAttenderDeleteListener;
import main.ZuluAssault;
import models.StaticWarAttender;
import models.animations.explosion.BigExplosionAnimation;
import models.hud.HUD;
import models.interaction_circles.InteractionCircle;
import models.items.Item;
import models.war_attenders.MovableWarAttender;
import models.war_attenders.WarAttender;
import models.war_attenders.planes.StaticEnemyPlane;
import models.war_attenders.soldiers.Soldier;
import models.war_attenders.windmills.Windmill;
import models.war_attenders.windmills.WindmillGreen;
import models.war_attenders.windmills.WindmillGrey;
import models.war_attenders.windmills.WindmillYellow;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;
import player.Player;
import screen_drawer.ScreenDrawer;

import java.util.ArrayList;
import java.util.List;

import static logic.TileMapInfo.*;

public abstract class AbstractLevel extends BasicGameState implements WarAttenderDeleteListener, GroundTileDamageListener {

    private static boolean firstCall;

    public Player player;
    public TiledMap map;
    public List<StaticWarAttender> static_enemies;
    public List<InteractionCircle> interaction_circles;
    public List<Item> items;
    public List<MovableWarAttender> friendly_war_attenders, hostile_war_attenders, drivable_war_attenders;

    private static KeyInputHandler keyInputHandler;
    private static CollisionHandler collisionHandler;
    private Camera camera;

    private HUD hud;

    // for destruction of tanks or robots
    private static BigExplosionAnimation bigExplosionAnimation;

    ScreenDrawer screenDrawer;

    public AbstractLevel() {
        hostile_war_attenders = new ArrayList<>();
        friendly_war_attenders = new ArrayList<>();
        drivable_war_attenders = new ArrayList<>();
        static_enemies = new ArrayList<>();
        interaction_circles = new ArrayList<>();
        items = new ArrayList<>();
        player = new Player();
    }

    static {
        firstCall = true;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        // reset the level info
        if (firstCall) {
            // this gets only executed once
            firstCall = false;
            TileMapInfo.init(map);

            collisionHandler = new CollisionHandler();
            keyInputHandler = new KeyInputHandler();
            bigExplosionAnimation = new BigExplosionAnimation(100);
        }

        createWarAttendersFromTiles();
        hud = new HUD(player, gameContainer);
        player.addListener(hud);

        camera = new Camera(gameContainer, map);

        // handle collisions
        //collisionHandler = new CollisionHandler();

        // add listeners for destruction of warAttenders
        for (MovableWarAttender warAttender : friendly_war_attenders) {
            warAttender.addListeners(this);
        }
        for (MovableWarAttender warAttender : hostile_war_attenders) {
            warAttender.addListeners(this);
        }
        player.getWarAttender().addListeners(this);
        //collisionHandler.addListener(this);
        screenDrawer = new ScreenDrawer();
    }

    /*
    this method creates additional java-objects from special tiles that act as warAttenders
     */
    private void createWarAttendersFromTiles() {
        // SETUP WINDMILLS USING MAP DATA
        for (int x = 0; x < map.getWidth(); ++x) {
            for (int y = 0; y < map.getHeight(); ++y) {
                for (int idx = 0; idx < windmill_indices.length; ++idx) {
                    //int tileID = map.getTileId(x, y, ENEMY_TILES_LAYER_IDX);
                    if (map.getTileId(x, y, ENEMY_TILES_LAYER_IDX) == windmill_indices[idx]) {
                        Windmill windmill = null;
                        Vector2f[] collision_tiles = new Vector2f[1];
                        // add all tile positions of this warAttender to its object
                        collision_tiles[0] = new Vector2f(x, y);
                        // position is at middle of the tile
                        Vector2f pos_windmill = new Vector2f(
                                x * TILE_WIDTH + TILE_WIDTH / 2.f,
                                y * TILE_HEIGHT + TILE_HEIGHT / 2.f
                        );
                        switch (idx) {
                            case 0: // green windmill
                                windmill = new WindmillGreen(pos_windmill, true, collision_tiles);
                                break;
                            case 1: // grey windmill
                                windmill = new WindmillGrey(pos_windmill, true, collision_tiles);
                                break;
                            case 2: // yellow windmill
                                windmill = new WindmillYellow(pos_windmill, true, collision_tiles);
                                break;
                        }
                        static_enemies.add(windmill);
                    }
                }
            }
        }

        // SETUP PLANES USING MAP DATA
        for (int x = 0; x < map.getWidth(); ++x) {
            for (int y = 0; y < map.getHeight(); ++y) {
                for (int idx = 0; idx < static_plane_creation_indices.length; ++idx) {
                    //int tileID = map.getTileId(x, y, PLANE_TILES_TILESET_IDX);
                    if (map.getTileId(x, y, ENEMY_TILES_LAYER_IDX) == static_plane_creation_indices[idx]) {
                        Vector2f[] collision_tiles = new Vector2f[5];
                        // add all collision tiles of this warAttender to its object
                        collision_tiles[0] = new Vector2f(x, y - 1);
                        collision_tiles[1] = new Vector2f(x - 1, y);
                        collision_tiles[2] = new Vector2f(x, y);
                        collision_tiles[3] = new Vector2f(x + 1, y);
                        collision_tiles[4] = new Vector2f(x, y + 1);
                        // add more tile positions for later tile replacement on destruction
                        // these tiles do not have collisions
                        Vector2f[] replacement_tiles = new Vector2f[6];
                        switch (idx) {
                            case 0: // the plane that's facing right in the tileset
                                replacement_tiles[0] = new Vector2f(x, y - 2);
                                replacement_tiles[1] = new Vector2f(x - 2, y - 1);
                                replacement_tiles[2] = new Vector2f(x - 2, y);
                                replacement_tiles[3] = new Vector2f(x - 2, y + 1);
                                replacement_tiles[4] = new Vector2f(x + 2, y);
                                replacement_tiles[5] = new Vector2f(x, y + 2);
                                break;
                            case 1: // the plane that's facing down in the tileset
                                replacement_tiles[0] = new Vector2f(x - 1, y - 2);
                                replacement_tiles[1] = new Vector2f(x, y - 2);
                                replacement_tiles[2] = new Vector2f(x + 1, y - 2);
                                replacement_tiles[3] = new Vector2f(x - 2, y);
                                replacement_tiles[4] = new Vector2f(x + 2, y);
                                replacement_tiles[5] = new Vector2f(x, y + 2);
                                break;
                            case 2: // the plane that's facing left in the tileset
                                replacement_tiles[0] = new Vector2f(x, y - 2);
                                replacement_tiles[1] = new Vector2f(x - 2, y);
                                replacement_tiles[2] = new Vector2f(x + 2, y - 1);
                                replacement_tiles[3] = new Vector2f(x + 2, y);
                                replacement_tiles[4] = new Vector2f(x + 2, y + 1);
                                replacement_tiles[5] = new Vector2f(x, y + 2);
                                break;
                            case 3:  // the plane that's facing up in the tileset
                                replacement_tiles[0] = new Vector2f(x, y - 2);
                                replacement_tiles[1] = new Vector2f(x - 2, y);
                                replacement_tiles[2] = new Vector2f(x + 2, y);
                                replacement_tiles[3] = new Vector2f(x - 1, y + 2);
                                replacement_tiles[4] = new Vector2f(x, y + 2);
                                replacement_tiles[5] = new Vector2f(x + 1, y + 2);
                                break;
                        }
                        // position is at middle of the tile
                        Vector2f pos_staticEnemyPlane = new Vector2f(
                                x * TILE_WIDTH + TILE_WIDTH / 2.f,
                                y * TILE_HEIGHT + TILE_HEIGHT / 2.f
                        );
                        StaticEnemyPlane staticEnemyPlane = new StaticEnemyPlane(pos_staticEnemyPlane, true,
                                collision_tiles, replacement_tiles);
                        static_enemies.add(staticEnemyPlane);
                    }
                }
            }
        }

    }

    @Override
    public void enter(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        collisionHandler.setLevel(this);
        collisionHandler.addListener(this);
        keyInputHandler.setLevel(this);

        // hide the mouse cursor
        gameContainer.setMouseGrabbed(true);
    }

    @Override
    public void leave(GameContainer var1, StateBasedGame var2) {
        TileMapInfo.reset();
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int deltaTime) {
        player.update(gameContainer, deltaTime);
        for (int idx = 0; idx < friendly_war_attenders.size(); ++idx) {
            friendly_war_attenders.get(idx).update(gameContainer, deltaTime);
        }
        for (int idx = 0; idx < hostile_war_attenders.size(); ++idx) {
            hostile_war_attenders.get(idx).update(gameContainer, deltaTime);
        }
        for (int idx = 0; idx < static_enemies.size(); ++idx) {
            static_enemies.get(idx).update(gameContainer, deltaTime);
        }
        for (int idx = 0; idx < drivable_war_attenders.size(); ++idx) {
            drivable_war_attenders.get(idx).update(gameContainer, deltaTime);
        }
        for (InteractionCircle interaction_circle : interaction_circles) {
            interaction_circle.update(deltaTime);
        }
        for (Item item : items) {
            item.update(deltaTime);
        }
        keyInputHandler.update(gameContainer, deltaTime);
        collisionHandler.update(gameContainer, deltaTime);
        hud.update(deltaTime);
        screenDrawer.update(deltaTime);
        bigExplosionAnimation.update(deltaTime);
        camera.centerOn(player.getWarAttender().position.x, player.getWarAttender().position.y);

        if (gameContainer.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
            stateBasedGame.enterState(ZuluAssault.MAIN_MENU);
        }

    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) {
        camera.drawMap();
        camera.translateGraphics();
        for (InteractionCircle interaction_circle : interaction_circles) {
            interaction_circle.draw();
        }
        for (Item item : items) {
            item.draw();
        }
        player.draw(graphics);
        for (int idx = 0; idx < friendly_war_attenders.size(); ++idx) {
            friendly_war_attenders.get(idx).draw(graphics);
        }
        for (int idx = 0; idx < hostile_war_attenders.size(); ++idx) {
            hostile_war_attenders.get(idx).draw(graphics);
        }
        for (int idx = 0; idx < static_enemies.size(); ++idx) {
            static_enemies.get(idx).draw(graphics);
        }
        for (int idx = 0; idx < drivable_war_attenders.size(); ++idx) {
            drivable_war_attenders.get(idx).draw(graphics);
        }
        collisionHandler.draw();
        screenDrawer.draw();
        bigExplosionAnimation.draw();
        // un-translate graphics to draw the HUD- items
        camera.untranslateGraphics();
        hud.draw();


    }

    @Override
    public void notifyForWarAttenderDeletion(WarAttender warAttender) {
        // TODO: add points according to the class of the enemy/warAttender

        if (warAttender.isHostile) {
            player.addPoints(warAttender.getScoreValue());  // add points
            screenDrawer.drawScoreValue(5, warAttender);    // draw the score on the screen

            //hostile_war_attenders.removeIf(enemy -> enemy.isDestroyed);
            if (warAttender instanceof Windmill) {
                bigExplosionAnimation.playTenTimes(warAttender.position.x + 20, warAttender.position.y + 20, 0);
            } else {
                hostile_war_attenders.remove(warAttender);
                if (warAttender instanceof Soldier) screenDrawer.drawDeadSoldierBody(3, warAttender);
                else bigExplosionAnimation.playTenTimes(warAttender.position.x, warAttender.position.y, 0);
            }
        } else {
            if (warAttender instanceof Soldier) screenDrawer.drawDeadSoldierBody(3, warAttender);
            else bigExplosionAnimation.playTenTimes(warAttender.position.x, warAttender.position.y, 0);
            friendly_war_attenders.remove(warAttender);
            //friendly_war_attenders.removeIf(friend -> friend.isDestroyed);
        }
    }


    @Override
    public void notifyForDestructibleTileDeletion(float xPos, float yPos) {
        // a destructible tile was deleted
        bigExplosionAnimation.playTenTimes(xPos, yPos, 0);
    }

    @Override
    public void notifyForGroundTileDamage(float xPos, float yPos) {
        // a ground tile was damaged by an iGroundTileDamageWeapon
        int mapX = (int) (xPos / TILE_WIDTH);
        int mapY = (int) (yPos / TILE_HEIGHT);
        int tileID = map.getTileId(mapX, mapY, LANDSCAPE_TILES_LAYER_IDX);
        int replacement_tile_id = TileMapInfo.getReplacementTileID(tileID);
        map.setTileId(mapX, mapY, DESTRUCTION_TILES_LAYER_IDX, replacement_tile_id);
    }

    @Override
    public void keyReleased(int key, char c) {
        keyInputHandler.onKeyRelease(key);
    }

}
