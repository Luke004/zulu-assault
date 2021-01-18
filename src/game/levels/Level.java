package game.levels;

import game.audio.CombatBackgroundMusic;
import game.graphics.animations.explosion.BigExplosionAnimation;
import game.graphics.hud.HUD;
import game.graphics.hud.Radar;
import game.graphics.screen_drawer.ScreenDrawer;
import game.logic.Camera;
import game.logic.CollisionHandler;
import game.logic.KeyInputHandler;
import game.logic.RandomItemDropper;
import game.logic.level_listeners.EntityDeleteListener;
import game.logic.level_listeners.GroundTileDamager;
import game.menu.Menu;
import game.models.entities.Entity;
import game.models.entities.MovableEntity;
import game.models.entities.aircraft.Aircraft;
import game.models.entities.robots.Robot;
import game.models.entities.soldiers.Soldier;
import game.models.interaction_circles.HealthCircle;
import game.models.interaction_circles.InteractionCircle;
import game.models.interaction_circles.TeleportCircle;
import game.models.items.Item;
import game.player.Player;
import game.util.TimeManager;
import level_editor.LevelEditor;
import game.util.LevelDataStorage;
import main.ZuluAssault;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.tiled.TiledMap;
import game.logic.TileMapData;
import settings.UserSettings;

import java.util.ArrayList;
import java.util.List;

import static game.util.TileMapUtil.doCollateralTileDamage;
import static game.logic.TileMapData.*;
import static game.logic.TileMapData.DESTRUCTION_TILES_LAYER_IDX;

public class Level extends BasicGameState implements EntityDeleteListener, GroundTileDamager {

    private static final String DEFAULT_MAP_FOLDER = "assets/maps/";

    private static RandomItemDropper randomItemDropper;

    protected static CombatBackgroundMusic combatBackgroundMusic;

    public static Player player;
    public TiledMap map;
    public static List<TeleportCircle> teleport_circles;
    public static List<HealthCircle> health_circles;
    public static List<Item> items;
    public static List<Entity> all_friendly_entities, all_hostile_entities,
            all_entities;
    public static List<MovableEntity> drivable_entities;

    // list for rendering -> respects the render hierarchy
    private static List<Entity> renderList;

    private static KeyInputHandler keyInputHandler;
    private static CollisionHandler collisionHandler;
    private Camera camera;

    private static HUD hud;
    private static Radar radar;

    // for destruction of tanks or robots
    private static BigExplosionAnimation bigExplosionAnimation;
    protected static Sound explosion_sound;

    private static ScreenDrawer screenDrawer;

    private boolean initOnce;

    private static boolean hasWonTheLevel;

    static {
        player = new Player();
        combatBackgroundMusic = new CombatBackgroundMusic();
        all_hostile_entities = new ArrayList<>();
        all_friendly_entities = new ArrayList<>();
        drivable_entities = new ArrayList<>();
        all_entities = new ArrayList<>();
        renderList = new ArrayList<>();
        health_circles = new ArrayList<>();
        teleport_circles = new ArrayList<>();
        items = new ArrayList<>();
        randomItemDropper = new RandomItemDropper();
        keyInputHandler = new KeyInputHandler();
        try {
            explosion_sound = new Sound("audio/sounds/explosion.ogg");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        if (ZuluAssault.nextLevelName.isEmpty()) {
            // this is called only once at startup
            LevelManager.init(stateBasedGame);
            return;
        }

        String mapLocation;
        boolean isStandardLevel = isStandardLevel(ZuluAssault.nextLevelName);
        if (isStandardLevel) {
            mapLocation = DEFAULT_MAP_FOLDER;
        } else {
            // custom map
            mapLocation = LevelEditor.CUSTOM_MAPS_FOLDER;
        }

        try {
            map = new TiledMap(mapLocation + ZuluAssault.nextLevelName + ".tmx");

            // set the current tilemap based on the level
            TileMapData.setMap(map);

            LevelDataStorage lds = LevelDataStorage.loadLevel(ZuluAssault.nextLevelName, isStandardLevel);
            if (lds != null) {
                reset();

                // load all items
                items.addAll(lds.getAllItems());

                // load all circles
                for (InteractionCircle interactionCircle : lds.getAllCircles()) {
                    if (interactionCircle instanceof HealthCircle) {
                        health_circles.add((HealthCircle) interactionCircle);
                    } else if (interactionCircle instanceof TeleportCircle) {
                        teleport_circles.add((TeleportCircle) interactionCircle);
                    }
                }

                // load all entities
                for (Entity entity : lds.getAllEntities()) {
                    if (entity instanceof MovableEntity) {
                        MovableEntity movableEntity = (MovableEntity) entity;
                        if (movableEntity.isDrivable) {
                            drivable_entities.add(movableEntity);
                            if (movableEntity.isMandatory) {
                                all_hostile_entities.add(entity);   // mandatory hostile entities can also be shot
                            }
                            continue;
                        }
                    }
                    if (entity.isHostile) {
                        all_hostile_entities.add(entity);
                    } else {
                        all_friendly_entities.add(entity);
                    }
                }

                // load all waypoint entities
                for (MovableEntity movableEntity : lds.getAllWaypointEntities()) {
                    if (movableEntity.isHostile) {
                        all_hostile_entities.add(movableEntity);
                    } else {
                        all_friendly_entities.add(movableEntity);
                    }
                }

                // load the player
                MovableEntity playerEntity = lds.getPlayerEntity(false);
                if (playerEntity instanceof Aircraft) {
                    ((Aircraft) playerEntity).setStarting();
                }
                player.init(playerEntity);

                if (!initOnce) {
                    initOnce = true;
                    // init things here that are same for each map and thus only need to be initialized once
                    collisionHandler = new CollisionHandler();
                    bigExplosionAnimation = new BigExplosionAnimation(100);
                    screenDrawer = new ScreenDrawer();
                    TileMapData.init();
                    hud = new HUD(player, gameContainer);
                    player.addListener(hud);
                    radar = new Radar(gameContainer, player);
                }

                // set the combat music based on the level
                combatBackgroundMusic.setIdx(lds.musicIdx);

                // check if the map size has changed - if so, inform the radar about it
                boolean map_size_changed = TileMapData.updateMapSize();
                if (map_size_changed) radar.updateMapSize();

                // init time manager for each level
                TimeManager.init();

                // create a global movable entity list for collisions between them
                all_entities.addAll(all_friendly_entities);
                all_entities.addAll(all_hostile_entities);
                renderList.addAll(all_entities);    // TODO: 25.10.2020 why are drivable_entities not in render list?
                all_entities.addAll(drivable_entities);

                // put planes at the end of the list so they get rendered on top of other entities
                renderList.sort((o1, o2) -> {
                    if (o1 instanceof Aircraft && o2 instanceof Aircraft) return 0;
                    else if (o1 instanceof Aircraft) return 1;
                    else if (o2 instanceof Aircraft) return -1;
                    else return 0;
                });

                camera = new Camera(gameContainer, map);
                camera.centerOn(player.getEntity().getPosition().x, player.getEntity().getPosition().y);

                // add listeners for destruction of entities
                for (Entity entity : all_friendly_entities) {
                    entity.addListeners(this);
                }
                for (Entity entity : all_hostile_entities) {
                    entity.addListeners(this);
                }
                for (Entity entity : drivable_entities) {
                    entity.addListeners(this);
                }
                player.getEntity().addListeners(this);
                player.getBaseSoldier().addListeners(this);

            } else {
                // has no level data storage -> can't play the level
                stateBasedGame.enterState(ZuluAssault.MAIN_MENU);
            }
        } catch (SlickException e) {
            System.out.println("Error: Could not load tmx map file.");
            stateBasedGame.enterState(ZuluAssault.MAIN_MENU);
            //e.printStackTrace();
        }
    }

    @Override
    public void enter(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        collisionHandler.setPlayer(player);
        keyInputHandler.setPlayer(player);
        combatBackgroundMusic.start();

        /*
        if (ZuluAssault.prevState.getID() == ZuluAssault.MAIN_MENU) {
            // game.player is resuming the level
        }
         */
    }

    @Override
    public void notifyForEntityDestruction(Entity entity) {
        if (entity.isHostile) {
            all_hostile_entities.remove(entity);
            if (entity instanceof MovableEntity) {
                if (((MovableEntity) entity).isDrivable) {
                    drivable_entities.remove(entity);
                }
            }
            player.addPoints(entity.getScoreValue());  // add points
            screenDrawer.drawScoreValue(5, entity);    // draw the score on the screen
        } else {
            if (entity instanceof MovableEntity) {
                if (entity == player.getEntity()) {
                    // THE PLAYER DIED
                    LevelManager.gameOver(this);
                } else {
                    all_friendly_entities.remove(entity);
                    if (((MovableEntity) entity).isDrivable) {
                        drivable_entities.remove(entity);
                    }
                }
            }
        }
        if (entity instanceof Robot) camera.shake();
        if (entity instanceof Soldier) screenDrawer.drawDeadSoldierBody(10, entity);
        else {
            this.damageGroundTile(entity.getPosition().x, entity.getPosition().y);
            bigExplosionAnimation.playTenTimes(entity.getPosition().x, entity.getPosition().y, 0);
            explosion_sound.play(1.f, UserSettings.soundVolume);
        }

        // maybe drop an item
        Item drop_item = randomItemDropper.dropItem(entity.getPosition());
        if (drop_item != null) {
            items.add(drop_item);
        }
        // remove entity from other relevant lists
        renderList.remove(entity);
        all_entities.remove(entity);
    }

    @Override
    public void damageGroundTile(float xPos, float yPos) {
        // damage a ground tile
        int mapX = (int) (xPos / TILE_WIDTH);
        int mapY = (int) (yPos / TILE_HEIGHT);
        int tileID = map.getTileId(mapX, mapY, LANDSCAPE_TILES_LAYER_IDX);

        int replacement_tile_id = TileMapData.getReplacementTileID(tileID);
        if (replacement_tile_id != -1) {
            map.setTileId(mapX, mapY, DESTRUCTION_TILES_LAYER_IDX, replacement_tile_id);
        }
        // maybe damage other ground tiles that are around, too
        doCollateralTileDamage(mapX, mapY);
    }

    public static boolean isStandardLevel(String s_name) {
        if (s_name.length() > 4) {
            if (s_name.substring(0, 4).equals("map_")) {
                // official map
                int levelID = Integer.parseInt(s_name.substring(4));
                if (levelID <= ZuluAssault.MAX_LEVEL && levelID >= 1) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void leave(GameContainer var1, StateBasedGame var2) {
        TileMapData.reset();
        combatBackgroundMusic.stop();
        ZuluAssault.prevState = this;
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {
        camera.drawMap();
        camera.translateGraphics();
        for (InteractionCircle health_circle : health_circles) {
            health_circle.draw(graphics);
        }
        for (InteractionCircle teleport_circle : teleport_circles) {
            teleport_circle.draw(graphics);
        }
        for (Item item : items) {
            item.draw(graphics);
        }

        screenDrawer.draw();    // draws dead bodies and score values

        /* --------------------- draw all entities --------------------- */

        for (MovableEntity drivableEntity : drivable_entities) {
            drivableEntity.draw(graphics);
        }

        if (!(player.getEntity() instanceof Aircraft)) player.getEntity().draw(graphics);

        for (Entity renderInstance : renderList) {
            renderInstance.draw(graphics);
        }

        bigExplosionAnimation.draw();
        collisionHandler.draw();

        if (player.getEntity() instanceof Aircraft) player.getEntity().draw(graphics);

        // un-translate game.graphics to draw the HUD-items
        camera.untranslateGraphics();
        hud.draw(gameContainer);
        radar.draw(graphics);
    }

    @Override
    public void update(GameContainer gc, StateBasedGame stateBasedGame, int dt) throws SlickException {
        if (hasWonTheLevel) {
            // notify TimeManager that the level is finished
            TimeManager.finishLevel();
            // enter debriefing
            stateBasedGame.enterState(ZuluAssault.DEBRIEFING,
                    new FadeOutTransition(), new FadeInTransition());
            return;
        }
        player.update(gc, dt);
        for (int idx = 0; idx < all_entities.size(); ++idx) {
            all_entities.get(idx).update(gc, dt);
        }

        for (InteractionCircle health_circle : health_circles) {
            health_circle.update(gc, dt);
        }
        for (InteractionCircle teleport_circle : teleport_circles) {
            teleport_circle.update(gc, dt);
        }
        for (Item item : items) {
            item.update(gc, dt);
        }
        keyInputHandler.update(gc, dt);
        collisionHandler.update(dt);
        hud.update(dt);
        radar.update(dt);
        screenDrawer.update(dt);
        bigExplosionAnimation.update(dt);
        camera.centerOn(player.getEntity().getPosition().x, player.getEntity().getPosition().y);
        camera.update(dt);
        TimeManager.update(dt);

        if (gc.getInput().isKeyPressed(Input.KEY_ESCAPE)) {  // paused the game
            Menu.goToMenu(Menu.STATE_IN_GAME_MENU);
            stateBasedGame.enterState(ZuluAssault.MAIN_MENU,
                    new FadeOutTransition(), new FadeInTransition());
            ZuluAssault.prevState = this;
        }

        combatBackgroundMusic.update();
        hasWonTheLevel = checkWon();
    }

    private boolean checkWon() {
        for (Entity entity : all_entities) {
            if (entity.isMandatory) return false;
        }
        for (Item item : items) {
            if (item.isMandatory) return false;
        }
        return true;
    }

    @Override
    public void keyReleased(int key, char c) {
        keyInputHandler.onKeyRelease(key);
    }

    private static void reset() {
        if (player.getEntity() == null) return;
        all_hostile_entities.clear();
        all_friendly_entities.clear();
        drivable_entities.clear();
        all_entities.clear();
        renderList.clear();
        health_circles.clear();
        teleport_circles.clear();
        items.clear();
        Radar.hideRadar();
        hasWonTheLevel = false;
    }

    public static void prepareNewPlayThrough() {
        player.reset();
        hud.reset();
    }

    @Override
    public int getID() {
        return ZuluAssault.IN_LEVEL;
    }

}
