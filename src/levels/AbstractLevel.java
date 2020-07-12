package levels;

import logic.*;
import logic.level_listeners.GroundTileDamageListener;
import logic.level_listeners.WarAttenderDeleteListener;
import main.ZuluAssault;
import menus.MainMenu;
import menus.UserSettings;
import models.StaticWarAttender;
import models.animations.explosion.BigExplosionAnimation;
import models.hud.HUD;
import models.hud.Radar;
import models.interaction_circles.InteractionCircle;
import models.items.Item;
import models.war_attenders.MovableWarAttender;
import models.war_attenders.WarAttender;
import models.war_attenders.planes.Plane;
import models.war_attenders.planes.StaticEnemyPlane;
import models.war_attenders.robots.Robot;
import models.war_attenders.soldiers.Soldier;
import models.war_attenders.windmills.Windmill;
import models.war_attenders.windmills.WindmillGreen;
import models.war_attenders.windmills.WindmillGrey;
import models.war_attenders.windmills.WindmillYellow;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.tiled.TiledMap;
import player.Player;
import screen_drawer.ScreenDrawer;

import java.util.ArrayList;
import java.util.List;

import static logic.TileMapInfo.*;

public abstract class AbstractLevel extends BasicGameState implements WarAttenderDeleteListener, GroundTileDamageListener {

    private StateBasedGame stateBasedGame;
    private GameContainer gameContainer;

    private static RandomItemDropper randomItemDropper;

    private static boolean has_initialized_once;
    protected boolean calledOnce;

    protected static Sound level_intro_sound;
    protected static Music level_music;

    protected String mission_title, briefing_message, debriefing_message;

    // keep track of the time that the player has been in the level
    private static long level_starting_time, level_begin_pause_time;

    public static Player player;
    public TiledMap map;
    public static List<StaticWarAttender> static_enemies;
    public static List<InteractionCircle> interaction_circles;
    public static List<Item> items;
    public static List<MovableWarAttender> friendly_movable_war_attenders, hostile_movable_war_attenders, drivable_war_attenders,
            all_movable_war_attenders;
    public static List<WarAttender> all_hostile_war_attenders;

    // list for rendering -> respects the render hierarchy
    private static List<MovableWarAttender> renderList;

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
        hostile_movable_war_attenders = new ArrayList<>();
        friendly_movable_war_attenders = new ArrayList<>();
        drivable_war_attenders = new ArrayList<>();
        all_movable_war_attenders = new ArrayList<>();
        all_hostile_war_attenders = new ArrayList<>();
        renderList = new ArrayList<>();
        static_enemies = new ArrayList<>();
        interaction_circles = new ArrayList<>();
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
        // reset the level info
        if (!has_initialized_once) {
            // this gets only executed once
            has_initialized_once = true;
            TileMapInfo.init();

            randomItemDropper = new RandomItemDropper();
            collisionHandler = new CollisionHandler();
            keyInputHandler = new KeyInputHandler();
            bigExplosionAnimation = new BigExplosionAnimation(100);
            screenDrawer = new ScreenDrawer();
            hud = new HUD(player, gameContainer);
            player.addListener(hud);
            radar = new Radar(gameContainer, player);
        }
        this.stateBasedGame = stateBasedGame;
        this.gameContainer = gameContainer;
        level_starting_time = System.currentTimeMillis();
        level_begin_pause_time = 0;

        // create a global movableWarAttender list for collisions between them
        all_movable_war_attenders.addAll(friendly_movable_war_attenders);
        all_movable_war_attenders.addAll(hostile_movable_war_attenders);
        renderList.addAll(all_movable_war_attenders);
        all_movable_war_attenders.addAll(drivable_war_attenders);

        // put planes at the end of the list so they get rendered LAST
        renderList.sort((o1, o2) -> {
            if (o1 instanceof Plane && o2 instanceof Plane) return 0;
            else if (o1 instanceof Plane) return 1;
            else if (o2 instanceof Plane) return -1;
            else return 0;
        });

        createStaticWarAttendersFromTiles();

        all_hostile_war_attenders.addAll(hostile_movable_war_attenders);
        all_hostile_war_attenders.addAll(static_enemies);

        camera = new Camera(gameContainer, map);
        camera.centerOn(player.getWarAttender().getPosition().x, player.getWarAttender().getPosition().y);

        // add listeners for destruction of warAttenders
        for (MovableWarAttender warAttender : friendly_movable_war_attenders) {
            warAttender.addListeners(this);
        }
        for (MovableWarAttender warAttender : hostile_movable_war_attenders) {
            warAttender.addListeners(this);
        }
        for (MovableWarAttender warAttender : drivable_war_attenders) {
            warAttender.addListeners(this);
        }
        player.getWarAttender().addListeners(this);
        player.getBaseSoldier().addListeners(this);
    }

    /*
    this method creates additional java-objects from special tiles that act as warAttenders
     */
    private void createStaticWarAttendersFromTiles() {
        // SETUP WINDMILLS USING MAP DATA
        for (int x = 0; x < map.getWidth(); ++x) {
            for (int y = 0; y < map.getHeight(); ++y) {
                for (int idx = 0; idx < windmill_indices.length; ++idx) {
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
                        static_enemies.add(windmill);
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
                        // PLANES ARE ALWAYS MANDATORY (FOR NOW!)
                        StaticEnemyPlane staticEnemyPlane = new StaticEnemyPlane(pos_staticEnemyPlane, true,
                                collision_tiles, replacement_tiles);
                        staticEnemyPlane.setAsMandatory();
                        static_enemies.add(staticEnemyPlane);
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
        level_intro_sound.play(1.f, UserSettings.MUSIC_VOLUME);

        if (ZuluAssault.prevState.getID() == ZuluAssault.MAIN_MENU && level_begin_pause_time != 0) {
            // player is resuming the level

            // remove the time the player has lost while in main menu:
            level_starting_time += System.currentTimeMillis() - level_begin_pause_time;
        }
    }

    @Override
    public void leave(GameContainer var1, StateBasedGame var2) {
        TileMapInfo.reset();
        level_intro_sound.stop();
        level_music.stop();
        ZuluAssault.prevState = this;
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int deltaTime) {
        if (hasWonTheLevel) {
            // enter debriefing
            stateBasedGame.enterState(ZuluAssault.DEBRIEFING,
                    new FadeOutTransition(), new FadeInTransition());
            return;
        }
        player.update(gameContainer, deltaTime);
        for (int idx = 0; idx < all_movable_war_attenders.size(); ++idx) {
            all_movable_war_attenders.get(idx).update(gameContainer, deltaTime);
        }

        for (int idx = 0; idx < static_enemies.size(); ++idx) {
            static_enemies.get(idx).update(gameContainer, deltaTime);
        }

        for (InteractionCircle interaction_circle : interaction_circles) {
            interaction_circle.update(deltaTime);
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
        camera.centerOn(player.getWarAttender().getPosition().x, player.getWarAttender().getPosition().y);
        camera.update(deltaTime);

        if (gameContainer.getInput().isKeyPressed(Input.KEY_ESCAPE)) {  // paused the game
            level_begin_pause_time = System.currentTimeMillis();
            MainMenu.goToMenu(MainMenu.STATE_IN_GAME_MENU, gameContainer);
            stateBasedGame.enterState(ZuluAssault.MAIN_MENU,
                    new FadeOutTransition(), new FadeInTransition());
            ZuluAssault.prevState = this;
        }

        if (!level_intro_sound.playing()) {
            if (!level_music.playing()) {
                level_music.play();
                level_music.loop();
                level_music.setVolume(UserSettings.MUSIC_VOLUME);
            }
        }
        checkWon();
    }

    private void checkWon() {
        int mandatoryCounter = 0;
        for (MovableWarAttender movable_war_attender : all_movable_war_attenders) {
            if (movable_war_attender.isMandatory()) mandatoryCounter++;
        }
        for (StaticWarAttender static_enemy : static_enemies) {
            if (static_enemy.isMandatory()) mandatoryCounter++;
        }
        if (mandatoryCounter == 0) hasWonTheLevel = true;
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

        screenDrawer.draw();    // dead bodies and score values

        /* --------------------- draw all warAttenders --------------------- */

        for (MovableWarAttender drivableWarAttender : drivable_war_attenders) {
            drivableWarAttender.draw(graphics);
        }

        if (!(player.getWarAttender() instanceof Plane)) player.getWarAttender().draw(graphics);

        for (StaticWarAttender staticEnemy : static_enemies) {
            staticEnemy.draw(graphics);
        }

        for (MovableWarAttender renderInstance : renderList) {
            renderInstance.draw(graphics);
        }

        bigExplosionAnimation.draw();
        collisionHandler.draw();

        if (player.getWarAttender() instanceof Plane) player.getWarAttender().draw(graphics);

        // un-translate graphics to draw the HUD-items
        camera.untranslateGraphics();
        hud.draw();
        radar.draw(graphics);
    }

    @Override
    public void notifyForWarAttenderDeletion(WarAttender warAttender) {
        if (warAttender.isHostile) {
            all_hostile_war_attenders.remove(warAttender);
            if (warAttender instanceof MovableWarAttender) {
                hostile_movable_war_attenders.remove(warAttender);
                if (warAttender instanceof Robot) camera.shake();
            } else if (warAttender instanceof StaticWarAttender) {
                static_enemies.remove(warAttender);
            }
            player.addPoints(warAttender.getScoreValue());  // add points
            screenDrawer.drawScoreValue(5, warAttender);    // draw the score on the screen

            if (warAttender instanceof Windmill) {
                bigExplosionAnimation.playTenTimes(warAttender.getPosition().x + 20,
                        warAttender.getPosition().y + 20, 0);
                explosion_sound.play(1.f, UserSettings.SOUND_VOLUME);
            } else {
                if (warAttender instanceof Soldier) screenDrawer.drawDeadSoldierBody(10, warAttender);
                else {
                    bigExplosionAnimation.playTenTimes(warAttender.getPosition().x, warAttender.getPosition().y, 0);
                    explosion_sound.play(1.f, UserSettings.SOUND_VOLUME);
                }
            }
        } else {
            if (warAttender instanceof MovableWarAttender) {
                if (warAttender instanceof Robot) camera.shake();
                if (warAttender == player.getWarAttender()) {
                    // THE PLAYER DIED
                    MainMenu.goToMenu(MainMenu.STATE_DEATH_MENU, gameContainer);
                    stateBasedGame.enterState(ZuluAssault.MAIN_MENU,
                            new FadeOutTransition(), new FadeInTransition()
                    );
                    ZuluAssault.prevState = this;
                } else {
                    friendly_movable_war_attenders.remove(warAttender);
                    if (((MovableWarAttender) warAttender).isDrivable())
                        drivable_war_attenders.remove(warAttender);
                }
            }
            if (warAttender instanceof Soldier) screenDrawer.drawDeadSoldierBody(10, warAttender);
            else {
                bigExplosionAnimation.playTenTimes(warAttender.getPosition().x, warAttender.getPosition().y, 0);
                explosion_sound.play(1.f, UserSettings.SOUND_VOLUME);
            }
        }
        // maybe drop an item
        Item drop_item = randomItemDropper.dropItem(warAttender.getPosition());
        if (drop_item != null) {
            items.add(drop_item);
        }
        // remove warAttender from relevant lists
        if (warAttender instanceof MovableWarAttender) {
            renderList.remove(warAttender);
            all_movable_war_attenders.remove(warAttender);
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
        if (player.getWarAttender() == null) return;
        hostile_movable_war_attenders.clear();
        friendly_movable_war_attenders.clear();
        drivable_war_attenders.clear();
        all_movable_war_attenders.clear();
        all_hostile_war_attenders.clear();
        renderList.clear();
        static_enemies.clear();
        interaction_circles.clear();
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

    public static long getTimeInLevel() {
        return System.currentTimeMillis() - level_starting_time;
    }

    public abstract void loadLevelMusic();  // load level music in separate thread since it takes long
}
