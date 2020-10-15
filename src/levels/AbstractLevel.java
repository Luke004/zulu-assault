package levels;

import audio.CombatBackgroundMusic;
import logic.*;
import logic.level_listeners.GroundTileDamageListener;
import logic.level_listeners.EntityDeleteListener;
import main.ZuluAssault;
import menus.MainMenu;
import settings.UserSettings;
import models.StaticEntity;
import graphics.animations.explosion.BigExplosionAnimation;
import graphics.hud.HUD;
import graphics.hud.Radar;
import models.interaction_circles.HealthCircle;
import models.interaction_circles.InteractionCircle;
import models.interaction_circles.TeleportCircle;
import models.items.Item;
import models.entities.MovableEntity;
import models.entities.Entity;
import models.entities.aircraft.friendly.Plane;
import models.entities.static_multitile_constructions.StaticEnemyPlane;
import models.entities.robots.Robot;
import models.entities.soldiers.Soldier;
import models.entities.windmills.Windmill;
import models.entities.windmills.WindmillGreen;
import models.entities.windmills.WindmillGrey;
import models.entities.windmills.WindmillYellow;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.tiled.TiledMap;
import player.Player;
import graphics.screen_drawer.ScreenDrawer;

import java.util.ArrayList;
import java.util.List;

import static logic.TileMapInfo.*;

public abstract class AbstractLevel extends BasicGameState implements EntityDeleteListener, GroundTileDamageListener {

    private GameContainer gameContainer;

    private static RandomItemDropper randomItemDropper;

    private static boolean has_initialized_once;
    protected boolean calledOnce;

    protected static CombatBackgroundMusic combatBackgroundMusic;

    protected String mission_title, briefing_message, debriefing_message;

    public static Player player;
    public TiledMap map;
    public static List<StaticEntity> static_enemy_entities;
    public static List<TeleportCircle> teleport_circles;
    public static List<HealthCircle> health_circles;
    public static List<Item> items;
    public static List<MovableEntity> friendly_movable_entities, hostile_movable_entities, drivable_entities,
            all_movable_entities;
    public static List<Entity> all_hostile_entities;

    // list for rendering -> respects the render hierarchy
    private static List<MovableEntity> renderList;

    private static KeyInputHandler keyInputHandler;
    private static CollisionHandler collisionHandler;
    private Camera camera;

    private static HUD hud;
    private static Radar radar;

    // for destruction of tanks or robots
    private static BigExplosionAnimation bigExplosionAnimation;
    protected static Sound explosion_sound;

    private static ScreenDrawer screenDrawer;

    private static boolean hasWonTheLevel;

    static {
        has_initialized_once = false;
        player = new Player();
        combatBackgroundMusic = new CombatBackgroundMusic();
        hostile_movable_entities = new ArrayList<>();
        friendly_movable_entities = new ArrayList<>();
        drivable_entities = new ArrayList<>();
        all_movable_entities = new ArrayList<>();
        all_hostile_entities = new ArrayList<>();
        renderList = new ArrayList<>();
        static_enemy_entities = new ArrayList<>();
        health_circles = new ArrayList<>();
        teleport_circles = new ArrayList<>();
        items = new ArrayList<>();
        try {
            explosion_sound = new Sound("audio/sounds/explosion.ogg");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        // set the current tilemap based on the level
        TileMapInfo.setMap(map);
        // set the combat music based on the level
        combatBackgroundMusic.setIdx(getCombatMusicIdx());
        // reset the level info
        if (!has_initialized_once) {
            // this gets only executed once
            has_initialized_once = true;
            TileMapInfo.init();
            LevelHandler.init(stateBasedGame);

            randomItemDropper = new RandomItemDropper();
            collisionHandler = new CollisionHandler();
            keyInputHandler = new KeyInputHandler();
            bigExplosionAnimation = new BigExplosionAnimation(100);
            screenDrawer = new ScreenDrawer();
            hud = new HUD(player, gameContainer);
            player.addListener(hud);
            radar = new Radar(gameContainer, player);
        }
        this.gameContainer = gameContainer;

        // check if the map size has changed - if so, inform the radar about it
        boolean map_size_changed = TileMapInfo.updateMapSize();
        if (map_size_changed) radar.updateMapSize();

        // init time manager for each level
        TimeManager.init();

        // create a global movable entity list for collisions between them
        all_movable_entities.addAll(friendly_movable_entities);
        all_movable_entities.addAll(hostile_movable_entities);
        renderList.addAll(all_movable_entities);
        all_movable_entities.addAll(drivable_entities);

        // put planes at the end of the list so they get rendered LAST
        renderList.sort((o1, o2) -> {
            if (o1 instanceof Plane && o2 instanceof Plane) return 0;
            else if (o1 instanceof Plane) return 1;
            else if (o2 instanceof Plane) return -1;
            else return 0;
        });

        createStaticEntitiesFromTiles();

        all_hostile_entities.addAll(hostile_movable_entities);
        all_hostile_entities.addAll(static_enemy_entities);

        camera = new Camera(gameContainer, map);
        camera.centerOn(player.getEntity().getPosition().x, player.getEntity().getPosition().y);

        // add listeners for destruction of entities
        for (MovableEntity movableEntity : friendly_movable_entities) {
            movableEntity.addListeners(this);
        }
        for (MovableEntity movableEntity : hostile_movable_entities) {
            movableEntity.addListeners(this);
        }
        for (MovableEntity movableEntity : drivable_entities) {
            movableEntity.addListeners(this);
        }
        player.getEntity().addListeners(this);
        player.getBaseSoldier().addListeners(this);
    }

    /*
    this method creates additional java-objects from special tiles that act as entities (can shoot, have health etc.)
     */
    private void createStaticEntitiesFromTiles() {
        // SETUP WINDMILLS USING MAP DATA
        for (int x = 0; x < map.getWidth(); ++x) {
            for (int y = 0; y < map.getHeight(); ++y) {
                for (int idx = 0; idx < windmill_indices.length; ++idx) {
                    if (map.getTileId(x, y, ENEMY_TILES_LAYER_IDX) == windmill_indices[idx]) {
                        Windmill windmill = null;
                        Vector2f[] collision_tiles = new Vector2f[1];
                        // add all tile positions of this entity to its object
                        collision_tiles[0] = new Vector2f(x, y);
                        // position is at middle of the tile
                        Vector2f pos_windmill = new Vector2f(
                                x * TILE_WIDTH + TILE_WIDTH / 2.f,
                                y * TILE_HEIGHT + TILE_HEIGHT / 2.f
                        );
                        boolean setAsMandatory = false;
                        switch (idx) {
                            case 3: // mandatory green windmill
                                setAsMandatory = true;
                                // no break
                            case 0: // green windmill
                                windmill = new WindmillGreen(pos_windmill, true, collision_tiles);
                                break;
                            case 4: // mandatory grey windmill
                                setAsMandatory = true;
                                // no break
                            case 1: // grey windmill
                                windmill = new WindmillGrey(pos_windmill, true, collision_tiles);
                                break;
                            case 5: // mandatory yellow windmill
                                setAsMandatory = true;
                                // no break
                            case 2: // yellow windmill
                                windmill = new WindmillYellow(pos_windmill, true, collision_tiles);
                                break;
                        }
                        if (setAsMandatory) windmill.setAsMandatory();
                        static_enemy_entities.add(windmill);
                    }
                }
            }
        }

        // SETUP PLANES USING MAP DATA
        for (int x = 0; x < map.getWidth(); ++x) {
            for (int y = 0; y < map.getHeight(); ++y) {
                for (int idx = 0; idx < static_plane_creation_indices.length; ++idx) {
                    if (map.getTileId(x, y, ENEMY_TILES_LAYER_IDX) == static_plane_creation_indices[idx]) {
                        Vector2f[] collision_tiles = new Vector2f[5];
                        // add all collision tiles of this entity to its object
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
                        // PLANES ARE ALWAYS MANDATORY (FOR NOW!)
                        StaticEnemyPlane staticEnemyPlane = new StaticEnemyPlane(pos_staticEnemyPlane, true,
                                collision_tiles, replacement_tiles);
                        staticEnemyPlane.setAsMandatory();
                        static_enemy_entities.add(staticEnemyPlane);
                    }
                }
            }
        }

    }

    @Override
    public void enter(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        collisionHandler.setPlayer(player);
        collisionHandler.addListener(this);
        keyInputHandler.setPlayer(player);
        combatBackgroundMusic.start();

        /*
        if (ZuluAssault.prevState.getID() == ZuluAssault.MAIN_MENU) {
            // player is resuming the level
        }
         */
    }

    @Override
    public void leave(GameContainer var1, StateBasedGame var2) {
        TileMapInfo.reset();
        combatBackgroundMusic.stop();
        ZuluAssault.prevState = this;
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int deltaTime) {
        if (hasWonTheLevel) {
            // notify TimeManager that the level is finished
            TimeManager.finishLevel();
            // enter debriefing
            stateBasedGame.enterState(ZuluAssault.DEBRIEFING,
                    new FadeOutTransition(), new FadeInTransition());
            return;
        }
        player.update(gameContainer, deltaTime);
        for (int idx = 0; idx < all_movable_entities.size(); ++idx) {
            all_movable_entities.get(idx).update(gameContainer, deltaTime);
        }

        for (int idx = 0; idx < static_enemy_entities.size(); ++idx) {
            static_enemy_entities.get(idx).update(gameContainer, deltaTime);
        }

        for (InteractionCircle health_circle : health_circles) {
            health_circle.update(deltaTime);
        }
        for (InteractionCircle teleport_circle : teleport_circles) {
            teleport_circle.update(deltaTime);
        }
        for (Item item : items) {
            item.update(deltaTime);
        }
        keyInputHandler.update(gameContainer, deltaTime);
        collisionHandler.update(deltaTime);
        hud.update(deltaTime);
        radar.update(deltaTime);
        screenDrawer.update(deltaTime);
        bigExplosionAnimation.update(deltaTime);
        camera.centerOn(player.getEntity().getPosition().x, player.getEntity().getPosition().y);
        camera.update(deltaTime);
        TimeManager.update(deltaTime);

        if (gameContainer.getInput().isKeyPressed(Input.KEY_ESCAPE)) {  // paused the game
            MainMenu.goToMenu(MainMenu.STATE_IN_GAME_MENU);
            stateBasedGame.enterState(ZuluAssault.MAIN_MENU,
                    new FadeOutTransition(), new FadeInTransition());
            ZuluAssault.prevState = this;
        }

        combatBackgroundMusic.update();
        checkWon();
    }

    private void checkWon() {
        int mandatoryCounter = 0;
        for (MovableEntity movable_war_attender : all_movable_entities) {
            if (movable_war_attender.isMandatory()) mandatoryCounter++;
        }
        for (StaticEntity static_enemy : static_enemy_entities) {
            if (static_enemy.isMandatory()) mandatoryCounter++;
        }
        if (mandatoryCounter == 0) hasWonTheLevel = true;
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) {
        camera.drawMap();
        camera.translateGraphics();
        for (InteractionCircle health_circle : health_circles) {
            health_circle.draw();
        }
        for (InteractionCircle teleport_circle : teleport_circles) {
            teleport_circle.draw();
        }
        for (Item item : items) {
            item.draw();
        }

        screenDrawer.draw();    // draws dead bodies and score values

        /* --------------------- draw all entities --------------------- */

        for (MovableEntity drivableEntity : drivable_entities) {
            drivableEntity.draw(graphics);
        }

        if (!(player.getEntity() instanceof Plane)) player.getEntity().draw(graphics);

        for (StaticEntity staticEnemy : static_enemy_entities) {
            staticEnemy.draw(graphics);
        }

        for (MovableEntity renderInstance : renderList) {
            renderInstance.draw(graphics);
        }

        bigExplosionAnimation.draw();
        collisionHandler.draw();

        if (player.getEntity() instanceof Plane) player.getEntity().draw(graphics);

        // un-translate graphics to draw the HUD-items
        camera.untranslateGraphics();
        hud.draw(gameContainer);
        radar.draw(graphics);
    }

    @Override
    public void notifyForEntityDestruction(Entity entity) {
        if (entity.isHostile) {
            all_hostile_entities.remove(entity);
            if (entity instanceof MovableEntity) {
                hostile_movable_entities.remove(entity);
                if (entity instanceof Robot) camera.shake();
            } else if (entity instanceof StaticEntity) {
                static_enemy_entities.remove(entity);
            }
            player.addPoints(entity.getScoreValue());  // add points
            screenDrawer.drawScoreValue(5, entity);    // draw the score on the screen

            if (entity instanceof Windmill) {
                bigExplosionAnimation.playTenTimes(entity.getPosition().x + 20,
                        entity.getPosition().y + 20, 0);
                explosion_sound.play(1.f, UserSettings.soundVolume);
            } else {
                if (entity instanceof Soldier) screenDrawer.drawDeadSoldierBody(10, entity);
                else {
                    bigExplosionAnimation.playTenTimes(entity.getPosition().x, entity.getPosition().y, 0);
                    explosion_sound.play(1.f, UserSettings.soundVolume);
                }
            }
        } else {
            if (entity instanceof MovableEntity) {
                if (entity instanceof Robot) camera.shake();
                if (entity == player.getEntity()) {
                    // THE PLAYER DIED
                    LevelHandler.gameOver(this);
                } else {
                    friendly_movable_entities.remove(entity);
                    if (((MovableEntity) entity).isDrivable())
                        drivable_entities.remove(entity);
                }
            }
            if (entity instanceof Soldier) screenDrawer.drawDeadSoldierBody(10, entity);
            else {
                bigExplosionAnimation.playTenTimes(entity.getPosition().x, entity.getPosition().y, 0);
                explosion_sound.play(1.f, UserSettings.soundVolume);
            }
        }
        // maybe drop an item
        Item drop_item = randomItemDropper.dropItem(entity.getPosition());
        if (drop_item != null) {
            items.add(drop_item);
        }
        // remove entity from relevant lists
        if (entity instanceof MovableEntity) {
            renderList.remove(entity);
            all_movable_entities.remove(entity);
        }
    }

    @Override
    public void notifyForGroundTileDamage(float xPos, float yPos) {
        // a ground tile was damaged by an iGroundTileDamageWeapon
        int mapX = (int) (xPos / TILE_WIDTH);
        int mapY = (int) (yPos / TILE_HEIGHT);
        int tileID = map.getTileId(mapX, mapY, LANDSCAPE_TILES_LAYER_IDX);

        int replacement_tile_id = TileMapInfo.getReplacementTileID(tileID);
        if (replacement_tile_id != -1) {
            map.setTileId(mapX, mapY, DESTRUCTION_TILES_LAYER_IDX, replacement_tile_id);
        }
        doCollateralTileDamage(mapX, mapY);
    }

    @Override
    public void keyReleased(int key, char c) {
        keyInputHandler.onKeyRelease(key);
    }

    protected static void resetLevel() {
        if (player.getEntity() == null) return;
        hostile_movable_entities.clear();
        friendly_movable_entities.clear();
        drivable_entities.clear();
        all_movable_entities.clear();
        all_hostile_entities.clear();
        renderList.clear();
        static_enemy_entities.clear();
        health_circles.clear();
        teleport_circles.clear();
        items.clear();
        Radar.hideRadar();
        hasWonTheLevel = false;
    }

    public static void resetPlayerStats() {
        player.reset();
        hud.reset();
    }

    public String getBriefingMessage() {
        return briefing_message;
    }

    public String getDebriefingMessage() {
        return debriefing_message;
    }

    public String getMissionTitle() {
        return mission_title;
    }

    public abstract int getCombatMusicIdx();
}
